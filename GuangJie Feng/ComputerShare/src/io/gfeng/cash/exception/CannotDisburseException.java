package io.gfeng.cash.exception;

/**
 * Throw this {@link Exception} if cash machine cannot disburse cash for withdraw.
 * @author gfeng
 *
 */
public final class CannotDisburseException extends Exception {

	private static final long serialVersionUID = 1140260097696277608L;

	public CannotDisburseException(String message) {
		super(message);
	}
}
