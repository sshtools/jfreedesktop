package org.freedesktop.themes;

import org.freedesktop.AbstractFreedesktopService;

/**
 * Abstract implementations of a {@link ThemeService} that provides methods
 * for maintaining the list of base directories (common to all themes). 
 */
public abstract class AbstractThemeService<T extends Theme> extends AbstractFreedesktopService<T> implements ThemeService<T> {


    // Private instance variables
    private T theme;

    public T getSelectedTheme() {
        return theme;
    }

    public void setSelectedTheme(T theme) {
        this.theme = theme;
    }

}
