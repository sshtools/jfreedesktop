/**
 * Copyright © 2006 - 2020 SSHTOOLS Limited (support@sshtools.com)
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
package org.freedesktop.wallpapers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.freedesktop.themes.AbstractThemeService;
import org.freedesktop.util.Log;

/**
 * Default implementations of an {@link WallpaperService}.
 */
public class DefaultWallpaperService extends AbstractThemeService<Wallpaper> implements WallpaperService {
	protected Collection<Wallpaper> scanBase(Path base) throws IOException {
		List<Wallpaper> themes = new ArrayList<Wallpaper>();
		for (Path dir : listImages(base)) {
			try {
				themes.add(new Wallpaper(dir));
			} catch (IOException ioe) {
				Log.warn("Invalid wallpaper directory " + dir.toString() + ". " + ioe.getMessage());
			} catch (ParseException ioe) {
				Log.warn("Invalid wallpaper definition in " + dir.toString() + ". " + ioe.getMessage());
			}
		}
		return themes;
	}

	protected Path[] listImages(Path dir) throws IOException {
		List<Path> l = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, new ImageSelector())) {
			for (Path type : stream) {
				l.add(type);
			}
		}
		return l.toArray(new Path[0]);
	}

	protected final class ImageSelector implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			String name = entry.getFileName().toString().toLowerCase();
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif");
		}
	}
}
