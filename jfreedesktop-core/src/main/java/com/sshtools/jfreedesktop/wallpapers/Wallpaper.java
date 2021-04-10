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
package com.sshtools.jfreedesktop.wallpapers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Properties;

import com.sshtools.jfreedesktop.themes.AbstractTheme;
import com.sshtools.jfreedesktop.util.INIFile;

public class Wallpaper extends AbstractTheme {

	private static final String WALLPAPER = "Wallpaper";
	private static final String FILE = "File";
	private static final String IMAGE_TYPE = "ImageType";
	private static final String AUTHOR = "Author";

	private String imageType;
	private String author;
	private Path themeFile;

	public String getImageType() {
		return imageType;
	}

	public String getAuthor() {
		return author;
	}

	public Wallpaper(Path... base) throws IOException, ParseException {
		super(WALLPAPER, base);
		for (Path b : base) {
			Path f = b.getParent().resolve(b.toString() + ".desktop");
			if (Files.exists(f)) {
				themeFile = f;
				break;
			}
		}
		if (themeFile == null) {
			throw new FileNotFoundException();
		}
	}

	public void initFromThemeProperties(INIFile iniFile, Properties themeProperties) throws ParseException {
		String file = themeProperties.getProperty(FILE);
		if (!file.equals(getBases().iterator().next().toString())) {
			throw new ParseException("Unexpected file " + file, 0);
		}
		imageType = themeProperties.getProperty(IMAGE_TYPE);
		author = themeProperties.getProperty(AUTHOR);
	}

	@Override
	protected void load() throws IOException, ParseException {
		if (Files.exists(themeFile)) {
			load(themeFile);
		} else {
			loadDefaults();
		}
	}
}
