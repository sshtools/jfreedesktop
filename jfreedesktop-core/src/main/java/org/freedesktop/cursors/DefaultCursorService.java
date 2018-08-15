package org.freedesktop.cursors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractThemeService;
import org.freedesktop.util.Log;

/**
 * Default implementations of an {@link CursorService}. Four image types are
 * supported. PNG, GIF, XPM and SVG.
 * <p>
 * Icon locations are also cached in memory, keyed on
 * [iconName]_[preferredSize]. 
 */
public class DefaultCursorService extends AbstractThemeService<CursorTheme> implements CursorService {

	public File findIcon(String name) throws IOException {
		return null;
	}

	public Collection<CursorTheme> getEntities(FileObject base) {
		return bases.get(base);
	}

	protected Collection<CursorTheme> scanBase(FileObject base) throws IOException {
		List<CursorTheme> themes = new ArrayList<CursorTheme>();
		for (FileObject dir : listDirs(base)) {
			FileObject cursorTheme = dir.resolveFile("cursor.theme");
			FileObject cursorsDir = dir.resolveFile("cursors");
			if (cursorTheme.exists() || cursorsDir.exists()) {
				try {
					themes.add(new CursorTheme(dir));
				} catch (FileNotFoundException fnfe) {
					// Skip
					Log.debug("Skipping " + dir + " because index.theme is missing.");
				} catch (IOException ioe) {
					Log.warn("Invalid theme directory " + dir.getName().getPath() + "." + ioe.getMessage());
				} catch (ParseException ioe) {
					Log.warn("Invalid theme definition in " + dir.getName().getPath() + ". " + ioe.getMessage());
				}
			} else {
				// Skip
				Log.debug("Skipping " + dir + " because it is an icon theme.");
			}
		}
		return themes;
	}
}
