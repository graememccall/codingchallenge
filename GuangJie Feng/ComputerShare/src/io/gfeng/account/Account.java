package io.gfeng.account;

import io.gfeng.account.exception.OverDrawnException;

/**
 * An interface represents bank account.
 * 
 * @author gfeng
 *
 */

public interface Account {
	/**
	 * Check the balance of the account. Return account balance.
	 * @return double
	 */
	public double checkBalance();
	
	/**
	 * Withdraw amount of value. Return balance after withdrawals.
	 * @param value double
	 * @return balance after withdrawals.
	 * @throws OverDrawnException
	 */
	public double withdraw(double value) throws OverDrawnException;
	
	/**
	 * Deposit value to the account 
	 * @param value {@code double} The value put in to account
	 * @return {@code double} return the balance after deposit value.
	 */
	public double deposit(double value);
}
