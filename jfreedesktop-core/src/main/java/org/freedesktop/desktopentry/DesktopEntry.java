package org.freedesktop.desktopentry;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.AbstractFreedesktopEntity;
import org.freedesktop.util.INIFile;
import org.freedesktop.util.Log;
import org.freedesktop.util.Util;

/**
 * Implements the <a href=
 * "http://standards.freedesktop.org/freedesktop-platform-specs/1.0/desktop-entry-spec-0.9.4/ar01s04.html"
 * >Desktop Entry Specification</a>
 */
public class DesktopEntry extends AbstractFreedesktopEntity {

	/**
	 * There are 4 types of desktop entries: Application, Link, FSDevice and
	 * Directory.
	 */
	public enum Type {
		application, link, fsDevice, directory
	}

	private static final String DESKTOP_ENTRY = "Desktop Entry";
	private static final String TYPE = "Type";
	private static final String VERSION = "Version";
	private static final String ENCODING = "Encoding";
	private static final String NO_DISPLAY = "NoDisplay";
	private static final String HIDDEN = "Hidden";
	private static final String ICON = "Icon";
	private static final String FILE_PATTERN = "FilePattern";
	private static final String TRY_EXEC = "TryExec";
	private static final String EXEC = "Exec";
	private static final String PATH = "Path";
	private static final String TERMINAL = "Terminal";
	private static final String SWALLOW_TITLE = "SwallowTitle";
	private static final String SWALLOW_EXEC = "SwallowExec";
	private static final String ACTIONS = "Actions";
	private static final String MIME_TYPE = "MimeType";
	private static final String SORT_ORDER = "SortOrder";
	private static final String DEV = "Dev";
	private static final String FS_TYPE = "FSType";
	private static final String MOUNT_POINT = "MountPoint";
	private static final String READ_ONLY = "ReadOnly";
	private static final String UNMOUNT_ICON = "UnmountIcon";
	private static final String URL = "URL";
	private static final String CATEGORIES = "Categories";
	private static final String ONLY_SHOW_IN = "OnlyShowIn";
	private static final String NOT_SHOW_IN = "NotShowIn";
	private static final String STARTUP_NOTIFY = "StartupNotify";
	private static final String STARTUP_WM_CLASS = "StartupWMClass";

	private Type type;
	private String encoding;
	private String genericName;
	private String version;
	private boolean noDisplay;
	private boolean hidden;
	private Collection<String> filePattern;
	private String tryExec;
	private String exec;
	private boolean terminal;
	private String path;
	private String swallowExec;
	private Collection<String> actions;
	private Collection<String> mimeType;
	private String sortOrder;
	private String dev;
	private String fSType;
	private String mountPoint;
	private boolean readOnly;
	private URI url;
	private Collection<String> categories;
	private Collection<String> onlyShowIn;
	private Collection<String> notShowIn;
	private boolean startupNotify;
	private String startupWMClass;

	public DesktopEntry(FileObject... base) throws IOException, ParseException {
		super(DESKTOP_ENTRY, base);
	}

	/**
	 * A list of regular expressions to match against for a file manager to
	 * determine if this entry's icon should be displayed. Usually simply the
	 * name of the main executable and friends.
	 * 
	 * @return file pattern
	 */
	public Collection<String> getFilePattern() {
		checkLoaded();
		return filePattern;
	}

	/**
	 * There are 4 types of desktop entries: Application, Link, FSDevice and
	 * Directory.
	 * 
	 * @return type
	 */
	public Type getType() {
		checkLoaded();
		return type;
	}

	/**
	 * Version of Desktop Entry Specification (While the version field is not
	 * required to be present, it should be in all newer implementations of the
	 * Desktop Entry Specification. If the version number is not present, a
	 * "pre-standard" desktop entry file is to be assumed).
	 * 
	 * @return version
	 */
	public String getVersion() {
		checkLoaded();
		return version;
	}

	/**
	 * NoDisplay means "this application exists, but don't display it in the
	 * menus". This can be useful to e.g. associate this application with MIME
	 * types, so that it gets launched from a file manager (or other apps),
	 * without having a menu entry for it (there are tons of good reasons for
	 * this, including e.g. the netscape -remote, or kfmclient openURL kind of
	 * stuff).
	 * 
	 * @return no display
	 */
	public boolean isNoDisplay() {
		checkLoaded();
		return noDisplay;
	}

	/**
	 * Generic name of the application, for example "Web Browser".
	 * 
	 * @return generic name
	 */
	public String getGenericName() {
		checkLoaded();
		return genericName;
	}

	/**
	 * Icon to display in file manager, menus, etc. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @return icon
	 */
	public String getIcon() {
		return getIcon((String) null);
	}

	/**
	 * Icon to display in file manager, menus, etc. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @param locale locale
	 * @return icon
	 */
	public String getIcon(Locale locale) {
		return getIcon(locale == null ? (String) null : locale.getLanguage());
	}

