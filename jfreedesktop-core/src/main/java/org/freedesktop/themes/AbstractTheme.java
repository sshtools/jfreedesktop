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
package org.freedesktop.themes;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Properties;

import org.freedesktop.AbstractFreedesktopEntity;
import org.freedesktop.util.INIFile;

public abstract class AbstractTheme extends AbstractFreedesktopEntity implements Theme {

    private static final String EXAMPLE = "Example";

    protected String example;

    public AbstractTheme(String themeTypeName, Path... base) throws IOException {
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
