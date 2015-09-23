package org.freedesktop.mime;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.util.Log;

public class DefaultMIMEService extends AbstractFreedesktopService<MIMEEntry>
		implements MIMEService {

	private Map<FileObject, MimeBase> mimeBases = new TreeMap<FileObject, MimeBase>(
			new FileObjectComparator());
	private GlobService globService;
	private AliasService aliasService;
	private MagicService magicService;

	public DefaultMIMEService() {
	}

	public DefaultMIMEService(GlobService globService,
			AliasService aliasService, MagicService magicService) {
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
	protected Collection<MIMEEntry> scanBase(FileObject base)
			throws IOException {
		FileObject[] d = listDirs(base);
		MimeBase mimeBase = new MimeBase();
		mimeBases.put(base, mimeBase);
		if (d != null) {
			for (FileObject dir : d) {
				String family = dir.getName().getBaseName();
				if (!family.equals("packages")) {
					Log.debug("Scanning family " + family);
					FileObject[] t = dir.findFiles(new FileSelector() {
						public boolean traverseDescendents(FileSelectInfo info)
								throws Exception {
							return info.getDepth() == 0;
						}

						public boolean includeFile(FileSelectInfo info)
								throws Exception {
							return info.getFile().getName().getBaseName()
									.toLowerCase().endsWith(".xml");
						}
					});
					for (FileObject type : t) {
						String typeName = type
								.getName()
								.getBaseName()
								.substring(
										0,
										type.getName().getBaseName().length() - 4);
						MIMEEntry entry = new MIMEEntry(family, typeName, type);
						Log.debug("    Adding type " + entry.getInternalName());
						mimeBase.byType.put(entry.getInternalName(), entry);
					}
				}
			}
		}
		return mimeBase.byType.values();
	}

	public void removeBase(FileObject base) {
		super.removeBase(base);
		mimeBases.remove(base);
	}

	public MIMEEntry getMimeTypeForFile(FileObject file, boolean useMagic)
			throws IOException {
		// Directories are always inode/directory
		try {
			if (file.getType().equals(FileType.FOLDER)) {
				return getEntryForMimeType("inode/directory");
			}
		} catch (FileSystemException e) {
			throw new Error(e);
		}

		// First try matching using glob pattterns
		try {
			MIMEEntry mimeTypeForPattern = getMimeTypeForPattern(file.getName()
					.getPath());
			if (mimeTypeForPattern != null) {
				return mimeTypeForPattern;
			}
		} catch (MagicRequiredException mre) {
			Log.debug("Conflicting match, magic required");

			// Try and get exact match using magic
			if (useMagic) {
				for (GlobEntry ge : mre.getAlternatives()) {
					MagicEntry me = magicService
							.getEntity(ge.getInternalName());
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
				if (me != null) {
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
		// Log.warn("Slow magic search  for " + file.getName());
		// for (MagicEntry me : magicService.getAllEntities()) {
		// if (me.match(file)) {
		// return getEntity(me.getInternalName());
		// }
		// }

		if(useMagic) {
			return checkForTextOrBinary(file);
		}
		else {
			return getEntity("application/octet-stream");
		}
	}

	private MIMEEntry checkForTextOrBinary(FileObject file)
			throws FileSystemException, IOException {
		/*
		 * If no magic rule matches the data (or if the content is not
		 * available), use the default type of application/octet-stream for
		 * binary data, or text/plain for textual data. If there was no glob
		 * match the magic match as the result.
		 */

		InputStream in = file.getContent().getInputStream();
		try {
			byte[] buf = new byte[(int) Math.min(32l, file.getContent()
					.getSize())];
			DataInputStream din = new DataInputStream(in);
			din.readFully(buf);
			for (byte b : buf) {
				if (b < 32) {
					return getEntity("application/octet-stream");
				}
			}
		} finally {
			in.close();
		}

		return getEntity("text/plain");
	}

	public MIMEEntry getMimeTypeForPattern(String text)
			throws MagicRequiredException {
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
		for (FileObject base : getBasesInReverse()) {
			AliasEntry alias = aliasService == null ? null : aliasService
					.getAliasEntryForMimeType(mimeType);
			MimeBase mimeBase = mimeBases.get(base);
			MIMEEntry entry = mimeBase.byType.get(alias != null ? alias
					.getAlias() : mimeType);
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
		GlobEntry entry = globService
				.getByMimeType(mimeEntry.getInternalName());
		return entry == null ? null : entry.getPatterns().iterator().next();
	}

	public Collection<String> getExtensionsForMimeType(MIMEEntry mimeEntry) {
		GlobEntry entry = globService
				.getByMimeType(mimeEntry.getInternalName());
		return entry == null ? null : entry.getPatterns();
	}

	class MimeBase {
		Map<String, MIMEEntry> byType = new HashMap<String, MIMEEntry>();
	}
}
