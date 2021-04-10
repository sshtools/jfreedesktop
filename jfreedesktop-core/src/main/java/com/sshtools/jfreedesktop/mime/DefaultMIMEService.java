/**
 * Copyright © 2006 - 2021 SSHTOOLS Limited (support@sshtools.com)
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
package com.sshtools.jfreedesktop.mime;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.sshtools.jfreedesktop.AbstractFreedesktopService;
import com.sshtools.jfreedesktop.util.Log;

public class DefaultMIMEService extends AbstractFreedesktopService<MIMEEntry> implements MIMEService {
	private Map<Path, MimeBase> mimeBases = new TreeMap<Path, MimeBase>(new PathComparator());
	private GlobService globService;
	private AliasService aliasService;
	private MagicService magicService;

	public DefaultMIMEService() {
	}

	public DefaultMIMEService(GlobService globService, AliasService aliasService, MagicService magicService) {
		this.globService = globService;
		this.aliasService = aliasService;
		this.magicService = magicService;
	}

	public AliasEntry getAliasEntryForAlias(String alias) {
		if (aliasService == null) {
			throw new IllegalStateException("No alias service configured.");
		}
		return aliasService.getAliasEntryForAlias(alias);
	}

	@Override
	protected Collection<MIMEEntry> scanBase(Path base) throws IOException {
		Path[] d = listDirs(base);
		MimeBase mimeBase = new MimeBase();
		mimeBases.put(base, mimeBase);
		if (d != null) {
			for (Path dir : d) {
				String family = dir.getFileName().toString();
				if (!family.equals("packages")) {
					Log.debug("Scanning family " + family);
					try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
						@Override
						public boolean accept(Path entry) throws IOException {
							return entry.getFileName().toString().toLowerCase().endsWith(".xml");
						}
					})) {
						for (Path type : stream) {
							String typeName = type.getFileName().toString().substring(0,
									type.getFileName().toString().length() - 4);
							MIMEEntry entry = new MIMEEntry(family, typeName, type);
							Log.debug("    Adding type " + entry.getInternalName());
							mimeBase.byType.put(entry.getInternalName(), entry);
						}
					}
				}
			}
		}
		return mimeBase.byType.values();
	}

	public void removeBase(Path base) {
		super.removeBase(base);
		mimeBases.remove(base);
	}

	public MIMEEntry getMimeTypeForFile(Path file, boolean useMagic) throws IOException {
		// Directories are always inode/directory
		if (Files.isDirectory(file)) {
			return getEntryForMimeType("inode/directory");
		}
		// First try matching using glob pattterns
		try {
			MIMEEntry mimeTypeForPattern = getMimeTypeForPattern(file.toString());
			if (mimeTypeForPattern != null) {
				return mimeTypeForPattern;
			}
		} catch (MagicRequiredException mre) {
			Log.debug("Conflicting match, magic required");
			// Try and get exact match using magic
			if (useMagic) {
				for (GlobEntry ge : mre.getAlternatives()) {
					MagicEntry me = magicService.getEntity(ge.getInternalName());
					if (me == null) {
						Log.debug("NO Mime Entry for " + ge.getInternalName());
					}
					if (me != null && me.match(file)) {
						MIMEEntry entity = getEntity(me.getInternalName());
						if (entity != null) {
							Log.debug("Will use " + entity.getName());
							return entity;
						}
					}
				}
			}
			// Return the first one we have a mime entry for
			for (GlobEntry ge : mre.getAlternatives()) {
				Log.debug("Trying " + ge.getInternalName());
				MIMEEntry me = getEntity(ge.getInternalName());
				if (me == null) {
					Log.debug("NO Mime Entry for " + ge.getInternalName());
				} else {
					Log.debug("Will use " + me.getInternalName());
					return me;
				}
			}
		}
		/*
		 * If the glob matching fails or results in multiple conflicting
		 * mimetypes, read the contents of the file and do magic sniffing on it.
		 */
		//
		// Log.warn("Slow magic search for " + file.getName());
//		for (MagicEntry me : magicService.getAllEntities()) {
//			if (me.match(file)) {
//				return getEntity(me.getInternalName());
//			}
//		}
		if (useMagic) {
			return checkForTextOrBinary(file);
		} else {
			return getEntity("application/octet-stream");
		}
	}

	private MIMEEntry checkForTextOrBinary(Path file) throws FileSystemException, IOException {
		/*
		 * If no magic rule matches the data (or if the content is not
		 * available), use the default type of application/octet-stream for
		 * binary data, or text/plain for textual data. If there was no glob
		 * match the magic match as the result.
		 */
		
		try(InputStream in = Files.newInputStream(file)) {
			byte[] buf = new byte[(int) Math.min(32l, Files.size(file))];
			DataInputStream din = new DataInputStream(in);
			din.readFully(buf);
			for (byte b : buf) {
				if (b < 32) {
					return getEntity("application/octet-stream");
				}
			}
		} 
		return getEntity("text/plain");
	}

	public MIMEEntry getMimeTypeForPattern(String text) throws MagicRequiredException {
		GlobEntry globEntry = globService.match(text);
		if (globEntry != null) {
			MIMEEntry entry = getEntryForMimeType(globEntry.getInternalName());
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	public MIMEEntry getEntryForMimeType(String mimeType) {
		for (Path base : getBasesInReverse()) {
			AliasEntry alias = aliasService == null ? null : aliasService.getAliasEntryForMimeType(mimeType);
			MimeBase mimeBase = mimeBases.get(base);
			MIMEEntry entry = mimeBase.byType.get(alias != null ? alias.getAlias() : mimeType);
			if (alias != null && entry == null) {
				// Just in case the alias was bad
				entry = mimeBase.byType.get(mimeType);
			}
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	public String getDefaultExtension(MIMEEntry mimeEntry) {
		GlobEntry entry = globService.getByMimeType(mimeEntry.getInternalName());
		return entry == null ? null : entry.getPatterns().iterator().next();
	}

	public Collection<String> getExtensionsForMimeType(MIMEEntry mimeEntry) {
		GlobEntry entry = globService.getByMimeType(mimeEntry.getInternalName());
		return entry == null ? null : entry.getPatterns();
	}

	class MimeBase {
		Map<String, MIMEEntry> byType = new HashMap<String, MIMEEntry>();
	}
}
