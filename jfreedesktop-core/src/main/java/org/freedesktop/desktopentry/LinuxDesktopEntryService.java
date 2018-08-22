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
package org.freedesktop.desktopentry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import org.freedesktop.util.Log;

public class LinuxDesktopEntryService extends DefaultDesktopEntryService {
	public LinuxDesktopEntryService() throws IOException, ParseException {
		checkAndAddBase(new File("/usr/share/applications"));
	}

	public static void main(String[] args) throws Exception {
		LinuxDesktopEntryService service = new LinuxDesktopEntryService();
		for (Path base : service.getBases()) {
			for (DesktopEntry entry : service.getEntities(base)) {
				Log.debug("[" + entry.getInternalName() + "]");
				Log.debug("    " + entry.getComment());
				Log.debug("    " + entry.getExec());
			}
		}
	}
}
