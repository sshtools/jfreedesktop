package org.freedesktop;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.util.INIFile;
import org.freedesktop.util.Log;

public abstract class AbstractFreedesktopEntity implements FreedesktopResource {

	private static final String NAME = "Name";
	private static final String COMMENT = "Comment";

	protected String example;

	private FileObject base;
	private Properties entityProperties;
	private String internalName;
	private String entityTypeName;
	private boolean loaded;

	public AbstractFreedesktopEntity(FileObject base, String entityTypeName) {
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

	public void load(FileObject file) throws IOException, ParseException {
		InputStream in = file.getContent().getInputStream();
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

	public FileObject getBase() {
		return base;
	}

	protected String getLocalisableField(String key, String language) {
		checkLoaded();
		String name = null;
		if (language != null) {
			name = entityProperties.getProperty(key + "[" + language + "]");
		}
		if (name == null) {
			name = entityProperties.getProperty(key);
		}
		return name;
	}

	protected void init(FileObject base) {
		this.base = base;
		internalName = base.getName().getBaseName();
	}

	public String toString() {
		return getInternalName();
	}

	protected void checkLoaded() {
		if (!isLoaded()) {
			try {
				load();
			} catch (IOException e) {
				Log.warn("Failed to load theme.", e);
			} catch (ParseException e) {
				Log.warn("Failed to parse theme.", e);
			}
		}
	}
	
	protected abstract void load() throws IOException, ParseException;
}
