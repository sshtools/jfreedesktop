package org.freedesktop.wallpapers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Extension of {@link DefaultWallpaperService} that could be used on a Linux
 * system. Adds <i>/usr/share/wallpapers</i> as a default base directory.
 * <p>
 * Tested on Ubuntu 8.04 LTS.
 */
public class LinuxWallpaperService extends DefaultWallpaperService {

    public LinuxWallpaperService() throws IOException, ParseException {
        checkAndAddBase(new File("/usr/share/wallpapers"));
    }
}
