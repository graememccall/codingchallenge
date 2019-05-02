package io.gfeng.app;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gfeng.account.Account;
import io.gfeng.account.AccountImpl;
import io.gfeng.app.exception.WrongNumberFormatException;
import io.gfeng.cash.CashMachine;
import io.gfeng.cash.CashMachineImpl;
import io.gfeng.cash.exception.CannotDisburseException;
import io.gfeng.cash.exception.HigherThanMaximumException;
import io.gfeng.cash.exception.LowBalanceException;
import io.gfeng.cash.exception.LowThanMinimalException;

public final class WithdrawApp {
	public static void main(String[] args) throws LowThanMinimalException, 
			HigherThanMaximumException, LowBalanceException, CannotDisburseException, 
			WrongNumberFormatException {
		
		if (args.length == 0) {
			printHelp();
			System.exit(0);
		}
		
		double deposit = -1;
		int withdrawal = -1;
		
		// Set up cash machine fill in notes.
		final CashMachine cashMachine = new CashMachineImpl("cashmachine-001");
		cashMachine.addNoteToCashMachine(5, 3000);
		cashMachine.addNoteToCashMachine(10, 1000);
		cashMachine.addNoteToCashMachine(20, 2000);
		cashMachine.addNoteToCashMachine(50, 1000);
		
		// Set up an account
		final Account account = new AccountImpl("01001");
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-withdrawal")) {
				final String wdl = args[++i];
				
				final Pattern p = Pattern.compile("\\d*?[05]");
				final Matcher m = p.matcher(wdl);
				
				if(m.matches()) {
					withdrawal = Integer.parseInt(wdl);
				} else {
					throw new WrongNumberFormatException("withdrawal " + wdl
							+ " is not an integer of multiples of 5");
				}
			} else if(args[i].equals("-deposit")) {
				final String dep = args[++i];
				
				final Pattern p = Pattern.compile("\\d+(\\.\\d{0,2})?");
				final Matcher m = p.matcher(dep);
				
				if(m.matches()) {
					deposit = Double.parseDouble(dep);
				} else {
					throw new WrongNumberFormatException("deposit " + dep 
							+ " is not in one of format "
							+ "12356.84, 123458, 1234567.8");
				}
			} else if(args[i].equals("-test")) {
				deposit = 1009838.59;
				account.deposit(deposit);
				
				testThough(account, cashMachine);
				
				System.exit(0);
			} else if(args[i].equals("-h") || args[i].equals("-help")) {
				printHelp();
				System.exit(0);
			}
		}
		
		if (deposit < 0) {
			System.err.println("deposit is " + deposit + " It should greater than zero");
			printHelp();
			System.exit(1);
		}
		
		if (withdrawal < 0) {
			System.err.println("withdrawal is " + withdrawal + " It should greater than zero");
			printHelp();
			System.exit(1);
		}
		
		account.deposit(deposit);
		
		withdraw(account, cashMachine, withdrawal);
		
		System.exit(0);
	}
	
	private static void printHelp(){
		out.println("Usage:");
		out.println("java -jar ./computershare.jar -withrawal {withdrawal} -deposit {deposit}");
		out.println("    -deposit deposit amount to the account");
		out.println("    -withdrawal withdraw amount");
		out.println("    -test withdraw amount from 20 to 250, deposit 1009838.59");
		out.println("    -debug run application in debug mode");
		out.println("    -h, -help print this help");
		out.println("");
		out.println("For example \"java -jar ./computershare.jar -withdrawal 185 -deposit 1893.28\"");
	}
	
	private static void withdraw(Account account, CashMachine cashMachine, int withdrawal) throws LowThanMinimalException, HigherThanMaximumException, LowBalanceException, CannotDisburseException {
		out.println(cashMachine.toString());

		out.println("Withdraw " + withdrawal);
		
		out.println("Disburse: " + cashMachine.withdraw(account, withdrawal).toString());
		
		out.println("After withdraw ");
		out.println(account.toString());
		out.println(cashMachine.toString());
	}
	
	private static void testThough(Account account, CashMachine cashMachine) throws LowThanMinimalException, HigherThanMaximumException, LowBalanceException, CannotDisburseException {
		for (int i = 20; i <= 250; i +=5) {
			final Map<Integer, Integer> disburse = cashMachine.withdraw(account, i);
		
			final int totalDispatch = disburse.entrySet().stream().flatMapToInt(t -> {
					return Arrays.stream(new int[] {t.getKey() * t.getValue()});
			}).sum();
			
			out.println("withdrawal " + i + " -- disburse " + disburse);
			
			if (i != totalDispatch)
				throw new RuntimeException(i + " is wrong");
		}
	}
}
