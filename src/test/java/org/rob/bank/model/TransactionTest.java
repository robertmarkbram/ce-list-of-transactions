package org.rob.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rob.bank.model.TransactionType.PAYMENT;
import static org.rob.bank.model.TransactionType.REVERSAL;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rob.bank.model.Transaction;

/**
 * Test equality and validation.
 */
public final class TransactionTest {

	/**
	 * @return data for
	 *         {@link #testEquality(String, Transaction, Transaction, boolean)}.
	 */
	private static Stream<Arguments> dataForTestEquality() {
		final Transaction payment = Transaction.builder()//
				.amount(100.0D)//
				.createdAt(LocalDateTime.now())//
				.fromAccountId("100")//
				.toAccountId("200")//
				.transactionId("100")//
				.transactionType(PAYMENT)//
				.build();

		final Transaction reversal = Transaction.builder()//
				.amount(100.0D)//
				.createdAt(LocalDateTime.now())//
				.fromAccountId("100")//
				.toAccountId("200")//
				.transactionId("100")//
				.transactionType(REVERSAL)//
				.relatedTransaction("50").build();

		return Stream.of(//
				Arguments.of("Object equals itself.", payment, payment, true) //
				, Arguments.of("State is same.", payment, payment.toBuilder().build(), true) //
				, Arguments.of("Amount is different.", payment, payment.toBuilder().amount(1.0).build(), false) //
				, Arguments.of("Create at is different.", payment,
						payment.toBuilder().createdAt(LocalDateTime.now().plusDays(1)).build(), false) //
				, Arguments.of("From account ID is different.", payment, payment.toBuilder().fromAccountId("1").build(),
						false) //
				, Arguments.of("To account ID is different.", payment, //
						payment.toBuilder().toAccountId("1").build(), false) //
				, Arguments.of("Transaction ID is different.", payment, //
						payment.toBuilder().transactionId("1").build(), false) //
				, Arguments.of("Transaction type is different.", payment, reversal, false) //
				, Arguments.of("Related transaction is different.", reversal, reversal.toBuilder()//
						.relatedTransaction("51").build(), false) //
		);
	}

	/**
	 * Test that all fields are used in equality.
	 * 
	 * @param label         for test
	 * @param t1            first transaction to compare for equality
	 * @param t2            second transaction to compare for equality
	 * @param shouldBeEqual should the two transactions be equal?
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestEquality")
	public void testEquality(final String label, final Transaction t1, final Transaction t2,
			final boolean shouldBeEqual) {
		assertEquals(shouldBeEqual, t1.equals(t2), label);
	}

	/**
	 * Payment transactions must not have a {@link Transaction#relatedTransaction}.
	 */
	@Test
	public void testPaymentCannotHaveRelatedTransaction() {
		assertThrows(IllegalStateException.class, () -> {
			Transaction.builder()//
					.amount(100.0D)//
					.createdAt(LocalDateTime.now())//
					.fromAccountId("100")//
					.toAccountId("200")//
					.transactionId("100")//
					.transactionType(PAYMENT)//
					.relatedTransaction("100") // Should not be allowed.
					.build();
		}, "Payment transactions cannot have a related transaction.");

	}

	/**
	 * Reversal transactions must have a {@link Transaction#relatedTransaction}.
	 */
	@Test
	public void testReversalMustHaveRelatedTransaction() {
		assertThrows(IllegalStateException.class, () -> {
			Transaction.builder()//
					.amount(100.0D)//
					.createdAt(LocalDateTime.now())//
					.fromAccountId("100")//
					.toAccountId("200")//
					.transactionId("100")//
					.transactionType(REVERSAL)// Must have related transaction.
					.build();
		}, "Reversal transactions must have a related transaction.");

	}
}
