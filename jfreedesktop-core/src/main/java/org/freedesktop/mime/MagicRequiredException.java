package org.freedesktop.mime;

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
