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
package org.freedesktop.cursors;

import java.io.File;
import java.io.IOException;

import org.freedesktop.themes.ThemeService;

/**
 * Implementations of this service interface provide an easy way for Java to use
 * the <a href="http://www.freedesktop.org/wiki/">freedesktop.org</a>'s cursor 
 * themes. 
 */
public interface CursorService extends ThemeService<CursorTheme> {
    /**
     * Find the cursor icon. See the class description for 
     * {@link CursorService} for a complete description of the search
     * algorithm. <code>null</code> will only be returned if no matching
     * icon can be found.
     * 
     * @param name name of icon 
     * @return file icon file
     * @throws IOException
     */
    File findIcon(String name) throws IOException;

}