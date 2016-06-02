/**
 * 
 */
package org.freedesktop.icons;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.util.ExtensionSelector;

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
	private IconTheme theme;
	private Map<String, FileObject> cache = new HashMap<String, FileObject>();

	public Directory(IconTheme theme, String key, Properties properties) throws ParseException, IOException {
		this.key = key;
		this.theme = theme;

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

		for (FileObject base : theme.getBases()) {
			FileObject dirBase = base.resolveFile(getKey());
			// Loop over the supported extensions so we get files in supported
			// extension order
			for (String extension : DefaultIconService.SUPPORTED_EXTENSIONS) {
				FileObject[] files = dirBase.findFiles(new ExtensionSelector(extension));
				if (files != null) {
					for (FileObject file : files) {
						String name = file.getName().getBaseName();
						int lidx = name.lastIndexOf('.');
						String basename = name.substring(0, lidx);
						if (!cache.containsKey(basename)) {
							cache.put(basename, file);
						}
					}
				}
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

	public FileObject findIcon(String icon) {
		return cache.get(icon);
	}

	@Override
	public String toString() {
		return "Directory [size=" + size + ", type=" + type + ", maxSize=" + maxSize + ", minSize=" + minSize + ", key="
				+ key + ", threshold=" + threshold + "]";
	}

}