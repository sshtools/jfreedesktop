package org.freedesktop.wallpapers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractTheme;
import org.freedesktop.util.INIFile;

public class Wallpaper extends AbstractTheme {

	private static final String WALLPAPER = "Wallpaper";
	private static final String FILE = "File";
	private static final String IMAGE_TYPE = "ImageType";
	private static final String AUTHOR = "Author";

	private String imageType;
	private String author;
	private FileObject themeFile;

	public String getImageType() {
		return imageType;
	}

	public String getAuthor() {
		return author;
	}

	public Wallpaper(FileObject... base) throws IOException, ParseException {
		super(WALLPAPER, base);
		for (FileObject b : base) {
			FileObject f = b.getParent().resolveFile(b.getName() + ".desktop");
			if (f.exists()) {
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
		if (!file.equals(getBases().iterator().next().getName())) {
			throw new ParseException("Unexpected file " + file, 0);
		}
		imageType = themeProperties.getProperty(IMAGE_TYPE);
		author = themeProperties.getProperty(AUTHOR);
	}

	@Override
	protected void load() throws IOException, ParseException {
		if (themeFile.exists()) {
			load(themeFile);
		} else {
			loadDefaults();
		}
	}
}
