package org.freedesktop.themes;

import org.freedesktop.FreedesktopService;

/**
 * Implementations of this service interface provide an easy way for Java to use
 * the <a href="http://www.freedesktop.org/wiki/">freedesktop.org</a>'s various
 * theme specications. See <a href="http://freedesktop.org/wiki/DesktopThemeSpec">http://freedesktop.org/wiki/DesktopThemeSpec</a>.
 */
public interface ThemeService<T extends Theme>  extends FreedesktopService<T> {

    /**
     * Set the currently selected theme.
     * 
     * @param theme selected theme
     */
    void setSelectedTheme(T theme);

    /**
     * Get the currently selected theme. May be <code>null</code> if 
     * no themes have been found in any base directory.
     *  
     * @return selected theme
     */
    T getSelectedTheme();

}