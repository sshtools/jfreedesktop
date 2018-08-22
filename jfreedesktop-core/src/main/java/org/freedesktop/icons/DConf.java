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
package org.freedesktop.icons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.freedesktop.util.Log;

public class DConf {

	// TODO escaping

	public class Entry<T> {
		private Class<?> type;
		private String name;
		private List<Object> values = new ArrayList<Object>();
		private boolean exists;

		private Entry(String name) {
			this.name = name;
		}

		private Entry(String name, String line) {
			this.name = name;
			if (line.startsWith("'") && line.endsWith("'")) {
				type = String.class;
				values.add(line.substring(1, line.length() - 1));
				exists = true;
			} else if (line.equals("")) {
				type = Object.class;
				exists = true;
			} else if(line.equals("true") || line.equals("false")) {
				type = Boolean.class;
				exists = true;
				values.add(Boolean.valueOf(line));
			}  else if(line.startsWith("uint32 ")) {
				type = Integer.class;
				exists = true;
				values.add(Integer.parseInt(line.substring(7)));
			} else if (line.startsWith("[") && line.endsWith("]")) {
				String[] a = line.substring(1, line.length() - 1).split(",");
				for (String s : a) {
					s = s.trim();
					if (s.startsWith("'") && s.endsWith("'")) {
						type = String.class;
						values.add(s.substring(1, s.length() - 1));
					} else {
						Log.warn("Could not parse '" + s + "'");
					}
				}
				exists = true;
			} else {
				try {
					Long l = Long.parseLong(line);
					type = Long.class;
					exists = true;
					values.add(l);
				} catch (NumberFormatException nfe) {
					try {
						Double l = Double.parseDouble(line);
						type = Double.class;
						exists = true;
						values.add(l);
					} catch (NumberFormatException nfe2) {
						Log.warn("Could not parse '" + line + "'");
					}
				}
			}
		}

		public boolean isExists() {
			return exists;
		}

		@SuppressWarnings("unchecked")
		public List<T> getValues() {
			return (List<T>) values;
		}

		public Class<?> getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("unchecked")
		public T getValue() {
			return (T) (values.size() > 0 ? values.get(0) : null);
		}
	}

	public final static DConf $ = new DConf("/");

	private Map<String, DConf> children = new HashMap<String, DConf>();
	private String path;
	private Map<String, Entry<?>> values = new HashMap<String, Entry<?>>();
	private boolean loaded;

	private DConf(String path) {
		this.path = path;
	}

	private void load() throws IOException {
		if (loaded) {
			return;
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("dconf", "list", path);
			pb.redirectErrorStream();
			Process p = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			try {
				String line = null;
				while ((line = r.readLine()) != null) {
					Log.debug("DConf output: " + line);
					if (line.endsWith("/")) {
						DConf d = new DConf(path + line);
						children.put(line, d);
					} else {
						values.put(line, null);
					}
				}
			} finally {
				r.close();
				p.waitFor();
			}

			for (Map.Entry<String, Entry<?>> en : values.entrySet()) {
				pb = new ProcessBuilder("dconf", "read", path + en.getKey());
				pb.redirectErrorStream();
				p = pb.start();
				r = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				try {
					en.setValue(new Entry<Object>(en.getKey(), r.readLine()));
				} finally {
					r.close();
					p.waitFor();
				}
			}
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}

		loaded = true;
	}

	public Entry<?> getEntry(String name) {
		try {
			load();
			return values.get(name);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public DConf node(String name) {
		if (!path.endsWith("/")) {
			throw new IllegalArgumentException("Node names must end with /");
		}
		try {
			DConf node = this;
			StringTokenizer t = new StringTokenizer(name, "/");
			while (t.hasMoreTokens()) {
				String n = t.nextToken() + "/";
				node.load();
				if (node.children.containsKey(n)) {
					node = node.children.get(n);
				} else {
					node = new DConf(n);
				}
			}
			return node;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@SuppressWarnings("unchecked")
	public Entry<String> getStringEntry(String name) {
		try {
			load();
			return values.containsKey(name) ? (Entry<String>) values.get(name)
					: new Entry<String>(name);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
