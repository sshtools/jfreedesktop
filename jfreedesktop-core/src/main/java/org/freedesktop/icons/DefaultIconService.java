package org.freedesktop.icons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractThemeService;
import org.freedesktop.util.Log;

/**
 * Default implementations of an {@link IconService}. Four image types are
 * supported. PNG, GIF, XPM and SVG.
 * <p>
 * Icon locations are also cached in memory, keyed on
 * [iconName]_[preferredSize]. Use {@link #clearCache()} to remove all cached
 * locations.
 */
public class DefaultIconService extends AbstractThemeService<IconTheme> implements IconService {

	/**
	 * Support file extensions
	 */
	public final static String[] SUPPORTED_EXTENSIONS = { "png", "svg", "gif", "xpm" };

	// Private instance variables
	private Map<String, FileObject> cache = new HashMap<String, FileObject>();

	private boolean returnMissingImage = true;
	private Map<String, String> existing = new TreeMap<String, String>();
	protected String defaultThemeName = null;

	public DefaultIconService() throws IOException, ParseException {
		super();
	}

	public void setDefaultThemeName(String defaultThemeName) {
		this.defaultThemeName = defaultThemeName;
	}

	public final void postInit() {
		IconTheme theme = getSelectedTheme();
		if (theme == null) {
			theme = getDefaultTheme();
		}
		if (theme != null) {
			setSelectedTheme(theme);
		}
	}

	protected IconTheme getDefaultTheme() {
		if (defaultThemeName != null) {
			IconTheme theme = getEntity(defaultThemeName);
			if (theme != null) {
				return theme;
			}
		}
		FileObject firstBase = bases.keySet().isEmpty() ? null : bases.keySet().iterator().next();
		return firstBase == null ? null : bases.get(firstBase).iterator().next();
	}

	public void addBase(FileObject base) throws IOException {
		super.addBase(base);
		clearCache();
	}

	public void removeBase(FileObject base) {
		super.removeBase(base);
		clearCache();
	}

	public void setSelectedTheme(IconTheme theme) {
		super.setSelectedTheme(theme);
		clearCache();
		preCache();
	}

	public Iterator<FileObject> icons(int size) {
		return new IconIterator(size);
	}

	public Iterator<String> iconNames() {
		return existing.keySet().iterator();
	}

	public FileObject findIcon(String name, int size) throws IOException {
		String key = name + "_" + size;
		FileObject file = null;
		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		// if(!existing.containsKey(name)) {
		// return returnMissing(name, size, key, new
		// IOException("Precache didn't find"));
		// }

		IconTheme theme = getSelectedTheme();
		if (theme != null) {
			file = findIconHelper(name, size, theme);
			if (file != null) {
				cache.put(key, file);
				return file;
			}
		}
		try {
			file = lookupFallbackIcon(name);
			if (file != null) {
				cache.put(key, file);
				return file;
			}
		} catch (IOException ioe) {
			return returnMissing(name, size, key, ioe);
		}

		return null;
	}

	private FileObject returnMissing(String name, int size, String key, IOException ioe) throws IOException {
		FileObject file = null;
		if (!name.equals("image-missing") && returnMissingImage) {
			file = findIcon("image-missing", size);
			if (file != null) {
				cache.put(key, file);
			}
		} else {
			if (returnMissingImage) {
				throw ioe;
			}
		}
		return file;
	}

	public boolean isIconExists(String name, int size) throws IOException {
		String key = name + "_" + size;
		FileObject file = cache.get(key);
		;
		if (file != null && file.exists()) {
			return true;
		}
		IconTheme theme = getSelectedTheme();
		if (theme != null) {
			file = findIconHelper(name, size, theme);
			if (file != null) {
				cache.put(key, file);
				return true;
			}
		}
		try {
			file = lookupFallbackIcon(name);
			if (file != null) {
				cache.put(key, file);
				return true;
			}
		} catch (IOException ioe) {
		}
		return false;
	}

	public void setReturnMissingImage(boolean returnMissingImage) {
		this.returnMissingImage = returnMissingImage;
	}

	public void clearCache() {
		existing.clear();
		cache.clear();
	}

