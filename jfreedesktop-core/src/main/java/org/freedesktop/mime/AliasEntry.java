package org.freedesktop.mime;

import org.freedesktop.FreedesktopEntity;

/**
 * Represents a single <strong>alias</strong> as described in the <a href=
 * "http://standards.freedesktop.org/shared-mime-info-spec/shared-mime-info-spec-latest.html">Shared
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
