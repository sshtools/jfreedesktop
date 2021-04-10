/**
 * Copyright © 2006 - 2021 SSHTOOLS Limited (support@sshtools.com)
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
package com.sshtools.jfreedesktop.mime;

import java.util.Arrays;
import java.util.Collection;

/**
 * Exception thrown when a mime type could not be determined by using a pattern
 * and magic must be used. This is for instance required to deal with container
 * formats like Ogg or AVI, that map various video and/or audio-encoded data to
 * one extension.
 * 
 */
@SuppressWarnings("serial")
public class MagicRequiredException extends Exception {

	private Collection<GlobEntry> alternatives;

	public MagicRequiredException(String message,
			GlobEntry... alternatives) {
		this(message, Arrays.asList(alternatives));
	}

	public MagicRequiredException(String message,
			Collection<GlobEntry> alternatives) {
		super(message);
		this.alternatives = alternatives;
	}

	public Collection<GlobEntry> getAlternatives() {
		return alternatives;
	}
}
