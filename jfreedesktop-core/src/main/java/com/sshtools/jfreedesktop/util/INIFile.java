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
package com.sshtools.jfreedesktop.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Properties;

public class INIFile extends HashMap<String, Properties> {

    private static final long serialVersionUID = 2321630945337743414L;

    public void load(InputStream in) throws IOException, ParseException {
        load(new InputStreamReader(new BufferedInputStream(in, 65536)));
    }

    public void load(Reader reader) throws IOException, ParseException {
        BufferedReader buffer = new BufferedReader(reader);
        String line;
        clear();
        Properties properties = null;
        String key = null;
        while ((line = buffer.readLine()) != null) {
            if (!line.trim().startsWith("#") && line.trim().length() != 0) {
                if (line.startsWith("[")) {
                    int idx = line.lastIndexOf(']');
                    if (idx == -1) {
                        throw new ParseException("Section name is not terminated with ]", line.length() - 1);
                    }
                    key = line.substring(1, idx);
                    properties = new Properties();
                    put(key, properties);
                } else {
                    if (properties == null) {
                        throw new ParseException("No section name.", 0);
                    }
                    int idx = line.indexOf('=');
                    if (idx == -1) {
                        throw new ParseException("Name / value pairs must be separated by an equals (=) character.",
                                        line.length() - 1);
                    }
                    properties.put(line.substring(0, idx), line.substring(idx + 1));
                }
            }
        }
    }
}
