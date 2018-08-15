package org.freedesktop.cursors;

import java.io.File;
import java.io.IOException;

import org.freedesktop.themes.ThemeService;

/**
 * Implementations of this service interface provide an easy way for Java to use
 * the <a href="http://www.freedesktop.org/wiki/">freedesktop.org</a>'s cursor
 * themes.
 */
public interface CursorService extends ThemeService<CursorTheme> {
	/**
	 * Find the cursor icon. See the class description for {@link CursorService}
	 * for a complete description of the search algorithm. <code>null</code>
	 * will only be returned if no matching icon can be found.
	 * 
	 * @param name name of icon
	 * @return file icon file
	 * @throws IOException on I/O error
	 */
	File findIcon(String name) throws IOException;
}