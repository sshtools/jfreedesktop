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
