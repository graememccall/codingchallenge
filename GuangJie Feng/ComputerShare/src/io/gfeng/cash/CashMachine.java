package io.gfeng.cash;

import java.util.Map;

import io.gfeng.account.Account;
import io.gfeng.cash.exception.CannotDisburseException;
import io.gfeng.cash.exception.HigherThanMaximumException;
import io.gfeng.cash.exception.LowBalanceException;
import io.gfeng.cash.exception.LowThanMinimalException;

/**
 * An interface represents cash machine.
 * @author gfeng
 *
 */
public interface CashMachine {
	/**
	 * Fill in bank note to cash machine. the count is the number of note.
	 * @param note int
	 * @param count int
	 * @return true if filling in succeeded
	 */
	public boolean addNoteToCashMachine(int note, int count);
	
	/**
	 * Return number of note remains in cash machine
	 * @param note int
	 * @return int the number of note
	 */
	public int checkNoteRemain(int note) ;
	
	/**
	 * Return easy reading {@link String} of account's balance. 
	 * The output example {@code £123,567.28} of {@code 1234567.28}.
	 * @param account {@link Account} which will be checked.
	 * @return account balance in string 
	 */
	public String checkAccountBalance(Account account);
	
	/**
	 * Withdraw amount from account.
	 * @param account {@link Account}
	 * @param withdrawal. The amount to withdraw
	 * @return The number of note
	 */
	public Map<Integer, Integer> withdraw(Account account, int withdrawal)
			throws LowThanMinimalException, HigherThanMaximumException, 
			LowBalanceException, CannotDisburseException;
}
