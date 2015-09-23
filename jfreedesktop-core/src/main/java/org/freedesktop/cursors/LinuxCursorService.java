package org.freedesktop.cursors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Extension of {@link DefaultCursorService} that could be used on a Linux
 * system. Adds <i>/usr/share/icons</i> and <i>${HOME}/.icons</i> as default
 * base directories.
 * <p>
 * Tested on Ubuntu 8.04 LTS.
 */
public class LinuxCursorService extends DefaultCursorService {

	public LinuxCursorService() throws IOException, ParseException {
		checkAndAddBase(new File("/usr/share/icons"));
		checkAndAddBase(new File(System.getProperty("user.home") + File.separator + ".icons"));
	}

	public static void main(String[] args) throws Exception {
		new LinuxCursorService();
	}
}
