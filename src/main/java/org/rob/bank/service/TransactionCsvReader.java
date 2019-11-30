package org.rob.bank.service;

import static org.rob.bank.controller.App.DATE_FORMAT;
import static org.rob.bank.model.TransactionType.REVERSAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.rob.bank.model.Transaction;
import org.rob.bank.model.TransactionType;
import org.rob.bank.model.Transaction.TransactionBuilder;

/**
 * Read {@link Transaction}s from a comma separated value file.
 */
public final class TransactionCsvReader {

	/** Index to field transactionId on CSV record. */
	private static final int INDEX_TRANSACTION_ID = 0;

	/** Index to field fromAccountId on CSV record. */
	private static final int INDEX_FROM_ACCOUNT_ID = 1;

	/** Index to field toAccountId on CSV record. */
	private static final int INDEX_TO_ACCOUNT_ID = 2;

	/** Index to field createdAt on CSV record. */
	private static final int INDEX_CREATED_AT = 3;

	/** Index to field amount on CSV record. */
	private static final int INDEX_AMOUNT = 4;

	/** Index to field transactionType on CSV record. */
	private static final int INDEX_TRANSACTION_TYPE = 5;

	/** Index to field relatedTransaction on CSV record. */
	private static final int INDEX_RELATED_TRANSACTION = 6;

	/**
	 * @param csvFile comma separated value file containing transaction data.
	 * @return unmodifiable list of {@link Transaction}s in the same order in which
	 *         they appear in the <code>csvFile</code>
	 * @throws IOException if we cannot read from the file for some reason.
	 */
	public static List<Transaction> readFromFile(final String csvFilePath) throws IOException {
		try (InputStream resource = TransactionCsvReader.class.getResourceAsStream(csvFilePath)) {
			return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))//
					.lines().skip(1)// Skip header.
					.map(TransactionCsvReader::fromSingleLine)//
					.collect(Collectors.toUnmodifiableList());
		}
	}

	/**
	 * @param csvLine single line of a CSV file
	 * @return {@link Transaction} from those values in <code>csvLine</code>
	 */
	public static Transaction fromSingleLine(final String csvLine) {

		String[] fields = Arrays.stream(csvLine.split(",")).map(String::trim).toArray(String[]::new);

		TransactionType transactionType = TransactionType.valueOf(fields[INDEX_TRANSACTION_TYPE]);

		TransactionBuilder transactionBuilder = Transaction.builder()//
				.transactionId(fields[INDEX_TRANSACTION_ID])//
				.fromAccountId(fields[INDEX_FROM_ACCOUNT_ID])//
				.toAccountId(fields[INDEX_TO_ACCOUNT_ID])//
				.createdAt(LocalDateTime.parse(fields[INDEX_CREATED_AT], DATE_FORMAT))//
				.amount(Double.valueOf(fields[INDEX_AMOUNT]))//
				.transactionType(transactionType);

		if (transactionType.equals(REVERSAL)) {
			transactionBuilder.relatedTransaction(fields[INDEX_RELATED_TRANSACTION]);
		}

		return transactionBuilder.build();

	}

}
