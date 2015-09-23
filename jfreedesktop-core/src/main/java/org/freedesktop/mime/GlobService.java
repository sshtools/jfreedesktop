package org.freedesktop.mime;

import org.freedesktop.FreedesktopService;

public interface GlobService extends FreedesktopService<GlobEntry> {
    public GlobEntry match(String pattern) throws MagicRequiredException;
    public GlobEntry getByMimeType(String mimeType);

}
