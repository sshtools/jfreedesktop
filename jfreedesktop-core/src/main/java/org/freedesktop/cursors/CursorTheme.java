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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractTheme;
import org.freedesktop.util.INIFile;

public class CursorTheme extends AbstractTheme {

	private static final String CURSOR_THEME = "Cursor Theme";
	private FileObject themeFile;

	public CursorTheme(FileObject base) throws IOException, ParseException {
		super(CURSOR_THEME, base);
		themeFile = base.resolveFile("index.theme");
		if (!themeFile.exists()) {
			throw new FileNotFoundException("index.theme not found");
		}
	}

	public void initFromThemeProperties(INIFile iniFile, Properties themeProperties) throws IOException {
	}

	@Override
	protected void load() throws IOException, ParseException {
		load(themeFile);
	}
}
