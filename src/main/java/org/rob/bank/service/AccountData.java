package org.rob.bank.service;

import static org.rob.bank.model.TransactionType.REVERSAL;

import java.util.List;
import java.util.stream.Collectors;

import org.rob.bank.model.Transaction;
import org.rob.bank.service.model.AccountQueryInput;
import org.rob.bank.service.model.RelativeBalanceResult;

/**
 * Queries account data.
 */
public final class AccountData {

	/**
	 * List of transactions this service will query.
	 */
	private final List<Transaction> transactions;

	/**
	 * @param transactions data to initialise the service with
	 */
	public AccountData(final List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * @param query input parameters to search: taking account number and date range
	 *              from <code>query</code>
	 * @return result, which includes total amount and count of transactions.
	 */
	public RelativeBalanceResult retrieveRelativeBalance(final AccountQueryInput query) {

		// Get list of all transactions for the given account ID and date range.
		List<Transaction> transactionsInDateRange = findTransactions(query);

		// Reduce the list to a result: total relative amount and count of transactions.
		return transactionsInDateRange.stream()//
				.reduce(new RelativeBalanceResult(), // Identity.
						// Accumulator
						(relativeBalance, transaction) -> relativeBalance.addTransaction(query, transaction),
						// Combiner
						(relativeBalance1, relativeBalance2) -> relativeBalance1.addResult(relativeBalance2));
	}

	/**
	 * @param query includes account ID and date range
	 * @return all transactions for the given account ID and date range
	 */
	private List<Transaction> findTransactions(final AccountQueryInput query) {
		// Get list of all transactions for the given account ID and date range.
		return transactions.stream()
				// Get transactions in the date range.
				.dropWhile(transaction -> transaction.getCreatedAt().isBefore(query.getFrom())) //
				.takeWhile(transaction -> transaction.getCreatedAt().isBefore(query.getTo())) //
				// Transaction must be to or from selected account.
				.filter(transaction -> transaction.getFromAccountId().equals(query.getAccountId())
						|| transaction.getToAccountId().equals(query.getAccountId()))//
				// Ignore transactions that were reversed, even if reversed after to date.
				.filter(this::isTransactionCurrent)
				// Get them all in a list.
				.collect(Collectors.toList());
	}

	/**
	 * @param transaction that may or may not have been reversed
	 * @return true if we should consider the transaction; false otherwise e.g. it
	 *         was reversed.
	 */
	private boolean isTransactionCurrent(final Transaction transaction) {
		// Consider transactions from this transaction onwards.
		return transactions.subList(transactions.indexOf(transaction), transactions.size() - 1).stream()
				// Look for transactions that reverse the one being considered.
				.filter(tx2 -> tx2.getTransactionType().equals(REVERSAL))
				.filter(tx2 -> tx2.getRelatedTransaction().equals(transaction.getTransactionId()))
				// Keep transaction if we find no reversal.
				.findFirst().isEmpty();
	}

}
