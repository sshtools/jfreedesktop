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
package org.freedesktop.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Very simple logging help. By default, warning and debug messages are output
 * to standard error. You can provide your own logging backend using
 * {@link Log#setLogger(Logger)}.
 * <p>
 * The default logger has two system properties. <code>jfreedesktop.quiet</code>
 * turns off all logging, <code>jfreedesktop.debug</code> will turn on debug
 * messages.
 */
public class Log {
	public interface Logger {
		default void log(String message) {
		}

		default void warn(String message) {
			log("WARNING: " + message);
		}

		default void warn(String message, Throwable exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			log("WARNING: " + message + ".\n" + sw.toString());
		}

		default void debug(String message) {
			log("DEBUG: " + message);
		}
	}

	private static Logger logger = new Logger() {
		@Override
		public void warn(String message) {
			if (!"true".equals(System.getProperty("jfreedesktop.quiet"))) {
				System.err.println("WARNING: " + message);
			}
		}

		@Override
		public void warn(String message, Throwable exception) {
			if (!"true".equals(System.getProperty("jfreedesktop.quiet"))) {
				System.err.println("WARNING: " + message);
				exception.printStackTrace();
			}
		}

		@Override
		public void debug(String message) {
			if ("true".equals(System.getProperty("jfreedesktop.debug"))) {
				System.out.println("DEBUG: " + message);
			}
		}
	};

	public static void setLogger(Logger logger) {
		Log.logger = logger;
	}

	public static void warn(String message) {
		logger.warn(message);
	}

	public static void warn(String message, Throwable exception) {
		logger.warn(message, exception);
	}

	public static void debug(String message) {
		logger.debug(message);
	}
}
