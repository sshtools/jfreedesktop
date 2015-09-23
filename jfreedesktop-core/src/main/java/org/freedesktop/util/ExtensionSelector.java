/**
 * 
 */
package org.freedesktop.util;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;


public final class ExtensionSelector implements FileSelector {

	private String extension;

	public ExtensionSelector(String extension) {
		this.extension = "." + extension.toLowerCase();
	}

	public boolean includeFile(FileSelectInfo info) throws Exception {
		return info.getFile().getName().getBaseName().toLowerCase().endsWith(extension);
	}

	public boolean traverseDescendents(FileSelectInfo info) throws Exception {
		return info.getDepth() == 0;
	}
}