package com.mycompany.twilio.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RetryWindowTest {

    @Test
    void shouldRetryWhenScheduledTimeHasArrived() {
        Instant now = Instant.parse("2026-07-14T10:00:00Z");
        Instant nextAttemptAt = now.minusSeconds(1);

        assertTrue(FailedMessageResender.shouldRetry(now, nextAttemptAt, 5));
    }

    @Test
    void shouldNotRetryBeforeScheduledTime() {
        Instant now = Instant.parse("2026-07-14T10:00:00Z");
        Instant nextAttemptAt = now.plusSeconds(60);

        assertFalse(FailedMessageResender.shouldRetry(now, nextAttemptAt, 5));
    }

    @Test
    void shouldUseDefaultValidityPeriodWhenValueIsInvalid() {
        Instant now = Instant.parse("2026-07-14T10:00:00Z");
        Instant nextAttemptAt = now.minusSeconds(1);

        assertTrue(FailedMessageResender.shouldRetry(now, nextAttemptAt, 0));
    }
}
