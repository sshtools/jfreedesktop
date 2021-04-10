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
package com.sshtools.jfreedesktop.icons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sshtools.jfreedesktop.themes.AbstractThemeService;
import com.sshtools.jfreedesktop.util.Log;

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
	private Map<String, Path> cache = new HashMap<String, Path>();

	private boolean returnMissingImage = true;
	private Map<String, String> existing = new TreeMap<String, String>();
	protected String defaultThemeName = null;
	private Set<String> globalFallbackThemes = new HashSet<>();

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
			if (theme != null) {
				setSelectedTheme(theme);
			}
		}
	}
	
	public Set<String> getGlobalFallbackThemes() {
		return Collections.unmodifiableSet(globalFallbackThemes);
	}
	
	public void addGlobalFallbackTheme(String theme) {
		globalFallbackThemes.add(theme);
	}
	
	public void removeGlobalFallbackTheme(String theme) {
		globalFallbackThemes.add(theme);
	}

	protected IconTheme getDefaultTheme() {
		if (defaultThemeName != null) {
			IconTheme theme = getEntity(defaultThemeName);
			if (theme != null) {
				return theme;
			}
		}
		
		/* Prefer to find jfreedesktop-tango */
		for(Path base : bases.keySet()) {
			String basename = base.toString();
			String aname = base.getFileSystem().toString();
			if(basename.indexOf("jfreedesktop-tango") != -1 || aname.indexOf("jfreedesktop-tango") != -1)
				return bases.get(base).iterator().next();
		}
		
		/* Failing that, the  first theme base that is outside of the users home directory (i.e. a system one) */
		for(Path base : bases.keySet()) {
			if(!base.toString().startsWith(System.getProperty("user.home") + "/"))
				return bases.get(base).iterator().next();
		}
		
		/* Finally just the first base */
		Path firstBase = bases.keySet().isEmpty() ? null : bases.keySet().iterator().next();
		return firstBase == null ? null : bases.get(firstBase).iterator().next();
	}

	public void addBase(Path base) throws IOException {
		super.addBase(base);
		clearCache();
	}

	public void removeBase(Path base) {
		super.removeBase(base);
		clearCache();
	}

	public void setSelectedTheme(IconTheme theme) {
		super.setSelectedTheme(theme);
		clearCache();
		preCache();
	}

	public Iterator<Path> icons(int size) {
		return new IconIterator(size);
	}

	public Iterator<String> iconNames() {
		return existing.keySet().iterator();
	}

	public Path findIcon(String name, int size) throws IOException {
		String key = name + "_" + size;
		Path file = null;
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
			file = lookupFallbackIcon(name, size);
			if (file != null) {
				cache.put(key, file);
				return file;
			}
		} catch (IOException ioe) {
			return returnMissing(name, size, key, ioe);
		}

		return null;
	}

	private Path returnMissing(String name, int size, String key, IOException ioe) throws IOException {
		Path file = null;
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
		Path file = cache.get(key);
		;
		if (file != null && Files.exists(file)) {
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
			file = lookupFallbackIcon(name, size);
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

	protected Collection<IconTheme> scanBase(Path base) throws IOException {
		List<IconTheme> themes = new ArrayList<IconTheme>();
		Path[] listDirs = listDirs(base);
		for (Path dir : listDirs) {
			// TODO cursor themes not supported here
			Path cursorTheme = dir.resolve("cursor.theme");
			Path cursorsDir = dir.resolve("cursors");
			if (Files.exists(cursorTheme) || Files.exists(cursorsDir)) {
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
				Log.warn("Invalid theme directory " + dir.toString() + "." + ioe.getMessage());
			}
		}
		return themes;
	}

	Path lookupFallbackIcon(String iconname, int size) throws IOException {
		
		for (String fallback : globalFallbackThemes) {
			IconTheme parentTheme = getEntity(fallback);
			if (parentTheme == null) {
				Log.debug("Fallback theme " + fallback + " specified");
			} else {
				Path filename = findIconHelper(iconname, size, parentTheme);
				if (filename != null) {
					return filename;
				}
			}
		}
		
		for (Path base : bases.keySet()) {
			for (String extension : SUPPORTED_EXTENSIONS) {
				Path f = base.resolve(iconname + "." + extension);
				if (Files.exists(f)) {
					return f;
				}
			}
		}
		throw new IOException("No theme or fallback icon for " + iconname + ".");
	}

	Path findIconHelper(String icon, int size, IconTheme theme) throws IOException {
		Path filename = theme.lookupIcon(icon, size);
		if (filename != null) {
			return filename;
		}

		Collection<String> parents = new ArrayList<String>();
		if (theme.isParents()) {
			parents.addAll(theme.getParents());
		} 
		
		if (!theme.getInternalName().equals(HICOLOR) && !parents.contains(HICOLOR)) {
			parents.add(HICOLOR);
		}

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
		
		return null;
	}

	private static final String HICOLOR = "hicolor";

	class IconIterator implements Iterator<Path> {

		private int size;
		private Iterator<String> icons;

		IconIterator(int size) {
			this.size = size;
			icons = existing.keySet().iterator();
		}

		public boolean hasNext() {
			return icons.hasNext();
		}

		public Path next() {
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
