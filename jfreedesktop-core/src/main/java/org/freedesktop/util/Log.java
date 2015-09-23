package org.freedesktop.util;

public class Log {
	public static void warn(String message) {
		System.err.println("WARNING: " + message);
	}

	public static void warn(String message, Throwable exception) {
		System.err.println("WARNING: " + message);
		exception.printStackTrace();
	}

	public static void debug(String message) {
		if ("debug".equals(System.getProperty("org.freedesktop.loglevel"))) {
			System.out.println("DEBUG: " + message);
		}
	}
}
