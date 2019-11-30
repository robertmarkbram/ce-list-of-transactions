package org.rob.bank.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.rob.bank.controller.App.DATE_FORMAT;
import static org.rob.bank.model.TransactionType.PAYMENT;
import static org.rob.bank.model.TransactionType.REVERSAL;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.rob.bank.model.Transaction;
import org.rob.bank.service.TransactionCsvReader;

/**
 * Test that {@link TransactionCsvReader} can read from a file.
 */
public final class TransactionCsvReaderTest {

	/**
	 * Test reading from a file.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testFromFile() throws URISyntaxException {

		final String file = "/testData.csv";
		assertDoesNotThrow(() -> {
			List<Transaction> actual = TransactionCsvReader.readFromFile(file);
			assertEquals(expectedTestData(), actual);
		}, "Reading from " + file + " should not throw any exception.");
	}

	/**
	 * @return what we expect to read from the test file.
	 */
	private static List<Transaction> expectedTestData() {
		return List.of(//
				Transaction.builder()//
						.transactionId("TX10001")//
						.fromAccountId("ACC334455")//
						.toAccountId("ACC778899")//
						.createdAt(LocalDateTime.parse("20/10/2018 12:47:55", DATE_FORMAT))//
						.amount(25.00D)//
						.transactionType(PAYMENT) //
						.build(),
				Transaction.builder()//
						.transactionId("TX10002")//
						.fromAccountId("ACC334455")//
						.toAccountId("ACC998877")//
						.createdAt(LocalDateTime.parse("20/10/2018 17:33:43", DATE_FORMAT))//
						.amount(10.50D)//
						.transactionType(PAYMENT) //
						.build(),
				Transaction.builder()//
						.transactionId("TX10003")//
						.fromAccountId("ACC998877")//
						.toAccountId("ACC778899")//
						.createdAt(LocalDateTime.parse("20/10/2018 18:00:00", DATE_FORMAT))//
						.amount(5.00D)//
						.transactionType(PAYMENT) //
						.build(),
				Transaction.builder()//
						.transactionId("TX10004")//
						.fromAccountId("ACC334455")//
						.toAccountId("ACC998877")//
						.createdAt(LocalDateTime.parse("20/10/2018 19:45:00", DATE_FORMAT))//
						.amount(10.50D)//
						.transactionType(REVERSAL)//
						.relatedTransaction("TX10002") //
						.build(),
				Transaction.builder()//
						.transactionId("TX10005")//
						.fromAccountId("ACC334455")//
						.toAccountId("ACC778899")//
						.createdAt(LocalDateTime.parse("21/10/2018 09:30:00", DATE_FORMAT))//
						.amount(7.25D)//
						.transactionType(PAYMENT) //
						.build());
	}

}
