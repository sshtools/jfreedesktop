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
package org.freedesktop.cursors;

import java.text.ParseException;
import java.util.Properties;

public class Directory {

    public enum Type {
        fixed,scalable,threshold;
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

    public Directory(String key, Properties properties) throws ParseException {
        this.key = key;
        
        if(!properties.containsKey(SIZE)) {
            throw new ParseException("Size entry is required.", 0);
        }
        size = Integer.parseInt(properties.getProperty(SIZE));
        context = properties.getProperty(CONTEXT);
        if(properties.containsKey(TYPE)) {
            String typeName = properties.getProperty(TYPE).toLowerCase();
            try {
                type = Type.valueOf(typeName);
            }
            catch(IllegalArgumentException iae) {
                throw new ParseException("Invalid Type ' " + typeName + "' in " + key, 0);
            }
        }
        if(properties.containsKey(MAX_SIZE)) {
            maxSize = Integer.parseInt(properties.getProperty(MAX_SIZE));
        }
        else {
            maxSize = size;
        }
        if(properties.containsKey(MIN_SIZE)) {
            minSize = Integer.parseInt(properties.getProperty(MIN_SIZE));
        }
        else {
            minSize = size;
        }
        if(properties.containsKey(THRESHOLD)) {
            minSize = Integer.parseInt(properties.getProperty(THRESHOLD));
            
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
    
}