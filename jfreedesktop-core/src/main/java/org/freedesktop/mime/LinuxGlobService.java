/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
