package org.freedesktop.mime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class LinuxGlobService extends DefaultGlobService {

	public LinuxGlobService() throws IOException, ParseException {
		super();
		init();
	}
	
	private void init() throws IOException, ParseException {
		checkAndAddBase(new File(System.getProperty("user.home")
				+ File.separator + ".local" + File.separator + "share" + File.separator + "mime"));
		checkAndAddBase(new File("/usr/share/mime"));
	}
}
