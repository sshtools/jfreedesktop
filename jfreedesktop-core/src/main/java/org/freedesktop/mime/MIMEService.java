package org.freedesktop.mime;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.FreedesktopService;

public interface MIMEService extends FreedesktopService<MIMEEntry> {
	MIMEEntry getEntryForMimeType(String mimeType);

	MIMEEntry getMimeTypeForFile(FileObject file, boolean useMagic) throws IOException;


	MIMEEntry getMimeTypeForPattern(String pattern)
			throws MagicRequiredException;

	String getDefaultExtension(MIMEEntry mimeEntry);

	Collection<String> getExtensionsForMimeType(MIMEEntry mimeEntry);

	AliasEntry getAliasEntryForAlias(String alias);

}
