package org.freedesktop.mime;

import org.freedesktop.FreedesktopService;

public interface AliasService extends FreedesktopService<AliasEntry> {
    public AliasEntry getAliasEntryForMimeType(String mimeType);
    public AliasEntry getAliasEntryForAlias(String mimeType);

}
