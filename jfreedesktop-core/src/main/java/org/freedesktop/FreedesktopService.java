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
package org.freedesktop;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Implementations of this service interface provide an easy way for Java to use
 * the <a href="http://www.freedesktop.org/wiki/">freedesktop.org</a>'s various
 * specifications.
 */
public interface FreedesktopService<T extends FreedesktopEntity> {

    /**
     * Add a directory to the list of those that should be searched. The base
     * directories will be searched in the order they were added.
     * 
     * @param base base directory
     * @throws IOException if location is invalid in some way
     */
    void addBase(Path base) throws IOException;

    /**
     * Remove a directory from the list that is is searched.
     * 
     * @param base base directory to remove from the list that is searched
     * @see #addBase(Path)
     */
    void removeBase(Path base);

    /**
     * Get a collection of all base directories.
     * 
     * @return base directories
     */
    public Collection<Path> getBases();

    /**
     * Get a entities given its name. If the entities exists in more than one base
     * directory, the first base that the named theme is found in is returned.
     * 
     * @param name internal name of entities
     * @return icon theme
     */
    T getEntity(String name);

    /**
     * Get a collection of all available entities in the specified base directory.
     * 
     * @param base base directory
     * @return collection of available entities
     */
    Collection<T> getEntities(Path base);
    
    /**
     * Get a collection of all available entities in all bases. The bases are
     * returned in reverse order, so that overriding entities come first.
     * 
     * @return collection of available entities
     */
    Collection<T> getAllEntities();

}