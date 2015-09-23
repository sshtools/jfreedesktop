package org.freedesktop.mime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.freedesktop.FreedesktopEntity;

/**
 * Represents a single <i>glob</i> as described in the <a
 * http://standards.freedesktop.org/shared-mime-info-spec/shared-mime-info-spec-latest.html">Shared
 * MIME-info Database Specification</a>
 */
public class GlobEntry implements FreedesktopEntity {

    private String name;
    private List<String> patterns;

    public GlobEntry(String name) {
        this.name = name;
        patterns=  new ArrayList<String>();
    }
    
    public void addPattern(String pattern) {
        patterns.add(pattern);
    }

    public Collection<String> getPatterns() {
        return patterns;
    }

    public String getInternalName() {
        return name;
    }
}
