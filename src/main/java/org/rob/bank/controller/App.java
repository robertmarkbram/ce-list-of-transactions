package org.rob.bank.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.rob.bank.model.Transaction;
import org.rob.bank.service.AccountData;
import org.rob.bank.service.TransactionCsvReader;
import org.rob.bank.service.model.AccountQueryInput;
import org.rob.bank.service.model.RelativeBalanceResult;

/**
 * Runs app.
 */
public final class App {

	/**
	 * Pattern for date format.
	 */
	private static final String FORMAT_STRING = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Common date format across the app.
	 */
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(FORMAT_STRING);

	/**
	 * Launch app.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		App app = new App();
		app.run();
	}

	/**
	 * Start process.
	 */
	private void run() {

		// Read transactions from file.
		String fileName = "/transactionData.csv";
		List<Transaction> transactions = null;
		try {
			transactions = TransactionCsvReader.readFromFile(fileName);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println("Unable to read file " + fileName + " for data. Exiting.");
		}

		// Quit if we failed to read them.
		if (transactions == null) {
			return;
		}

		// Create service that can query the transactions.
		AccountData service = new AccountData(transactions);

		// Loop for eternity or until user cancels, getting input and searching for it,
		// displaying results.
		Scanner inputDevice = new Scanner(System.in);
		System.out.print("Welcome! ");
		while (true) {
			System.out.printf("Please enter search criteria. Control+c to exit at any time.%n%n");
			AccountQueryInput input = obtainSearchCriteria(inputDevice);
			RelativeBalanceResult retrieveRelativeBalance = service.retrieveRelativeBalance(input);
			System.out.printf("%n%s%n", retrieveRelativeBalance);
		}
	}

	/**
	 * @param inputDevice how we get input from user
	 * @return search criteria from user input
	 */
	private AccountQueryInput obtainSearchCriteria(final Scanner inputDevice) {
		AccountQueryInput input = null;

		do {
			System.out.print("accountId: ");
			String accountId = inputDevice.nextLine();
			LocalDateTime from = obtainDate(inputDevice, "from (" + FORMAT_STRING + "): ");
			LocalDateTime to = obtainDate(inputDevice, "to (" + FORMAT_STRING + "): ");
			try {
				input = AccountQueryInput.builder().accountId(accountId).from(from).to(to).build();
			} catch (IllegalStateException ise) {
				System.out.printf("%nInvalid date range (to must be before from). Please try again.%n%n");
			}
		} while (input == null);

		return input;

	}

	/**
	 * @param inputDevice how we get input from user
	 * @param prompt      to display to user
	 * @return date entered by user
	 */
	private LocalDateTime obtainDate(final Scanner inputDevice, final String prompt) {
		LocalDateTime date = null;

		do {
			System.out.print(prompt);
			String dateString = inputDevice.nextLine();
			try {
				date = LocalDateTime.parse(dateString, DATE_FORMAT);
			} catch (DateTimeParseException e) {
				System.out.println("Invalid date format. Need " + FORMAT_STRING + ". Please try again.");
			}

		} while (date == null);

		return date;
	}
}
