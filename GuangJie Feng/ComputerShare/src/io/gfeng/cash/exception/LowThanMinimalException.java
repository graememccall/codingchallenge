package io.gfeng.cash.exception;

/**
 * Throw this {@link Exception} if withdrawal is lower than minimal.
 * @author gfeng
 *
 */
public final class LowThanMinimalException extends Exception {

	private static final long serialVersionUID = -2614364717402628815L;

	public LowThanMinimalException (String message) {
		super(message);
	}
}
