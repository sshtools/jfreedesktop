/**
 * Copyright © 2006 - 2020 SSHTOOLS Limited (support@sshtools.com)
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.ParseException;

import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.util.Util;

public class BuiltInMIMEService extends DefaultMIMEService {
	public BuiltInMIMEService() throws IOException, ParseException {
		super();
		init();
	}

	public BuiltInMIMEService(GlobService globService, AliasService aliasService, MagicService magicService)
			throws IOException, ParseException {
		super(globService, aliasService, magicService);
		init();
	}

	private void init() throws IOException, ParseException {
		addJarMimeSet(this, "default-mime");
	}

	public static void addJarMimeSet(AbstractFreedesktopService<?> srv, String set) throws IOException, ParseException {
		URL loc = srv.getClass().getClassLoader().getResource(set + "/types");
		Path obj = null;
		if (loc != null) {
			try {
				obj = Util.resourceToPath(loc);
			} catch (URISyntaxException e) {
			}
		}
		if (obj != null) {
			URI uri = obj.toUri();
			String uriStr = uri.toString();
			int idx = uriStr.lastIndexOf('!');
			if (idx != -1) {
				uriStr = uriStr.substring(idx + 1);
				if (uriStr.endsWith("/types"))
					uriStr = uriStr.substring(0, uriStr.length() - 6);
			} else
				uriStr = null;
			if (uri.getScheme().equals("jar")) {
				for (Path r : obj.getFileSystem().getRootDirectories()) {
					if (uriStr != null)
						r = r.resolve(uriStr);
					srv.checkAndAddBase(r);
				}
			} else if (uri.getScheme().equals("file")) {
				srv.checkAndAddBase(obj.getParent());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		BuiltInMagicService lms = new BuiltInMagicService();
		// for(MagicEntry me : lms.getAllEntities()) {
		// System.out.println(me);
		// }
		BuiltInGlobService lgs = new BuiltInGlobService();
		BuiltInAliasService las = new BuiltInAliasService();
		BuiltInMIMEService lmts = new BuiltInMIMEService(lgs, las, lms);
		// for(MIMEEntry me : lmts.getAllEntities()) {
		// System.out.println(me);
		// }
		MIMEEntry ment = lmts.getMimeTypeForFile(new File("pom.xml").toPath(), true);
		System.out.println(ment);
		ment = lmts.getMimeTypeForFile(new File("MoonOnly.png").toPath(), true);
		System.out.println(ment);
	}
}
