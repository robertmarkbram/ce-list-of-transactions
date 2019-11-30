package org.rob.bank.service.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.rob.bank.service.model.AccountQueryInput;

/**
 * Test validation.
 */
public final class AccountQueryInputTest {

	/**
	 * Test that we cannot set {@link RelativeBalance#getTo()} before
	 * {@link RelativeBalance#getFrom()}.
	 */
	@Test
	public void testToBeforeFrom() {
		assertThrows(IllegalStateException.class, () -> {
			AccountQueryInput.builder()//
					.accountId("foo")//
					.from(LocalDateTime.now().plusDays(1))//
					.to(LocalDateTime.now().minusDays(1))//
					.build();
		}, "Input criteria should not allow to date before from.");

	}

	/**
	 * Test that we can set {@link RelativeBalance#getFrom()} before
	 * {@link RelativeBalance#getTo()}.
	 */
	@Test
	public void testFromBeforeTo() {
		assertDoesNotThrow(() -> {
			AccountQueryInput.builder()//
					.accountId("foo")//
					.from(LocalDateTime.now().minusDays(1))//
					.to(LocalDateTime.now().plusDays(1))//
					.build();
		}, "Input criteria should allow from date before to.");

	}

}
