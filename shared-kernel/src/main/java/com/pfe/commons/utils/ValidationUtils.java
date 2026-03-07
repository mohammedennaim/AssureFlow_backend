package com.pfe.commons.utils;

import com.pfe.commons.exceptions.BusinessException;

import java.math.BigDecimal;

public final class ValidationUtils {

    private ValidationUtils() {
        // Prevent instantiation
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " cannot be null");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(fieldName + " cannot be null or empty");
        }
    }

    public static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(fieldName + " must be a positive number");
        }
    }
}
