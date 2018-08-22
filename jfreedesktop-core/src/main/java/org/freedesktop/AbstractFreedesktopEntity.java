/**
 * Copyright © 2006 - 2018 SSHTOOLS Limited (support@sshtools.com)
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
package org.freedesktop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.freedesktop.util.INIFile;
import org.freedesktop.util.Log;

public abstract class AbstractFreedesktopEntity implements FreedesktopResource {

	private static final String NAME = "Name";
	private static final String COMMENT = "Comment";

	protected String example;

	private List<Path> bases = new ArrayList<Path>();
	private Properties entityProperties;
	private String internalName;
	private String entityTypeName;
	private boolean loaded;

	public AbstractFreedesktopEntity(String entityTypeName, Path... base) {
		this.entityTypeName = entityTypeName;
		init(base);
	}

	public String getInternalName() {
		return internalName;
	}

	public String getName() {
		return getName((String) null);
	}

	public String getName(Locale locale) {
		return getName(locale == null ? (String) null : locale.getLanguage());
	}

	public String getName(String language) {
		checkLoaded();
		String name = getLocalisableField(NAME, language);
		if (name == null) {
			name = getInternalName();
		}
		return name;
	}

	public String getComment() {
		return getComment((String) null);
	}

	public String getComment(Locale locale) {
		return getComment(locale == null ? (String) null : locale.getLanguage());
	}

	public String getComment(String language) {
		return getLocalisableField(COMMENT, language);
	}

	public void load(Path file) throws IOException, ParseException {
		InputStream in = Files.newInputStream(file);
		try {
			load(in);
		} finally {
			in.close();
			loaded = true;
		}
	}

	protected void loadDefaults() {
		entityProperties = new Properties();
	}

	protected boolean isLoaded() {
		return loaded;
	}

	public void load(InputStream in) throws IOException, ParseException {
		INIFile iniFile = new INIFile();
		iniFile.load(in);
		entityProperties = iniFile.get(entityTypeName);
		if (entityProperties == null) {
			throw new ParseException("No '" + entityTypeName + "' section.", 0);
		}
		initFromProperties(iniFile, entityProperties);
	}

	protected abstract void initFromProperties(INIFile iniFile, Properties properties) throws IOException, ParseException;

	public List<Path> getBases() {
		return bases;
	}

	public Path getBase() {
		return bases.get(0);
	}

	protected String getLocalisableField(String key, String language) {
		checkLoaded();
		String name = null;
		if(entityProperties == null) {
			// Failed to load
			Log.warn("No entity properties!");
			return key;
		}
		if (language != null) {
			name = entityProperties.getProperty(key + "[" + language + "]");
		}
		if (name == null) {
			name = entityProperties.getProperty(key);
		}
		return name;
	}

	protected void init(Path... bases) {
		if(bases.length == 0)
			throw new IllegalArgumentException("Entity must have at least one base.");
		this.bases.addAll(Arrays.asList(bases));
		String base = null;
		for(Path s : bases) {
			if(base == null) {
				base = s.getFileName().toString();
			}
			else if(!base.equals(s.getFileName().toString())) {
				throw new IllegalArgumentException("All bases must have the same filename.");				
			}
		}
		internalName = base;
	}

	public String toString() {
		return getInternalName();
	}

	protected void checkLoaded() {
		if (!isLoaded()) {
			try {
				load();
			} catch (IOException e) {
				Log.warn("Failed to load entity.", e);
			} catch (ParseException e) {
				Log.warn("Failed to parse entity.", e);
			}
		}
	}
	
	protected abstract void load() throws IOException, ParseException;
}
