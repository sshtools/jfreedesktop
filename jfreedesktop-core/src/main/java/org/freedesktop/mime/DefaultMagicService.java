/**
 * Copyright © 2006 - 2018 SSHTOOLS Limited (support@sshtools.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freedesktop.mime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.mime.MagicEntry.Pattern;

public class DefaultMagicService extends AbstractFreedesktopService<MagicEntry> implements MagicService {
	private Map<Path, MagicBase> magicBases = new TreeMap<Path, MagicBase>(new PathComparator());

	@Override
	protected Collection<MagicEntry> scanBase(Path base) throws IOException {
		Path f = base.resolve("magic");
		MagicBase aliasBase = new MagicBase();
		magicBases.put(base, aliasBase);
		InputStream fin = Files.newInputStream(f);
		try {
			byte[] buf = new byte[65536];
			StringBuilder bui = new StringBuilder();
			int state = -1;
			// -1 outside of format
			// 0 file header
			// 1 in header (priority)
			// 2 in header (mime type)
			// 3 out of header, waiting for newline
			// 4 waiting for indent
			// 5 waiting for offset
			// 6 waiting for value length (hi)
			// 7 waiting for value length (lo)
			// 8 reading value
			// 9 read value, waiting for optional mask
			// 10 reading mask
			// 11 word-size
			// 12 range-length
			// 13 done
			MagicEntry entry = null;
			Pattern pattern = null;
			int valueLength = 0;
			int bufIdx = 0;
			while (true) {
				int read = fin.read(buf);
				if (read == -1) {
					break;
				}
				for (int i = 0; i < read; i++) {
					byte b = buf[i];
					// System.out.println((int)b + "[" + (char)b + "] = " +
					// state);
					if (state == -1) {
						if (b == '\n') {
							state = 0;
							if (!bui.toString().equals("MIME-Magic\0")) {
								throw new IOException("No MIME-Magic header");
							}
							bui.setLength(0);
						} else {
							bui.append((char) b);
						}
					} else if (state == 0) {
						if (b == '[') {
							state = 1;
						}
					} else if (state == 1) {
						if (b == ':') {
							entry = new MagicEntry();
							entry.setPriority(Integer.parseInt(bui.toString()));
							bui.setLength(0);
							state = 2;
						} else {
							bui.append((char) b);
						}
					} else if (state == 2) {
						if (b == ']') {
							entry.setMIMEType(bui.toString());
							bui.setLength(0);
							state = 3;
							aliasBase.byType.put(entry.getInternalName(), entry);
						} else {
							bui.append((char) b);
						}
					} else if (state == 3) {
						if (b == '\n') {
							state = 4;
							pattern = new MagicEntry.Pattern();
						}
					} else if (state == 4) {
						if (b == '[') {
							state = 1;
						} else if (b == '>') {
							state = 5;
							if (bui.length() > 0) {
								String str = bui.toString();
								pattern.setIndent(Integer.parseInt(str));
								bui.setLength(0);
							}
						} else {
							bui.append((char) b);
						}
					} else if (state == 5) {
						if (b == '=') {
							state = 6;
							pattern.setOffset(Long.parseLong(bui.toString()));
							bui.setLength(0);
						} else {
							bui.append((char) b);
						}
					} else if (state == 6) {
						valueLength = b << 8;
						state = 7;
					} else if (state == 7) {
						valueLength = valueLength | b;
						state = 8;
						pattern.setValueLength(valueLength);
						bufIdx = 0;
					} else if (state == 8) {
						pattern.getValue()[bufIdx++] = b;
						if (bufIdx == valueLength) {
							state = 9;
						}
					} else if (state == 9) {
						if (b == '&') {
							bufIdx = 0;
							state = 10;
						} else if (b == '~') {
							state = 11;
						} else if (b == '+') {
							state = 12;
						} else if (b == '\n') {
							state = 13;
						}
					} else if (state == 10) {
						pattern.getMask()[bufIdx++] = b;
						if (bufIdx == valueLength) {
							state = 9;
						}
					} else if (state == 11) {
						pattern.setWordSize(Integer.parseInt(String.valueOf((char) b)));
						state = 9;
					} else if (state == 12) {
						if (b == '\n') {
							pattern.setRangeLength(Integer.parseInt(bui.toString()));
							bui.setLength(0);
							state = 13;
						} else {
							bui.append((char) b);
						}
					}
					// Done
					if (state == 13) {
						entry.add(pattern);
						pattern = new MagicEntry.Pattern();
						state = 4;
					}
				}
			}
		} finally {
			fin.close();
		}
		// Sort the patterns in the base
		for (MagicEntry e : aliasBase.byType.values()) {
			Collections.sort(e);
		}
		return aliasBase.byType.values();
	}

	public void removeBase(Path base) {
		super.removeBase(base);
		magicBases.remove(base);
	}

	class MagicBase {
		Map<String, MagicEntry> byType = new HashMap<String, MagicEntry>();
	}

	public MagicEntry getMagicEntryForMimeType(String mimeType) {
		for (Path base : getBasesInReverse()) {
			MagicEntry entry = magicBases.get(base).byType.get(mimeType);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}
}
