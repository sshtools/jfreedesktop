package org.freedesktop.cursors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractTheme;
import org.freedesktop.util.INIFile;

public class CursorTheme extends AbstractTheme {

	private static final String ICON_THEME = "Icon Theme";
	private FileObject themeFile;

	public CursorTheme(FileObject base) throws IOException, ParseException {
		super(base, ICON_THEME);
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
