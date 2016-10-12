/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * 
 */
package org.freedesktop.util;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;


public final class ExtensionSelector implements FileSelector {

	private String extension;

	public ExtensionSelector(String extension) {
		this.extension = "." + extension.toLowerCase();
	}

	public boolean includeFile(FileSelectInfo info) throws Exception {
		return info.getFile().getName().getBaseName().toLowerCase().endsWith(extension);
	}

	public boolean traverseDescendents(FileSelectInfo info) throws Exception {
		return info.getDepth() == 0;
	}
}