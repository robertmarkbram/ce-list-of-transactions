package org.rob.bank.model;

/**
 * What type of transaction is this.
 */
public enum TransactionType {

	/**
	 * Funds being paid from an account.
	 */
	PAYMENT,

	/**
	 * Reversing a previous transaction.
	 */
	REVERSAL;
}