	/**
	 * Icon to display in file manager, menus, etc. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @param language language
	 * @return icon
	 */
	public String getIcon(String language) {
		return getLocalisableField(ICON, language);
	}

	/**
	 * Hidden should have been called Deleted. It means the user deleted (at his
	 * level) something that was present (at an upper level, e.g. in the system
	 * dirs). It's strictly equivalent to the .desktop file not existing at all,
	 * as far as that user is concerned. This can also be used to "uninstall"
	 * existing files (e.g. due to a renaming) - by letting make install install
	 * a file with Hidden=true in it.
	 * 
	 * @return hidden
	 */
	public boolean isHidden() {
		checkLoaded();
		return hidden;
	}

	/**
	 * File name of a binary on disk used to determine if the program is
	 * actually installed. If not, entry may not show in menus, etc. Will either
	 * return a non-empty string or <code>null</code> if the entry was not
	 * specified.
	 * 
	 * @return try exec
	 */
	public String getTryExec() {
		checkLoaded();
		return tryExec;
	}

	/**
	 * Program to execute, possibly with arguments. Will either return a
	 * non-empty string or <code>null</code> if the entry was not specified.
	 * 
	 * @return exec
	 */
	public String getExec() {
		checkLoaded();
		return exec;
	}

	/**
	 * If entry is of type Application, the working directory to run the program
	 * in. Will either return a non-empty string or <code>null</code> if the
	 * entry was not specified.
	 * 
	 * @return path
	 */
	public String getPath() {
		checkLoaded();
		return path;
	}

	/**
	 * Whether the program runs in a terminal window.
	 * 
	 * @return program runs in a terminal window
	 */
	public boolean isTerminal() {
		checkLoaded();
		return terminal;
	}

	/**
	 * If entry is swallowed onto the panel, this should be the title of the
	 * window.
	 * 
	 * @return swallow title
	 */
	public String getSwallowTitle() {
		return getSwallowTitle((String) null);
	}

	/**
	 * If entry is swallowed onto the panel, this should be the title of the
	 * window.
	 * 
	 * @param locale locale
	 * @return swallow title
	 */
	public String getSwallowTitle(Locale locale) {
		return getSwallowTitle(locale == null ? (String) null : locale.getLanguage());
	}

	/**
	 * If entry is swallowed onto the panel, this should be the title of the
	 * window.
	 * 
	 * @param language language
	 * @return swallow title
	 */
	public String getSwallowTitle(String language) {
		return getLocalisableField(SWALLOW_TITLE, language);
	}

	/**
	 * Program to exec if swallowed app is clicked.
	 * 
	 * @return swallow exec
	 */
	public String getSwallowExec() {
		checkLoaded();
		return swallowExec;
	}

	/**
	 * Additional actions possible.Will always return at least an empty
	 * collection.
	 * 
	 * @return additional actions
	 */
	public Collection<String> getActions() {
		checkLoaded();
		return actions;
	}

	/**
	 * Get a list of all the Mime types the applications supported by this
	 * entry. Will always return at least an empty collection.
	 * 
	 * @return mime types
	 */
	public Collection<String> getMimeType() {
		checkLoaded();
		return mimeType;
	}

	/**
	 * Get the order in which to display files. Will either return a non-empty
	 * string or <code>null</code> if the entry was not specified.
	 * 
	 * @return sort order
	 */
	public String getSortOrder() {
		checkLoaded();
		return sortOrder;
	}

	/**
	 * Get the device to mount. Will either return a non-empty string or
	 * <code>null</code> if the entry was not specified.
	 * 
	 * @return device to mount
	 */
	public String getDev() {
		checkLoaded();
		return dev;
	}

	/**
	 * Get the type of file system to try and mount. Will either return a
	 * non-empty string or <code>null</code> if the entry was not specified.
	 * 
	 * @return file system type
	 */
	public String getFSType() {
		return fSType;
	}

	/**
	 * Get the mount point of the device. Will either return a non-empty string
	 * or <code>null</code> if the entry was not specified.
	 * 
	 * @return mount point
	 */
	public String getMountPoint() {
		return mountPoint;
	}

	/**
	 * Get whether the device is read only or not.
	 * 
	 * @return read only
	 */
	public boolean isReadOnly() {
		checkLoaded();
		return readOnly;
	}

	/**
	 * Icon to display when device is not mounted. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @return icon
	 */
	public String getUnmountIcon() {
		return getUnmountIcon((String) null);
	}

	/**
	 * Icon to display when device is not mounted. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @param locale locale
	 * @return icon
	 */
	public String getUnmountIcon(Locale locale) {
		return getUnmountIcon(locale == null ? (String) null : locale.getLanguage());
	}

