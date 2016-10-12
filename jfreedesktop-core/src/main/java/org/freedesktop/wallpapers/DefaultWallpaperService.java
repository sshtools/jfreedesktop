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
package org.freedesktop.wallpapers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.freedesktop.themes.AbstractThemeService;
import org.freedesktop.util.Log;

/**
 * Default implementations of an {@link WallpaperService}.
 */
public class DefaultWallpaperService extends AbstractThemeService<Wallpaper> implements WallpaperService {

	protected Collection<Wallpaper> scanBase(FileObject base) throws IOException {
		List<Wallpaper> themes = new ArrayList<Wallpaper>();
		for (FileObject dir : listImages(base)) {
			try {
				themes.add(new Wallpaper(dir));
			} catch (IOException ioe) {
				Log.warn("Invalid wallpaper directory " + dir.getName().getPath() + ". " + ioe.getMessage());
			} catch (ParseException ioe) {
				Log.warn("Invalid wallpaper definition in " + dir.getName().getPath() + ". " + ioe.getMessage());
			}
		}
		return themes;
	}

	protected FileObject[] listImages(FileObject dir) throws IOException {
		FileSelector fileFilter = new ImageSelector();
		FileObject[] files = dir.findFiles(fileFilter);
		if (files == null) {
			throw new IOException("Directory could not be read.");
		}
		return files;
	}

	protected final class ImageSelector implements FileSelector {

		public boolean includeFile(FileSelectInfo info) throws Exception {
			String name = info.getFile().getName().getBaseName().toLowerCase();
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif");
		}

		public boolean traverseDescendents(FileSelectInfo info) throws Exception {
			return info.getDepth() == 0;
		}
	}
}
