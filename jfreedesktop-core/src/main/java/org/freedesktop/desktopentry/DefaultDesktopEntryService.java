package org.freedesktop.desktopentry;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.util.Log;
import org.freedesktop.wallpapers.WallpaperService;

/**
 * Default implementations of an {@link WallpaperService}.
 */

public class DefaultDesktopEntryService extends AbstractFreedesktopService<DesktopEntry> {

	protected Collection<DesktopEntry> scanBase(FileObject base) throws IOException {
		List<DesktopEntry> entries = new ArrayList<DesktopEntry>();
		for (FileObject file : listEntries(base)) {
			try {
				entries.add(new DesktopEntry(file));
			} catch (IOException ioe) {
				Log.warn("Invalid desktop entry directory " + file.getName().getPath() + ". " + ioe.getMessage());
			} catch (ParseException ioe) {
				Log.warn("Invalid desktop entry in " + file.getName().getPath() + ". " + ioe.getMessage());
			}
		}
		return entries;
	}

	protected FileObject[] listEntries(FileObject dir) throws IOException {
		FileObject[] files = dir.findFiles(new DesktopSelector());
		if (files == null) {
			throw new IOException("Directory could not be read.");
		}
		return files;
	}

	protected final class DesktopSelector implements FileSelector {

		public boolean includeFile(FileSelectInfo info) throws Exception {
			return info.getFile().getName().getBaseName().endsWith(".desktop");
		}

		public boolean traverseDescendents(FileSelectInfo info) throws Exception {
			return info.getDepth() == 0;
		}

	}
}
