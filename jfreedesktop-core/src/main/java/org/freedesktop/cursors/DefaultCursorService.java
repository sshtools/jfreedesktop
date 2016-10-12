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
 * [iconName]_[preferredSize]. Use {@link #clearCache()} to remove all cached
 * locations.
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
