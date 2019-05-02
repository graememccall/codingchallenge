package io.gfeng.account;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Logger;

import io.gfeng.account.exception.OverDrawnException;

/**
 * An {@link Account} implementation. The class has a {@link ReentrantReadWriteLock}
 * which make thread safe in deposit and withdraw certain amount.
 * @author gfeng
 *
 */
public final class AccountImpl implements Account {
	/**
	 * A {@link Logger} records information for debug and monitor.
	 */
	private static final Logger log = Logger.getLogger(AccountImpl.class.getName());

	/**
	 * An instance of {@link ReentrantReadWriteLock} for threading read/write safety. 
	 */
	private final ReentrantReadWriteLock lock;
	
	/**
	 * {@link ReadLock} instance from {@code lock}
	 */
	private final ReadLock readLock;
	
	/**
	 * {@link WriteLock} instance from {@link lock}
	 */
	private final WriteLock writeLock;
	
	/**
	 * A {@link String} unchangeable account number. It should unique in the system. 
	 */
	private final String accountNumber;
	
	/**
	 * The account balance. It must be thread safe in read and write operation./
	 */
	private volatile double balance = 0;
	
	/**
	 * Construct an account with a {@code String} account number.
	 * @param accountNumber {@link String}
	 */
	public AccountImpl(String accountNumber){
		this.accountNumber = accountNumber;
		this.lock = new ReentrantReadWriteLock(true);
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}
	
	/**
	 * Return the account number
	 * @return {@link String}
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	
	/**
	 * Return the current balance guarded by {@ ReadLock}
	 */
	public double checkBalance() {
		readLock.lock();
		
		try {
			log.fine(toString());
			return balance;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * Return balance after deposited value amount. The code guarded by {@link WriteLock}
	 */
	public double deposit(double value) {
		writeLock.lock();
		
		try {
			log.fine(toString() + " deposit " + value);
			balance += value;
			log.fine(toString());
			
			return balance;
		} finally {
			writeLock.unlock();
		}
	}
	
	public double withdraw(double value) throws OverDrawnException {
		writeLock.lock();
		
		try {
			log.fine(toString() + " withdrew " + value);
			
			if (value > balance) {
				final String msg = toString() + " fail to withdraw " + value;
				throw new OverDrawnException(msg);
			}
			
			balance -= value;
			log.fine(toString());
			
			return balance;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountImpl other = (AccountImpl) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;

		return true;
	}

	/**
	 * Return the information of the instance.
	 */
	@Override
	public String toString() {
		readLock.lock();
		try {
			return "Account [accountNumber=" + accountNumber + ", balance=" + balance + "]";
		} finally {
			readLock.unlock();
		}
	}
}
