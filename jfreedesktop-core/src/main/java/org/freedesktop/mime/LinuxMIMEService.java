/**
 * Copyright © 2006 - 2018 SSHTOOLS Limited (support@sshtools.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freedesktop.mime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class LinuxMIMEService extends DefaultMIMEService {
	public LinuxMIMEService() throws IOException, ParseException {
		super();
		init();
	}

	public LinuxMIMEService(GlobService globService, AliasService aliasService, MagicService magicService)
			throws IOException, ParseException {
		super(globService, aliasService, magicService);
		init();
	}

	private void init() throws IOException, ParseException {
		checkAndAddBase(new File(
				System.getProperty("user.home") + File.separator + ".local" + File.separator + "share" + File.separator + "mime"));
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
		MIMEEntry ment = lmts.getMimeTypeForFile(new File("test.jar").toPath(), true);
		System.out.println(ment);
		ment = lmts.getMimeTypeForFile(new File("MoonOnly.png").toPath(), true);
		System.out.println(ment);
	}
}
