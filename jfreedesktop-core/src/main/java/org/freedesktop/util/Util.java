/**
 * Copyright © 2006 - 2018 SSHTOOLS Limited (support@sshtools.com)
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
package org.freedesktop.util;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

public class Util {
	public static String emptyOrTrimmed(String string) {
		return string == null ? "" : string.trim();
	}

	public static String getFileNameExtension(String name) {
		int idx = name.lastIndexOf('.');
		return idx == -1 ? name : name.substring(idx + 1);
	}

	public static String trimmedNonEmptyOrNull(String string) {
		if (string == null) {
			return null;
		}
		String trim = string.trim();
		return trim.length() == 0 ? null : trim;
	}

	public static Collection<String> splitList(String string) {
		return string.length() == 0 ? Arrays.asList(new String[] {}) : Arrays.asList(string.split(";"));
	}

	public static String getBasename(URL base) {
		String path = base.getPath();
		if (path.equals("") || path.equals("/")) {
			return "/";
		}
		int idx = path.lastIndexOf("/", path.endsWith("/") ? path.length() - 2 : path.length() - 1);
		return idx == -1 ? path : path.substring(idx + 1);
	}

	public static String stripTrailingSlash(String path) {
		while (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
}
