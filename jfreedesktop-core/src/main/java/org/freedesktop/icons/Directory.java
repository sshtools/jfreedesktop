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
/**
 * 
 */
package org.freedesktop.icons;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.freedesktop.util.ExtensionSelector;
import org.freedesktop.util.Log;

public class Directory {
	public enum Type {
		fixed, scalable, threshold;
	}

	private static final String THRESHOLD = "Threshold";
	private static final String MIN_SIZE = "MinSize";
	private static final String MAX_SIZE = "MaxSize";
	private static final String TYPE = "Type";
	private static final String CONTEXT = "Context";
	private static final String SIZE = "Size";
	private int size;
	private String context;
	private Type type = Type.threshold;
	private int maxSize;
	private int minSize;
	private String key;
	private int threshold = 2;
	private Map<String, Path> cache = new HashMap<String, Path>();

	public Directory(IconTheme theme, String key, Properties properties) throws ParseException, IOException {
		this.key = key;
		if (!properties.containsKey(SIZE)) {
			throw new ParseException("Size entry is required.", 0);
		}
		size = Integer.parseInt(properties.getProperty(SIZE));
		context = properties.getProperty(CONTEXT);
		if (properties.containsKey(TYPE)) {
			String typeName = properties.getProperty(TYPE).toLowerCase();
			try {
				type = Type.valueOf(typeName);
			} catch (IllegalArgumentException iae) {
				throw new ParseException("Invalid Type ' " + typeName + "' in " + key, 0);
			}
		}
		if (properties.containsKey(MAX_SIZE)) {
			maxSize = Integer.parseInt(properties.getProperty(MAX_SIZE));
		} else {
			maxSize = size;
		}
		if (properties.containsKey(MIN_SIZE)) {
			minSize = Integer.parseInt(properties.getProperty(MIN_SIZE));
		} else {
			minSize = size;
		}
		if (properties.containsKey(THRESHOLD)) {
			minSize = Integer.parseInt(properties.getProperty(THRESHOLD));
		}
		for (Path base : theme.getBases()) {			
			Path dirBase = base.resolve(getKey());

			if (Files.exists(dirBase)) {
				// Loop over the supported extensions so we get files in
				// supported
				// extension order
				for (String extension : DefaultIconService.SUPPORTED_EXTENSIONS) {
					try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirBase, new ExtensionSelector(extension))) {
						for (Path file : stream) {
							String name = file.getFileName().toString();
							int lidx = name.lastIndexOf('.');
							String basename = name.substring(0, lidx);
							if (!cache.containsKey(basename)) {
								cache.put(basename, file);
							}
						}
					}
				}
			} else {
				Log.debug(String.format("No directory %s", dirBase));
			}
		}
	}

	public int getSize() {
		return size;
	}

	public String getContext() {
		return context;
	}

	public Type getType() {
		return type;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getMinSize() {
		return minSize;
	}

	public String getKey() {
		return key;
	}

	public int getThreshold() {
		return threshold;
	}

	public boolean isMatchesSize(int iconsize) {
		if (getType().equals(Directory.Type.fixed)) {
			return getSize() == iconsize;
		}
		if (getType().equals(Directory.Type.scalable)) {
			return getMinSize() <= iconsize && iconsize <= getMaxSize();
		}
		if (getType().equals(Directory.Type.threshold)) {
			return getSize() - getThreshold() <= iconsize && iconsize <= getSize() + getThreshold();
		}
		return false;
	}

	public Path findIcon(String icon) {
		return cache.get(icon);
	}

	@Override
	public String toString() {
		return "Directory [size=" + size + ", type=" + type + ", maxSize=" + maxSize + ", minSize=" + minSize + ", key=" + key
				+ ", threshold=" + threshold + "]";
	}
}