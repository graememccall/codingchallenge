package io.gfeng.app.exception;

/**
 * Throw this {@link Exception} if input string is invalid currency amount.
 * @author gfeng
 *
 */
public class WrongNumberFormatException extends Exception {
	
	private static final long serialVersionUID = 988513787053240316L;

	public WrongNumberFormatException(String msg) {
		super(msg);
	}
}
