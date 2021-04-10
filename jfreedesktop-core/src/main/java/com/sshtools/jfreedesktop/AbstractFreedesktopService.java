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
package com.sshtools.jfreedesktop;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementations of a {@link FreedesktopService} that provides
 * methods for maintaining the list of base directories (common to all
 * specifications).
 */
public abstract class AbstractFreedesktopService<T extends FreedesktopEntity> implements FreedesktopService<T> {
	// Private instance variables
	protected Map<Path, Collection<T>> bases = new HashMap<Path, Collection<T>>();
	protected List<Path> basesList = new ArrayList<Path>();

	public void addBase(Path base) throws IOException {
		if (!basesList.contains(base)) {
			bases.put(base, scanBase(base));
			basesList.add(base);
		}
	}

	public void removeBase(Path base) {
		bases.remove(base);
		basesList.remove(base);
	}

	public Collection<T> getEntities(Path base) {
		return bases.get(base);
	}

	public Collection<T> getAllEntities() {
		List<T> all = new ArrayList<T>();
		for (Path base : getBasesInReverse()) {
			all.addAll(getEntities(base));
		}
		return all;
	}

	public T getEntity(String name) {
		for (Path base : bases.keySet()) {
			for (T theme : bases.get(base)) {
				if (theme.getInternalName().equals(name)) {
					return theme;
				}
			}
		}
		return null;
	}

	public Collection<Path> getBases() {
		return basesList;
	}

	public Collection<Path> getBasesInReverse() {
		List<Path> reverseBases = new ArrayList<Path>(basesList);
		Collections.reverse(reverseBases);
		return reverseBases;
	}

	public void checkAndAddBase(Path file) throws IOException, ParseException {
		if (Files.exists(file)) {
			addBase(file);
		}
	}

	public void checkAndAddBase(File file) throws IOException, ParseException {
		if (file.exists()) {
			addBase(file.toPath());
		}
	}

	protected Path[] listDirs(Path dir) throws IOException {
		List<Path> l = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, new DirectorySelector())) {
			for (Path type : stream) {
				l.add(type);
			}
		}
		return l.toArray(new Path[0]);
	}

	protected abstract Collection<T> scanBase(Path base) throws IOException;

	class DirectorySelector implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry) && !entry.equals(entry.getRoot());
		}
	}
}
