package io.gfeng.cash;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.gfeng.account.Account;
import io.gfeng.account.exception.OverDrawnException;
import io.gfeng.cash.exception.CannotDisburseException;
import io.gfeng.cash.exception.HigherThanMaximumException;
import io.gfeng.cash.exception.LowBalanceException;
import io.gfeng.cash.exception.LowThanMinimalException;

/**
 * An implementation of {@link CashMachine} with an unique {@link String} id. 
 * So the instance should be unique in the system.
 * Its instance is thread safe.
 * 
 * @author gfeng
 *
 */

public final class CashMachineImpl implements CashMachine {
	private static final Logger log = Logger.getLogger(CashMachineImpl.class.getName());
	
	/**
	 * The minimal amount of withdraw.
	 */
	private static final int MINIMAL_WITHDRALS = 20;
	
	/**
	 * The maximum amount of withdraw.
	 */
	private static final int MAXIMUM_WITHDRAWS = 250;
	
	/**
	 * A {@link DecimalFormat} with pattern "£###,###.##" for printing out {@link Account} balance. 
	 */
	private static final DecimalFormat BALANCE_FORMATTER = new DecimalFormat("£###,###.##");
	
	
	/**
	 * A {@link String} unique id of instance.
	 */
	private final String mId;
	
	/**
	 * A {@link ReentrantLock} for guarded replenish. 
	 */
	private final ReentrantLock mLock;
	
	/**
	 * A {@link Map} holds note in this cash machine. The key is note, value is number of the note.
	 * Currency notes of denominations 5, 10, 20 and 50.
	 */
	private final Map<Integer, Integer> mReplenish;
	
	/**
	 * Construct a {@link CashMachine}
	 * @param id {@link String}
	 */
	public CashMachineImpl(String id) {
		this.mId = id;
		this.mReplenish = new HashMap<>();
		this.mLock = new ReentrantLock(true);
	}
	
	/**
	 * It is guarded by lock. 
	 * @see CashMachine#checkNoteRemain(int)
	 */
	public int checkNoteRemain(final int note) {
		mLock.lock();
		
		try {
			final Integer noteCount = mReplenish.get(note);
			
			return noteCount == null? 0 : noteCount;
		} finally {
			mLock.unlock();
		}
	}
	
	/**
	 * It is guarded by lock.
	 * @see CashMachine#addNoteToCashMachine(int, int)
	 */
	public boolean addNoteToCashMachine(final int note, final int count) {
		mLock.lock();
		
		try {
			final Integer noteCount = mReplenish.get(note);
			if (noteCount == null) {
				mReplenish.put(note, Integer.valueOf(count));
			} else {
				final int updateCount = noteCount.intValue() + count;

				mReplenish.put(note, updateCount);
			}

			return true;
		} finally {
			mLock.unlock();
		}
	}

	/**
	 * @see CashMachine#checkAccountBalance(Account)
	 */
	public String checkAccountBalance(Account account) {
		if (account == null)
			throw new RuntimeException("Please input a valid account");
				
		return BALANCE_FORMATTER.format(account.checkBalance());
	}
	
	/**
	 * <li>Returns notes of the correct denominations</li>
	 * <li>Allow withdrawals between 20 and 250 inclusive, in multiples of 5</li>
	 * <li>Disburse smallest number of notes</li>
	 * <li>Always disburse at least one 5 note, if possible</li>
	 * @throws LowThanMinimalException 
	 * @see CashMachine#withdraw(Account, int)
	 */
	public Map<Integer, Integer> withdraw(final Account account, final int withdrawal) 
			throws LowThanMinimalException, HigherThanMaximumException, 
			LowBalanceException, CannotDisburseException { 
		//Check withdrawal
		if (withdrawal < MINIMAL_WITHDRALS)
			throw new LowThanMinimalException("Minimal withdraw is " + MINIMAL_WITHDRALS);
		
		if (withdrawal > MAXIMUM_WITHDRAWS)
			throw new HigherThanMaximumException("Maximum withdraw is " + MAXIMUM_WITHDRAWS);
		
		if (withdrawal > account.checkBalance())
			throw new LowBalanceException("Your balance is low.");
		
		if (withdrawal % 5 != 0)
			throw new CannotDisburseException("Withdraw should be 5 times");
		
		mLock.lock();
		
		final Map<Integer, Integer> disburse = new HashMap<>(mReplenish.size());

		try {
			int remain = withdrawal;
			final int note5;
			if (withdrawal % 10 == 0) { 
				//Withdraw 10 multiply. Try get at least one 5 note, if possible
				note5 = Math.min(2, mReplenish.get(5));
			} else { 
				//Withdrawal end with 5 (for example 15, 195). Must get one 5 note.
				note5 = Math.min(1, mReplenish.get(5));
				if (note5 == 0)
					throw new CannotDisburseException("No 5 pound note available");
			}
			if(note5 > 0) {
				remain -= note5 * 5;
				disburse.put(5, note5);
			}
			
			//Get 50 notes
			remain = getRemain(disburse, remain, 50);
	
			if (remain > 0) {
				//Get 20 note in remains
				remain = getRemain(disburse, remain, 20);
	
				if (remain > 0) {
					//Having remains, get 10 notes
					remain = getRemain(disburse, remain, 10);
	
					if (remain > 0) {
						//Having remains, get 5 notes.
						int note5Append = Math.min(remain / 5, mReplenish.get(5) - note5);
						remain -= note5Append * 5;
						if (remain != 0) {
							//The replenish is not enough notes for this withdraw
							final StringBuilder msg = new StringBuilder("Distpath error.");
							msg.append("\n\taccount: ").append(account.toString());
							msg.append(" withdrawal: ").append(withdrawal);
							msg.append("\n\treplenish: ").append(mReplenish.toString());
							msg.append("\n\tdisburse: ").append(disburse.toString());
							msg.append("\n\tcannot disburse: ").append(remain);
							log.warning(msg.toString());
							
							throw new CannotDisburseException("Dispatch error. Cannot withdraw this amount");
						} else if(note5Append > 0) {
							final Integer exist5 = disburse.get(5);
							if (exist5 != null)
								note5Append += exist5.intValue();
							
							disburse.put(5, note5Append);
						}
					}
				}
			}
	
			try {
				//Deduct withdrawal from account
				//It is safe casting from int to double
				account.withdraw((double)withdrawal);
				
				//Deduct note from replenish
				disburse.entrySet().stream().forEach(t -> { 
					Integer value = t.getValue();
					if (value != 0) {
						Integer key = t.getKey();
						int noteRemain = mReplenish.get(key) - value;
						mReplenish.put(key, noteRemain);
					}
				});
			} catch (OverDrawnException e) {
				disburse.clear();
				log.log(Level.WARNING, e.getMessage(), e.getStackTrace());
			}
			
			return disburse;
		} finally {
			mLock.unlock();
		}
	}

	private int getRemain(final Map<Integer, Integer> disburse, int remain, final int note) {
		final int numNote = Math.min(remain / note, mReplenish.get(note));
		remain -= numNote * note;
		if(numNote > 0) {
			disburse.put(note, numNote);
		}
		
		return remain;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(mId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CashMachineImpl other = (CashMachineImpl) obj;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		mLock.lock();
		try {
			return "CashMachine [id=" + mId + ", replenish=" + mReplenish + "]";
		} finally {
			mLock.unlock();
		}
	}
}
