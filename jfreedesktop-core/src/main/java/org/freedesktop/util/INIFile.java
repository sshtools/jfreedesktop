package org.freedesktop.util;

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
