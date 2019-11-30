package org.rob.bank.service.model;

import java.text.DecimalFormat;

import org.rob.bank.model.Transaction;
import org.rob.bank.model.TransactionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Relative account balance (from examining transactions for an account over a
 * given date range) and count of un-reversed transactions during that period.
 */
@Data
@EqualsAndHashCode
public final class RelativeBalanceResult {

	/**
	 * Format dollars amounts.
	 */
	private static final DecimalFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00;-$#,##0.00");

	/**
	 * Amount of the relative balance for the period.
	 */
	private final Double amount;

	/**
	 * Number of un-reversed transactions during that period.
	 */
	private final Integer countTransactions;

	/**
	 * Creates with starting count and amount of 0.
	 */
	public RelativeBalanceResult() {
		this.amount = 0.0;
		this.countTransactions = 0;
	}

	/**
	 * Creates with given count and amount.
	 * 
	 * @param amount            starting amount
	 * @param countTransactions starting count
	 */
	public RelativeBalanceResult(final Double amount, final Integer countTransactions) {
		this.amount = amount;
		this.countTransactions = countTransactions;
	}

	/**
	 * Update count of transactions and amount for non reversal transactions.
	 * 
	 * @param query that lets us know which account we are looking at
	 * @param transaction that we will count as part of this result.
	 */
	public RelativeBalanceResult addTransaction(final AccountQueryInput query, final Transaction transaction) {
		if (transaction.getTransactionType().equals(TransactionType.REVERSAL)) {
			return this;
		}
		// If transaction is to this account, it's an amount received; otherwise it's a payment out.
		if (query.getAccountId().equals(transaction.getToAccountId())) {
			return new RelativeBalanceResult(amount + transaction.getAmount(), countTransactions + 1);
		} else {
			return new RelativeBalanceResult(amount - transaction.getAmount(), countTransactions + 1);
		}
	}

	/**
	 * Return new result that is a sum of this and the given one.
	 * 
	 * @param result that will be added to this and returned in a new object
	 */
	public RelativeBalanceResult addResult(final RelativeBalanceResult result) {
		return new RelativeBalanceResult(//
				amount + result.getAmount(), //
				countTransactions + result.getCountTransactions());
	}

	@Override
	public String toString() {
		return String.format("Relative balance for the period is: %s%n" //
				+ "Number of transactions included is: %d%n", //
				DOLLAR_FORMAT.format(amount).toString(), countTransactions);
	}

}
