package org.rob.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.rob.bank.controller.App.DATE_FORMAT;
import static org.rob.bank.model.TransactionType.PAYMENT;
import static org.rob.bank.model.TransactionType.REVERSAL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rob.bank.model.Transaction;
import org.rob.bank.service.AccountData;
import org.rob.bank.service.model.AccountQueryInput;
import org.rob.bank.service.model.RelativeBalanceResult;

/**
 * Test that the {@link AccountData} service queries transactional data as we
 * expect.
 */
public final class AccountDataTest {

	/**
	 * @return data for
	 *         {@link #testRetrieveRelativeBalance(String, List, RelativeBalance, RelativeBalance)}.
	 */
	private static Stream<Arguments> dataForTestRetrieveRelativeBalance() {

		List<Transaction> dataSet1 = testTransactionsSet1();

		AccountQueryInput input33445SmallDateRange = AccountQueryInput.builder()//
				.accountId("ACC334455")//
				.fromString("20/10/2018 12:00:00")//
				.toString("20/10/2018 13:00:00").build();

		AccountQueryInput input33445MediumDateRange = input33445SmallDateRange.toBuilder()//
				.toString("20/10/2018 19:00:00").build();

		AccountQueryInput input33445LargeDateRange = input33445SmallDateRange.toBuilder()//
				.toString("20/10/2019 19:00:00").build();

		AccountQueryInput input33445EarlyDates = AccountQueryInput.builder()//
				.accountId("ACC334455")//
				.fromString("20/10/1900 12:00:00")//
				.toString("20/10/1950 13:00:00").build();

		AccountQueryInput input33445LateDates = AccountQueryInput.builder()//
				.accountId("ACC334455")//
				.fromString("20/10/2300 12:00:00")//
				.toString("20/10/2350 13:00:00").build();
		
		AccountQueryInput input998877LargeDateRange = AccountQueryInput.builder()//
				.accountId("ACC998877")//
				.fromString("20/10/2018 12:00:00")//
				.toString("20/10/2019 13:00:00").build();

		AccountQueryInput inputACC778899LargeDateRange = AccountQueryInput.builder()//
				.accountId("ACC778899")//
				.fromString("20/10/2018 12:00:00")//
				.toString("20/10/2019 13:00:00").build();

		AccountQueryInput inputNoSuchAccount = input998877LargeDateRange.toBuilder()//
				.accountId("I can haz job plz?").build();

		return Stream.of(//
				Arguments.of("ACC334455 One transaction.", dataSet1, input33445SmallDateRange,
						new RelativeBalanceResult(-25.00, 1)) //
				, Arguments.of("ACC334455 Two transactions, one reversed.", dataSet1, //
						input33445MediumDateRange, new RelativeBalanceResult(-25.0, 1)) //
				, Arguments.of("ACC334455 Three transactions, one reversed.", dataSet1, //
						input33445LargeDateRange, new RelativeBalanceResult(-32.25, 2)) //
				, Arguments.of("ACC334455 No transaction; date range early.", dataSet1, //
						input33445EarlyDates, new RelativeBalanceResult()) //
				, Arguments.of("ACC334455 No transaction; date range early.", dataSet1, //
						input33445LateDates, new RelativeBalanceResult()) //
				, Arguments.of("ACC998877 One transaction.", dataSet1, //
						input998877LargeDateRange, new RelativeBalanceResult(-5.0, 1)) //
				, Arguments.of("ACC778899 Three transactions.", dataSet1, //
						inputACC778899LargeDateRange, new RelativeBalanceResult(37.25, 3)) //
				, Arguments.of("No such account.", dataSet1, //
						inputNoSuchAccount, new RelativeBalanceResult()) //
		);
	}

	/**
	 * @return list of transactions we can test against
	 */
	private static List<Transaction> testTransactionsSet1() {
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

	/**
	 * Test that {@link AccountData#retrieveRelativeBalance(RelativeBalance)} is
	 * correctly implemented.
	 * 
	 * @param label          for test
	 * @param sourceData     transactions that will be injected into service for
	 *                       testing
	 * @param input          search criteria
	 * @param expectedOutput what we expect to see resulting from the search
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestRetrieveRelativeBalance")
	public void testRetrieveRelativeBalance(final String label, final List<Transaction> sourceData,
			final AccountQueryInput input, final RelativeBalanceResult expectedOutput) {
		AccountData dataService = new AccountData(sourceData);
		assertEquals(expectedOutput, dataService.retrieveRelativeBalance(input), label);
	}
}
