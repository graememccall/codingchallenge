package io.gfeng.account.exception;

/**
 * An {@link Exception} for over drawn. 
 * @author gfeng
 *
 */
public final class OverDrawnException extends Exception {

	private static final long serialVersionUID = 4121272423520056080L;

	/**
	 * Then reason for throwing this Exception. 
	 * @param message String
	 */
	public OverDrawnException(String message) {
		super(message);
	}
}
