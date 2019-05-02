package io.gfeng.cash.exception;

/**
 * Throw this {@link Exception} if withdrawal is higher than account's balance.
 * @author gfeng
 *
 */
public final class LowBalanceException extends Exception {
	private static final long serialVersionUID = -3924957574323836560L;

	public LowBalanceException(String message) {
		super(message);
	}
}
