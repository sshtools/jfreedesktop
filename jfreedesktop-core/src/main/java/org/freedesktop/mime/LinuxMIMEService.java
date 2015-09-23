package org.freedesktop.mime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.vfs2.VFS;

public class LinuxMIMEService extends DefaultMIMEService {

	public LinuxMIMEService() throws IOException, ParseException {
		super();
		init();
	}

	public LinuxMIMEService(GlobService globService, AliasService aliasService,
			MagicService magicService) throws IOException, ParseException {
		super(globService, aliasService, magicService);
		init();
	}

	private void init() throws IOException, ParseException {
		checkAndAddBase(new File(System.getProperty("user.home")
				+ File.separator + ".local" + File.separator + "share"
				+ File.separator + "mime"));
		checkAndAddBase(new File("/usr/share/mime"));
	}

	public static void main(String[] args) throws Exception {
		LinuxMagicService lms = new LinuxMagicService();
		// for(MagicEntry me : lms.getAllEntities()) {
		// System.out.println(me);
		// }
		LinuxGlobService lgs = new LinuxGlobService();
		LinuxAliasService las = new LinuxAliasService();
		LinuxMIMEService lmts = new LinuxMIMEService(lgs, las, lms);
		// for(MIMEEntry me : lmts.getAllEntities()) {
		// System.out.println(me);
		// }
		MIMEEntry ment = lmts.getMimeTypeForFile(VFS.getManager().resolveFile(
				"file:///home/tanktarta/Downloads/mcpatcher-3.0.3.jar"), true);
		System.out.println(ment);

		ment = lmts.getMimeTypeForFile(VFS.getManager().resolveFile(
				"file:///home/tanktarta/Desktop/MoonOnly.png"), true);
		System.out.println(ment);

	}

}
