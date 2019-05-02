1. Application description
The application simulates a person with a bank account and withdraws cash from 
a cash machine in UK. The behaviours are
	Replenish: 
		Cash machine has currency notes of denominations 5, 10, 20 and 50
		For simplified, the cash machine has loaded 
			5 pounds has 3000 notes
			10 pounds has 1000 notes
			20 pounds has 2000 notes
			50 pounds has 1000 notes
	Withdraw:
		Always disburse at least one 5 note, if possible
		Disburse smallest number of notes
		Allow withdrawals between 20 and 250 inclusive, in multiples of 5

2. System
Linux is needed since I made the application under Linux. 

3. Java
Java 8 is needed for build and run the application.

4. Unzip
Unzip file ComputerShare.zip.

5. Build and Test
In a Linux bash terminal, set the script executable, type:
	chmod +x build.sh

type:
	./build.sh

The script will build source and pack to "computershare.jar", and run the test.

6. The user can run the application, for example:
	java -jar ./computershare.jar -withdrawal 185 -deposit 1893.28
	
	java -jar ./computershare.jar -withdrawal 35 -deposit 300.98
	
	java -jar ./computershare.jar -withdrawal 95 -deposit 893.28
	
	java -jar ./computershare.jar -withdrawal 245 -deposit 3003.28

7. Check the exception cases:
	java -jar ./computershare.jar -withdrawal 243 -deposit 3003.28
	
	java -jar ./computershare.jar -withdrawal 15 -deposit 3003.28
	
	java -jar ./computershare.jar -withdrawal 255 -deposit 3003.28
			
	java -jar ./computershare.jar -withdrawal 225 -deposit 200.23

