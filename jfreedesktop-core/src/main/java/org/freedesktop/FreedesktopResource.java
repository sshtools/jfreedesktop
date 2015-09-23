package org.freedesktop;

import java.util.Locale;

public interface FreedesktopResource extends FreedesktopEntity {
    String getName();
    String getName(Locale locale);
    String getName(String language);
    String getComment();
    String getComment(Locale locale);
    String getComment(String language);

}
