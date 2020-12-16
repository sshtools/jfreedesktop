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
package com.sshtools.jfreedesktop.themes;

import com.sshtools.jfreedesktop.FreedesktopService;

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