	/**
	 * Icon to display when device is not mounted. The the name will either be
	 * an absolute path, or the platform implementation-dependent icon name.
	 * 
	 * @param language language
	 * @return icon
	 */
	public String getUnmountIcon(String language) {
		return getLocalisableField(UNMOUNT_ICON, language);
	}

	/**
	 * If entry is Link type, the URL to access.
	 * 
	 * @return URL
	 */
	public URI getURL() {
		checkLoaded();
		return url;
	}

	/**
	 * Categories in which the entry should be shown in a menu (for possible
	 * values see the Desktop Menu Specification).
	 * 
	 * @return categories
	 */
	public Collection<String> getCategories() {
		checkLoaded();
		return categories;
	}

	/**
	 * If true, it is KNOWN that the application will send a "remove" message
	 * when started with the DESKTOP_LAUNCH_ID environment variable set (see the
	 * Startup Notification Protocol Specification for more details).
	 * 
	 * @return startup notify
	 */
	public boolean isStartupNotify() {
		checkLoaded();
		return startupNotify;
	}

	/**
	 * The window manager class or name hint (see the Startup Notification
	 * Protocol Specification for more details) to use for startup notifocation
	 * (see the Startup Notification Protocol Specification for more details).
	 * 
	 * @return startup window manager class
	 * @see #isStartupNotify()
	 */
	public String getStartupWMClass() {
		checkLoaded();
		return startupWMClass;
	}

	protected void initFromProperties(INIFile iniFile, Properties properties) throws IOException, ParseException {
		String typeName = properties.getProperty(TYPE, "").trim();
		if (typeName.equals("")) {
			throw new ParseException("Type field is required.", 0);
		}
		type = Type.valueOf(typeName.toLowerCase());
		if (type == null) {
			throw new ParseException("Invalid Type.", 0);
		}
		version = properties.getProperty(VERSION, "pre-standard").trim();
		encoding = properties.getProperty(ENCODING, "").trim();
		if (!encoding.equals("") && !encoding.equalsIgnoreCase("utf-8") && !encoding.equals("legacy-mixed")) {
			Log.warn(String.format("Invalid encoding, %s, defaulting to UTF-8", encoding));
			encoding = "UTF-8";
		}
		noDisplay = "true".equalsIgnoreCase(Util.emptyOrTrimmed(properties.getProperty(NO_DISPLAY)));
		hidden = "true".equalsIgnoreCase(Util.emptyOrTrimmed(properties.getProperty(HIDDEN)));
		filePattern = Util.splitList(properties.getProperty(FILE_PATTERN, ""));
		tryExec = Util.trimmedNonEmptyOrNull(properties.getProperty(TRY_EXEC));
		exec = Util.trimmedNonEmptyOrNull(properties.getProperty(EXEC));
		path = Util.trimmedNonEmptyOrNull(properties.getProperty(PATH));
		terminal = "true".equalsIgnoreCase(Util.emptyOrTrimmed(properties.getProperty(TERMINAL)));
		swallowExec = Util.trimmedNonEmptyOrNull(properties.getProperty(SWALLOW_EXEC));
		actions = Util.splitList(properties.getProperty(ACTIONS, ""));
		mimeType = Util.splitList(properties.getProperty(MIME_TYPE, ""));
		sortOrder = Util.trimmedNonEmptyOrNull(properties.getProperty(SORT_ORDER));
		dev = Util.trimmedNonEmptyOrNull(properties.getProperty(DEV));
		fSType = Util.trimmedNonEmptyOrNull(properties.getProperty(FS_TYPE));
		mountPoint = Util.trimmedNonEmptyOrNull(properties.getProperty(MOUNT_POINT));
		readOnly = "true".equalsIgnoreCase(Util.emptyOrTrimmed(properties.getProperty(READ_ONLY)));
		String url = Util.trimmedNonEmptyOrNull(properties.getProperty(URL));
		if (url != null) {
			try {
				this.url = new URI(url);
			} catch (URISyntaxException e) {
				throw new ParseException(e.getMessage(), 0);
			}
		}
		categories = Util.splitList(properties.getProperty(CATEGORIES, ""));
		onlyShowIn = Util.splitList(properties.getProperty(ONLY_SHOW_IN, ""));
		notShowIn = Util.splitList(properties.getProperty(NOT_SHOW_IN, ""));
		if (onlyShowIn.size() > 0 && notShowIn.size() > 0) {
			throw new ParseException("Only OnlyShowIn or NotShowIn may be specified, not both.", 0);
		}
		startupNotify = "true".equalsIgnoreCase(Util.emptyOrTrimmed(properties.getProperty(STARTUP_NOTIFY)));
		startupWMClass = Util.trimmedNonEmptyOrNull(properties.getProperty(STARTUP_WM_CLASS));
	}

	@Override
	protected void load() throws IOException, ParseException {
		load(getBases().iterator().next());
	}
}
