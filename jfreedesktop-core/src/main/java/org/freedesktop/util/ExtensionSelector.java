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
package org.freedesktop.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

public class ExtensionSelector implements DirectoryStream.Filter<Path> {
	private String extension;

	public ExtensionSelector(String extension) {
		this.extension = "." + extension.toLowerCase();
	}

	@Override
	public boolean accept(Path entry) throws IOException {
		return entry.getFileName().toString().toLowerCase().endsWith(extension);
	}
}