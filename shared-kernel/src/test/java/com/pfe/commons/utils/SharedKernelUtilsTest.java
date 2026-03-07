package com.pfe.commons.utils;

import com.pfe.commons.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityUtils and ValidationUtils in shared-kernel.
 */
class SharedKernelUtilsTest {

    @Nested
    @DisplayName("SecurityUtils Tests")
    class SecurityUtilsTests {

        @Test
        @DisplayName("Should mask PII string")
        void shouldMaskPII() {
            assertEquals("Mo***ed", SecurityUtils.maskPII("Mohammed"));
        }

        @Test
        @DisplayName("Should return *** for short string")
        void shouldMaskShortString() {
            assertEquals("***", SecurityUtils.maskPII("AB"));
        }

        @Test
        @DisplayName("Should return *** for null")
        void shouldMaskNull() {
            assertEquals("***", SecurityUtils.maskPII(null));
        }

        @Test
        @DisplayName("Should mask email address")
        void shouldMaskEmail() {
            assertEquals("us***er@example.com", SecurityUtils.maskEmail("user@example.com"));
        }

        @Test
        @DisplayName("Should return *** for invalid email")
        void shouldMaskInvalidEmail() {
            assertEquals("***", SecurityUtils.maskEmail("notanemail"));
        }

        @Test
        @DisplayName("Should mask UUID showing only last 4 chars")
        void shouldMaskUUID() {
            String uuid = "550e8400-e29b-41d4-a716-446655440000";
            assertEquals("***0000", SecurityUtils.maskUUID(uuid));
        }
    }

    @Nested
    @DisplayName("ValidationUtils Tests")
    class ValidationUtilsTests {

        @Test
        @DisplayName("Should pass for non-null value")
        void shouldPassNonNull() {
            assertDoesNotThrow(() -> ValidationUtils.validateNotNull("value", "field"));
        }

        @Test
        @DisplayName("Should throw for null value")
        void shouldThrowForNull() {
            assertThrows(BusinessException.class, () -> ValidationUtils.validateNotNull(null, "field"));
        }

        @Test
        @DisplayName("Should pass for non-blank string")
        void shouldPassNonBlank() {
            assertDoesNotThrow(() -> ValidationUtils.validateNotBlank("value", "field"));
        }

        @Test
        @DisplayName("Should throw for blank string")
        void shouldThrowForBlank() {
            assertThrows(BusinessException.class, () -> ValidationUtils.validateNotBlank("  ", "field"));
        }

        @Test
        @DisplayName("Should throw for null string")
        void shouldThrowForNullString() {
            assertThrows(BusinessException.class, () -> ValidationUtils.validateNotBlank(null, "field"));
        }

        @Test
        @DisplayName("Should pass for positive BigDecimal")
        void shouldPassPositive() {
            assertDoesNotThrow(() -> ValidationUtils.validatePositive(new BigDecimal("100"), "amount"));
        }

        @Test
        @DisplayName("Should throw for zero BigDecimal")
        void shouldThrowForZero() {
            assertThrows(BusinessException.class, () -> ValidationUtils.validatePositive(BigDecimal.ZERO, "amount"));
        }

        @Test
        @DisplayName("Should throw for negative BigDecimal")
        void shouldThrowForNegative() {
            assertThrows(BusinessException.class,
                    () -> ValidationUtils.validatePositive(new BigDecimal("-1"), "amount"));
        }
    }
}
