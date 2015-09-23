package org.freedesktop.mime;

import org.freedesktop.FreedesktopEntity;

/**
 * Represents a single <i>alias</i> as described in the <a
 * http://standards.freedesktop.org/shared-mime-info-spec/shared-mime-info-spec-latest.html">Shared
 * MIME-info Database Specification</a>
 */
public class AliasEntry implements FreedesktopEntity {

    private String name;
    private String alias;

    public AliasEntry(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getInternalName() {
        return name;
    }
}
