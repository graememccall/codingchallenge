package io.gfeng.cash.exception;

/**
 * Throw this {@link Exception} if withdrawal is higher than maximum.
 * @author gfeng
 *
 */
public final class HigherThanMaximumException extends Exception {

	private static final long serialVersionUID = 6408115079431398579L;

	public HigherThanMaximumException(String message) {
		super(message);
	}
}
