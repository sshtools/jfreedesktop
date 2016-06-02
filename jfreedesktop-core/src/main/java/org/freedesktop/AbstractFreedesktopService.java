package org.freedesktop;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;


/**
 * Abstract implementations of a {@link FreedesktopService} that provides
 * methods for maintaining the list of base directories (common to all
 * specifications).
 */
public abstract class AbstractFreedesktopService<T extends FreedesktopEntity> implements FreedesktopService<T> {

	// Private instance variables
	protected Map<FileObject, Collection<T>> bases = new HashMap<FileObject, Collection<T>>();
	protected List<FileObject> basesList = new ArrayList<FileObject>();

	public void addBase(FileObject base) throws IOException {
		if (!basesList.contains(base)) {
			bases.put(base, scanBase(base));
			basesList.add(base);
		}
	}

	public void removeBase(FileObject base) {
		bases.remove(base);
		basesList.remove(base);
	}

	public Collection<T> getEntities(FileObject base) {
		return bases.get(base);
	}

	public Collection<T> getAllEntities() {
		List<T> all = new ArrayList<T>();
		for (FileObject base : getBasesInReverse()) {
			all.addAll(getEntities(base));
		}
		return all;
	}

	public T getEntity(String name) {
		for (FileObject base : bases.keySet()) {
			for (T theme : bases.get(base)) {
				if (theme.getInternalName().equals(name)) {
					return theme;
				}
			}
		}
		return null;
	}

	public Collection<FileObject> getBases() {
		return basesList;
	}

	public Collection<FileObject> getBasesInReverse() {
		List<FileObject> reverseBases = new ArrayList<FileObject>(basesList);
		Collections.reverse(reverseBases);
		return reverseBases;
	}

	protected void checkAndAddBase(FileObject file) throws IOException, ParseException {
		if (file.exists()) {
			addBase(file);
		}
	}

	protected void checkAndAddBase(File file) throws IOException, ParseException {
		if (file.exists()) {
			addBase(VFS.getManager().resolveFile(file.getAbsolutePath()));
		}
	}

	protected FileObject[] listDirs(FileObject dir) throws IOException {
		FileObject[] dirs = dir.findFiles(new DirectorySelector());
		if (dirs == null) {
			throw new IOException("Directory could not be read.");
		}
		return dirs;
	}

	protected abstract Collection<T> scanBase(FileObject base) throws IOException;

	class DirectorySelector implements FileSelector {

		public boolean includeFile(FileSelectInfo info) throws Exception {
			return info.getFile().getType().equals(FileType.FOLDER) && !info.getFile().equals(info.getBaseFolder());
		}
       
		public boolean traverseDescendents(FileSelectInfo info) throws Exception {
			return info.getDepth() == 0;
		}

	}

}