	void preCache() {
		// Log.warn("Precaching, may take a while");
		// long started = System.currentTimeMillis();
		// IconTheme selectedTheme = getSelectedTheme();
		// List<String> extensions = Arrays.asList(SUPPORTED_EXTENSIONS);
		// if (selectedTheme != null) {
		// List<IconTheme> toPreCache = new ArrayList<IconTheme>();
		// addParents(selectedTheme, toPreCache);
		// for (IconTheme theme : toPreCache) {
		// for (Directory dir : theme.getDirectories()) {
		// File dirFile = new File(theme.getBase(), dir.getKey());
		// File[] images = dirFile.listFiles();
		// if (images != null) {
		// for (File image : images) {
		// String imageName = image.getName();
		// int lidx = imageName.lastIndexOf('.');
		// if (lidx != -1) {
		// String ext = imageName.substring(lidx + 1);
		// if (extensions.contains(ext)) {
		// existing.put(imageName.substring(0, lidx), ext);
		// }
		// }
		// }
		// }
		// }
		// }
		// }
		// Log.warn("Precaching done (" + existing.size() + " took " +
		// ((System.currentTimeMillis() - started) / 1000) + ")");
	}

	void addParents(IconTheme theme, List<IconTheme> toPreCache) {
		if (!toPreCache.contains(theme)) {
			toPreCache.add(theme);
		}
		for (String parent : theme.getParents()) {
			IconTheme parentTheme = getEntity(parent);
			if (parentTheme != null) {
				addParents(parentTheme, toPreCache);
			}
		}
	}

	protected Collection<IconTheme> scanBase(FileObject base) throws IOException {
		List<IconTheme> themes = new ArrayList<IconTheme>();
		FileObject[] listDirs = listDirs(base);
		for (FileObject dir : listDirs) {
			// TODO cursor themes not supported here
			FileObject cursorTheme = dir.resolveFile("cursor.theme");
			FileObject cursorsDir = dir.resolveFile("cursors");
			if (cursorTheme.exists() || cursorsDir.exists()) {
				// Skip
				Log.debug("Skipping " + dir + " because it is a cursor theme.");
				continue;
			}

			try {
				themes.add(new IconTheme(dir));
			} catch (FileNotFoundException fnfe) {
				// Skip
				Log.debug("Skipping " + dir + " because index.theme is missing.");
			} catch (IOException ioe) {
				Log.warn("Invalid theme directory " + dir.getName().getPath() + "." + ioe.getMessage());
			}
		}
		return themes;
	}

	FileObject lookupFallbackIcon(String iconname) throws IOException {
		for (FileObject base : bases.keySet()) {
			for (String extension : SUPPORTED_EXTENSIONS) {
				FileObject f = base.resolveFile(iconname + "." + extension);
				if (f.exists()) {
					return f;
				}
			}
		}
		throw new IOException("No theme or fallback icon for " + iconname + ".");
	}

	FileObject findIconHelper(String icon, int size, IconTheme theme) throws IOException {
		FileObject filename = theme.lookupIcon(icon, size);
		if (filename != null) {
			return filename;
		}

		Collection<String> parents = null;
		if (theme.isParents()) {
			parents = theme.getParents();
		} else if (!theme.getInternalName().equals(HICOLOR)) {
			parents = Arrays.asList(new String[] { HICOLOR });
		}

		if (parents != null) {
			for (String parent : parents) {
				IconTheme parentTheme = getEntity(parent);
				if (parentTheme == null) {
					Log.debug("Parent theme " + parent + " specified in " + theme.getInternalName() + " does not exist.");
				} else {
					filename = findIconHelper(icon, size, parentTheme);
					if (filename != null) {
						return filename;
					}
				}
			}
		}
		return null;
	}

	private static final String HICOLOR = "hicolor";

	class IconIterator implements Iterator<FileObject> {

		private int size;
		private Iterator<String> icons;

		IconIterator(int size) {
			this.size = size;
			icons = existing.keySet().iterator();
		}

		public boolean hasNext() {
			return icons.hasNext();
		}

		public FileObject next() {
			String icon = icons.next();
			try {
				return findIcon(icon, size);
			} catch (IOException e) {
				throw new Error(e);
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
