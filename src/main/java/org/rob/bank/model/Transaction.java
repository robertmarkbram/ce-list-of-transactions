package org.rob.bank.model;

import static org.rob.bank.model.TransactionType.PAYMENT;
import static org.rob.bank.model.TransactionType.REVERSAL;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Financial transaction between two accounts.
 */
@Data
@Builder(toBuilder = true, buildMethodName = "buildInternal")
@EqualsAndHashCode
public final class Transaction {

	/**
	 * ID for the transaction.
	 */
	private final String transactionId;

	/**
	 * ID of account that funds are coming from.
	 */
	@NonNull
	private final String fromAccountId;

	/**
	 * ID of account that funds are going to.
	 */
	@NonNull
	private final String toAccountId;

	/**
	 * Date account was created.
	 */
	@NonNull
	private final LocalDateTime createdAt;

	/**
	 * Amount of money being transferred.
	 */
	@NonNull
	private final Double amount;

	/**
	 * What kind of transaction is occurring.
	 */
	@NonNull
	private final TransactionType transactionType;

	/**
	 * If the transaction refers to a another transaction, which one is it referring
	 * to.
	 */
	private final String relatedTransaction;

	/**
	 * Validate state of transaction.
	 */
	public void validate() {
		if (transactionType.equals(PAYMENT) && relatedTransaction != null) {
			throw new IllegalStateException("Transaction type " + PAYMENT + " should have no related transaction (has "
					+ relatedTransaction + ").");
		} else if (transactionType.equals(REVERSAL) && relatedTransaction == null) {
			throw new IllegalStateException(
					"Transaction type " + REVERSAL + " must have a related transaction (has none).");
		}
	}

	/** Custom builder to provide validation. */
	public static class TransactionBuilder {

		public Transaction build() {
			Transaction transaction = this.buildInternal();
			transaction.validate();
			return transaction;
		}
	}
	
	public static void main(String[] args) {
		DecimalFormat format = new DecimalFormat("$#,##0.00;-$#,##0.00");
		System.out.println(format.format(15.5).toString());
		System.out.println(format.format(-15.5).toString());
	}
	
}
