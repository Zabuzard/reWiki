package de.zabuza.rewiki.exceptions;

import java.io.IOException;

/**
 * Exception that is thrown whenever an unexpected IO-exception occurred.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class UnexpectedIOException extends IllegalStateException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this exception to be thrown whenever an
	 * unexpected IO-exception occurred.
	 * 
	 * @param cause
	 *            The exact cause that lead to this problem
	 */
	public UnexpectedIOException(final IOException cause) {
		super(cause);
	}

}
