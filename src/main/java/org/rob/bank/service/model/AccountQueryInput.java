package org.rob.bank.service.model;

import static org.rob.bank.controller.App.DATE_FORMAT;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Input to an account query.
 */
@Data
@Builder(toBuilder = true, buildMethodName = "buildInternal")
@EqualsAndHashCode
public final class AccountQueryInput {

	/**
	 * ID of the account being examined.
	 */
	@NonNull
	private final String accountId;

	/**
	 * Start of date range being examined.
	 */
	@NonNull
	private final LocalDateTime from;

	/**
	 * End of date range being examined.
	 */
	@NonNull
	private final LocalDateTime to;

	/**
	 * Validate state of query input.
	 */
	public void validate() {
		if (to.isBefore(from)) {
			throw new IllegalStateException("From date [" + from + "] must be before to date [" + to + "].");
		}
	}

	/** Custom builder to provide validation and easier date entry. */
	public static class AccountQueryInputBuilder {

		private LocalDateTime from;

		private LocalDateTime to;

		public AccountQueryInputBuilder fromString(final String from) {
			this.from = LocalDateTime.parse(from, DATE_FORMAT);
			return this;
		}

		public AccountQueryInputBuilder toString(final String to) {
			this.to = LocalDateTime.parse(to, DATE_FORMAT);
			return this;
		}

		public AccountQueryInput build() {
			AccountQueryInput accountQueryInput = this.buildInternal();
			accountQueryInput.validate();
			return accountQueryInput;
		}
	}

}
