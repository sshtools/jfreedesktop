/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.freedesktop.mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.AbstractFreedesktopService;

public class DefaultGlobService extends AbstractFreedesktopService<GlobEntry>
		implements GlobService {

	private Map<FileObject, GlobBase> globBases = new TreeMap<FileObject, GlobBase>(
			new FileObjectComparator());

	@Override
	protected Collection<GlobEntry> scanBase(FileObject base)
			throws IOException {
		FileObject f = base.resolveFile("globs");
		GlobBase globBase = new GlobBase();
		globBases.put(base, globBase);
		InputStream fin = f.getContent().getInputStream();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fin));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.equals("") && !line.startsWith("#")) {
					int idx = line.indexOf(':');
					if (idx == -1) {
						throw new IOException(f + " contains invalid data '"
								+ line + "'.");
					}
					String mimeType = line.substring(0, idx);
					String pattern = line.substring(idx + 1);

					// A single mime type may have several patterns
					GlobEntry entry = globBase.byType.get(mimeType);
					if (entry == null) {
						entry = new GlobEntry(mimeType);
						globBase.byType.put(mimeType, entry);
					}
					entry.addPattern(pattern);

					// Provide a quick lookup table for simple patterns and
					// explicit names
					if (isSimplePattern(pattern) || !isExpression(pattern)) {
						ArrayList<GlobEntry> entries = globBase.byPattern
								.get(pattern);
						if (entries == null) {
							entries = new ArrayList<GlobEntry>();
							globBase.byPattern.put(pattern, entries);
						}
						if (!entries.contains(entry)) {
							entries.add(entry);
						}
					}
				}
			}
		} finally {
			fin.close();
		}
		return globBase.byType.values();
	}

	public void removeBase(FileObject base) {
		super.removeBase(base);
		globBases.remove(base);
	}

	public GlobEntry match(String text) throws MagicRequiredException {
		for (FileObject base : getBasesInReverse()) {
			GlobEntry entry = match(base, text);
			if (entry != null) {
				return entry;
			} else {
				entry = match(base, text.toLowerCase());
				if (entry != null) {
					return entry;
				}
			}
		}
		return null;
	}

	GlobEntry match(FileObject base, String text) throws MagicRequiredException {
		// First lookup explicit pattern
		GlobBase globBase = globBases.get(base);
		if (globBase.byPattern.containsKey(text)) {
			ArrayList<GlobEntry> arrayList = globBase.byPattern.get(text);
			if (arrayList.size() > 1) {
				throw new MagicRequiredException(
						"Explicit pattern matches more than one entry, magic required.",
						arrayList);
			}
			return arrayList.get(0);
		}

		// Now try lookup based on the longest available filename extension
		int idx = text.indexOf('.');
		while (idx > -1) {
			String extension = text.substring(idx + 1);
			String key = "*." + extension;
			if (globBase.byPattern.containsKey(key)) {
				ArrayList<GlobEntry> arrayList = globBase.byPattern.get(key);
				if (arrayList.size() > 1) {
					throw new MagicRequiredException(
							"Extension pattern matches more than one entry, magic required.",
							arrayList);
				}
				return arrayList.get(0);
			}
			idx = text.indexOf('.', idx + 1);
		}

		// Still no match, look at all the entries using regexp. matching
		// against each one
		GlobEntry match = null;
		for (GlobEntry entry : getEntities(base)) {
			for (String pattern : entry.getPatterns()) {
				if (text.matches(globToRE(pattern))) {
					if (match == null) {
						match = entry;
					} else {
						throw new MagicRequiredException(
								"Regular expression pattern matches more than one entry, magic required.",
								match, entry);
					}
				}
			}
		}
		return match;
	}

	/**
	 * Converts a Unix-style glob to a regular expression.
	 * <p>
	 * ? becomes ., * becomes .*, {aa,bb} becomes (aa|bb).
	 * 
	 * @param glob
	 *            The glob pattern
	 */
	public static String globToRE(String glob) {
		// TODO this is GPL code - replace
		final Object NEG = new Object();
		final Object GROUP = new Object();
		Stack<Object> state = new Stack<Object>();

		StringBuffer buf = new StringBuffer();
		boolean backslash = false;

		for (int i = 0; i < glob.length(); i++) {
			char c = glob.charAt(i);
			if (backslash) {
				buf.append('\\');
				buf.append(c);
				backslash = false;
				continue;
			}

			switch (c) {
			case '\\':
				backslash = true;
				break;
			case '?':
				buf.append('.');
				break;
			case '.':
			case '+':
			case '(':
			case ')':
				buf.append('\\');
				buf.append(c);
				break;
			case '*':
				buf.append(".*");
				break;
			case '|':
				if (backslash)
					buf.append("\\|");
				else
					buf.append('|');
				break;
			case '{':
				buf.append('(');
				if (i + 1 != glob.length() && glob.charAt(i + 1) == '!') {
					buf.append('?');
					state.push(NEG);
				} else
					state.push(GROUP);
				break;
			case ',':
				if (!state.isEmpty() && state.peek() == GROUP)
					buf.append('|');
				else
					buf.append(',');
				break;
			case '}':
				if (!state.isEmpty()) {
					buf.append(")");
					if (state.pop() == NEG)
						buf.append(".*");
				} else
					buf.append('}');
				break;
			default:
				buf.append(c);
			}
		}

		return buf.toString();
	}

	private boolean isSimplePattern(String pattern) {
		if (!pattern.startsWith("*.")) {
			return false;
		}
		return !isExpression(pattern.substring(2));
	}

	private boolean isExpression(String pattern) {
		char ch;
		boolean found = false;
		for (int i = pattern.length() - 1; i >= 0 && !found; i--) {
			ch = pattern.charAt(i);
			switch (ch) {
			case '*':
			case '?':
			case '[':
			case ']':
			case '`':
			case '\'':
				found = true;
				break;
			}
		}
		return found;
	}

	private Collection<GlobEntry> bySimplePattern(String pattern) {
		if (!isSimplePattern(pattern)) {
			throw new IllegalArgumentException(
					"Only simple patterns are cached.");
		}
		for (FileObject base : getBasesInReverse()) {
			Collection<GlobEntry> entries = globBases.get(base).byPattern
					.get(pattern);
			if (entries != null) {
				return entries;
			}
		}
		return null;
	}

	public GlobEntry getByMimeType(String mimeType) {
		for (FileObject base : getBasesInReverse()) {
			GlobEntry entry = globBases.get(base).byType.get(mimeType);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	class GlobBase {
		Map<String, GlobEntry> byType = new HashMap<String, GlobEntry>();
		Map<String, ArrayList<GlobEntry>> byPattern = new HashMap<String, ArrayList<GlobEntry>>();
	}

}
