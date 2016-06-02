package org.freedesktop.themes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.AbstractFreedesktopEntity;
import org.freedesktop.util.INIFile;

public abstract class AbstractTheme extends AbstractFreedesktopEntity implements Theme {

    private static final String EXAMPLE = "Example";

    protected String example;

    public AbstractTheme(String themeTypeName, FileObject... base) throws IOException {
        super(themeTypeName, base);
    }

    public String getExample() {
		checkLoaded();
        return example;
    }

    protected void initFromProperties(INIFile iniFile, Properties properties) throws IOException, ParseException {
        example = properties.getProperty(EXAMPLE);
        initFromThemeProperties(iniFile, properties);
    }

    protected abstract void initFromThemeProperties(INIFile iniFile, Properties themeProperties) throws IOException, ParseException;
}
