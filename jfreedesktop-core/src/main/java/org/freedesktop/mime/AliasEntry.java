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
