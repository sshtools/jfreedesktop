package org.freedesktop.desktopentry;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.util.Log;

public class LinuxDesktopEntryService extends DefaultDesktopEntryService {

	public LinuxDesktopEntryService() throws IOException, ParseException {
		checkAndAddBase(new File("/usr/share/applications"));
	}

	public static void main(String[] args) throws Exception {
		LinuxDesktopEntryService service = new LinuxDesktopEntryService();
		for (FileObject base : service.getBases()) {
			for (DesktopEntry entry : service.getEntities(base)) {
				Log.debug("[" + entry.getInternalName() + "]");
				Log.debug("    " + entry.getComment());
				Log.debug("    " + entry.getExec());
			}
		}
	}
}
