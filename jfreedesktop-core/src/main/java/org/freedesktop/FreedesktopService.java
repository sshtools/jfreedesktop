package org.freedesktop;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.vfs2.FileObject;

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
    void addBase(FileObject base) throws IOException;

    /**
     * Remove a directory from the list that is is searched.
     * 
     * @param base base directory to remove from the list that is searched
     * @see #addBase(File)
     */
    void removeBase(FileObject base);

    /**
     * Get a collection of all base directories.
     * 
     * @return base directories
     */
    public Collection<FileObject> getBases();

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
    Collection<T> getEntities(FileObject base);
    
    /**
     * Get a collection of all available entities in all bases. The bases are
     * returned in reverse order, so that overriding entities come first.
     * 
     * @return collection of available entities
     */
    Collection<T> getAllEntities();

}