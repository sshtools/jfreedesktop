/**
 * Copyright © 2006 - 2021 SSHTOOLS Limited (support@sshtools.com)
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
package com.sshtools.jfreedesktop.cursors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Properties;

import com.sshtools.jfreedesktop.themes.AbstractTheme;
import com.sshtools.jfreedesktop.util.INIFile;

public class CursorTheme extends AbstractTheme {
	private static final String CURSOR_THEME = "Cursor Theme";
	private Path themeFile;

	public CursorTheme(Path base) throws IOException, ParseException {
		super(CURSOR_THEME, base);
		themeFile = base.resolve("index.theme");
		if (!Files.exists(themeFile)) {
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
