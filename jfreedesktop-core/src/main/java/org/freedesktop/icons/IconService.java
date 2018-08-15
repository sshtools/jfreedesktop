package org.freedesktop.icons;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.ThemeService;

/**
 * Implementations of this service interface provide an easy way for Java to use
 * the <a href="http://www.freedesktop.org/wiki/">freedesktop.org</a>'s <i><a
 * href=
 * "http://standards.freedesktop.org/icon-theme-spec/icon-theme-spec-latest.html"
 * Icon Theme Specification</a></i>.
 * <p>
 * To use the API, you must add (in the order that the locations should be
 * searched) at lease one <i>Base</i> directory using the {@link #addBase(FileObject)}
 * method.
 * <p>
 * You should also select a theme using {@link ThemeService#setSelectedTheme(IconTheme)}. If
 * no theme is selected, the first theme found is used.
 * <p>
 * To find an icon file, use {@link #findIcon(String, int)}. Only supply the
 * filename without the prefix (for example, <i>user-home</i>, not
 * <i>user-home.png</i>). Specify a size in pixels to locate the icon that
 * closest matches (use {@link Integer#MAX_VALUE} to get the biggest available
 * and {@link Integer#MIN_VALUE} to get the smallest).
 * <p>
 * Themes are keyed by their <i>internal name</i>. This is the file name of the
 * directory the theme definition file is located in.
 * <p>
 * If an icon is not found in the selected theme, the themes parent will then be
 * searched, and then its parent etc until a theme with no parent is reached.
 * <p>
 * If the icon is not found, the <i>hicolor</i> theme will then be searched in
 * the same manner. Finally, if the icon is still not found, then the root of
 * each base directory will be searched for any image files that are prefixed
 * with the required name.
 * <p>
 * When no icon file is found using any of the above methods,
 * {@link #findIcon(String, int)} will either return <code>null</code> or the
 * location of the <i>image-missing</i> special image (if that can be found).
 * <p>
 * See the specification in the link above for a complete description of the
 * search algorithm.
 */
public interface IconService extends ThemeService<IconTheme> {
	/**
	 * Must be called after construction to set the initial theme.
	 */
	void postInit();

	/**
	 * Find the closest matching icon. See the class description for
	 * {@link IconService} for a complete description of the search algorithm.
	 * <code>null</code> will only be returned if no matching icon can be found
	 * and the <i>image-missing</i> icon cannot be found either
	 * 
	 * @param name name of icon
	 * @param size closest pixel size
	 * @return file icon file
	 * @throws IOException on I/O error
	 */
	FileObject findIcon(String name, int size) throws IOException;

	/**
	 * Find the closest matching icon. See the class description for
	 * {@link IconService} for a complete description of the search algorithm.
	 * 
	 * @param name name of icon
	 * @param size closest pixel size
	 * @return file or <code>null</code> if no icon can be found
	 * @throws IOException
	 */
	boolean isIconExists(String name, int size) throws IOException;

	/**
	 * Set whether the special <i>Missing</i> image is returned when no other
	 * matches is found. If <code>true</code> and this image isn't found, an
	 * {@link IOException} will be thrown.
	 * 
	 * @param b return <i>Missing</i> image
	 */
	void setReturnMissingImage(boolean b);

	/**
	 * Iterator over all the available icons in the currently selected theme for
	 * a given size.
	 * 
	 * @param size preferred size
	 * @return icon iterator
	 */
	Iterator<FileObject> icons(int size);

	/**
	 * Iterator over all the available icon names in the currently selected
	 * theme.
	 * 
	 * @return icon iterator
	 */
	Iterator<String> iconNames();
}