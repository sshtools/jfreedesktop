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
package org.freedesktop.desktopentry;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.util.Log;
import org.freedesktop.wallpapers.WallpaperService;

/**
 * Default implementations of an {@link DesktopEntryService}.
 */
public class DefaultDesktopEntryService extends AbstractFreedesktopService<DesktopEntry> implements DesktopEntryService {
	protected Collection<DesktopEntry> scanBase(Path base) throws IOException {
		List<DesktopEntry> entries = new ArrayList<DesktopEntry>();
		for (Path file : listEntries(base)) {
			try {
				entries.add(new DesktopEntry(file));
			} catch (IOException ioe) {
				Log.warn("Invalid desktop entry directory " + file.toString() + ". " + ioe.getMessage());
			} catch (ParseException ioe) {
				Log.warn("Invalid desktop entry in " + file.toString() + ". " + ioe.getMessage());
			}
		}
		return entries;
	}

	protected Path[] listEntries(Path dir) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, new DesktopSelector())) {
			List<Path> l = new ArrayList<>();
			for (Path file : stream)
				l.add(file);
			return l.toArray(new Path[0]);
		}
	}

	class DesktopSelector implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			return entry.getFileName().toString().endsWith(".desktop");
		}
	}
}
