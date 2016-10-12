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
package org.freedesktop.desktopentry;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.freedesktop.AbstractFreedesktopService;
import org.freedesktop.util.Log;
import org.freedesktop.wallpapers.WallpaperService;

/**
 * Default implementations of an {@link WallpaperService}.
 */

public class DefaultDesktopEntryService extends AbstractFreedesktopService<DesktopEntry> implements DesktopEntryService {

	protected Collection<DesktopEntry> scanBase(FileObject base) throws IOException {
		List<DesktopEntry> entries = new ArrayList<DesktopEntry>();
		for (FileObject file : listEntries(base)) {
			try {
				entries.add(new DesktopEntry(file));
			} catch (IOException ioe) {
				Log.warn("Invalid desktop entry directory " + file.getName().getPath() + ". " + ioe.getMessage());
			} catch (ParseException ioe) {
				Log.warn("Invalid desktop entry in " + file.getName().getPath() + ". " + ioe.getMessage());
			}
		}
		return entries;
	}

	protected FileObject[] listEntries(FileObject dir) throws IOException {
		FileObject[] files = dir.findFiles(new DesktopSelector());
		if (files == null) {
			throw new IOException("Directory could not be read.");
		}
		return files;
	}

	protected final class DesktopSelector implements FileSelector {

		public boolean includeFile(FileSelectInfo info) throws Exception {
			return info.getFile().getName().getBaseName().endsWith(".desktop");
		}

		public boolean traverseDescendents(FileSelectInfo info) throws Exception {
			return info.getDepth() == 0;
		}

	}
}
