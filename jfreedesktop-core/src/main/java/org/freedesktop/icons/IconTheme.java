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
package org.freedesktop.icons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.themes.AbstractTheme;
import org.freedesktop.util.INIFile;

public class IconTheme extends AbstractTheme {

	private static final String ICON_THEME = "Icon Theme";
	private static final String DIRECTORIES = "Directories";
	private static final String INHERITS = "Inherits";
	private static final String HIDDEN = "Hidden";

	private List<String> inherits;
	private boolean hidden;
	private List<Directory> directories;
	private FileObject themeFile;

	public IconTheme(FileObject... base) throws IOException {
		super(ICON_THEME, base);
		for (FileObject b : base) {
			FileObject f = b.resolveFile("index.theme");
			if (f.exists()) {
				themeFile = f;
				break;
			}
		}
		if (themeFile == null) {
			throw new FileNotFoundException();
		}
	}

	public boolean isHidden() {
		checkLoaded();
		return hidden;
	}

	public void initFromThemeProperties(INIFile iniFile, Properties themeProperties)
			throws IOException, ParseException {
		inherits = new ArrayList<String>();
		directories = new ArrayList<Directory>();

		if (themeProperties.containsKey(INHERITS)) {
			StringTokenizer t = new StringTokenizer(themeProperties.getProperty(INHERITS), ",");
			while (t.hasMoreTokens()) {
				inherits.add(t.nextToken());
			}
		}

		if (!themeProperties.containsKey(DIRECTORIES)) {
			if (inherits.size() == 0) {
				throw new ParseException("Directories entry is required when no theme is inherited.", 0);
			}
		} else {
			StringTokenizer t = new StringTokenizer(themeProperties.getProperty(DIRECTORIES), ",");
			while (t.hasMoreTokens()) {
				String directoryName = t.nextToken();
				Properties directoryProperties = iniFile.get(directoryName);
				if (directoryProperties == null) {
					throw new ParseException(
							"Entry '" + directoryName + "' in Directories does not have a corresponding section.", 0);
				}
				directories.add(new Directory(this, directoryName, directoryProperties));
			}
		}

		hidden = "true".equalsIgnoreCase(themeProperties.getProperty(HIDDEN));
	}

	public boolean isParents() {
		checkLoaded();
		return inherits.size() > 0;
	}

	public Collection<String> getParents() {
		checkLoaded();
		return inherits;
	}

	public Collection<Directory> getDirectories() {
		checkLoaded();
		return directories;
	}

	public FileObject lookupIcon(String icon, int size) throws IOException {
		checkLoaded();
		for (Directory directory : getDirectories()) {
			if (directory.isMatchesSize(size)) {
				FileObject file = directory.findIcon(icon);
				if (file != null) {
					return file;
				}
			}
		}

		int minimalSize = Integer.MAX_VALUE;
		FileObject closestFile = null;
		FileObject firstFile = null;
		for (Directory directory : getDirectories()) {
			for (String ext : DefaultIconService.SUPPORTED_EXTENSIONS) {
				for (FileObject base : getBases()) {
					FileObject file = base.resolveFile(directory.getKey() + File.separator + icon + "." + ext);
					int directorySizeDistance = directorySizeDistance(directory, size);
					if (file.exists()) {
						if (directorySizeDistance < minimalSize) {
							closestFile = file;
							minimalSize = directorySizeDistance;
						} else {
							if (firstFile == null) {
								firstFile = file;
							}
						}
					}
				}
			}
		}

		return closestFile == null ? firstFile : closestFile;
	}

	int directorySizeDistance(Directory directory, int iconsize) {
		if (directory.getType().equals(Directory.Type.fixed)) {
			return Math.abs(directory.getSize() - iconsize);
		}
		if (directory.getType().equals(Directory.Type.scalable)) {
			if (iconsize < directory.getMinSize()) {
				return directory.getMinSize() - iconsize;
			}
			if (iconsize > directory.getMaxSize()) {
				return iconsize - directory.getMaxSize();
			}
		}
		if (directory.getType().equals(Directory.Type.threshold)) {
			if (iconsize < directory.getSize() - directory.getThreshold()) {
				return directory.getMinSize() - iconsize;
			}
			if (iconsize > directory.getSize() + directory.getThreshold()) {
				return iconsize - directory.getMaxSize();
			}
		}
		return 0;

	}

	@Override
	protected void load() throws IOException, ParseException {
		load(themeFile);
	}
}
