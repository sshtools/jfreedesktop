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
