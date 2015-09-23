package org.freedesktop.mime;

import java.util.Comparator;

import org.apache.commons.vfs2.FileObject;

public final class FileObjectComparator implements Comparator<FileObject> {
	public int compare(FileObject o1, FileObject o2) {
		return o1.getName().compareTo(o2.getName());
	}
}