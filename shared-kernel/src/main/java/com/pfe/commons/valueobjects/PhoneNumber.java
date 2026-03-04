package com.pfe.commons.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class PhoneNumber {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    private final String value;

    private PhoneNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        String cleaned = value.replaceAll("[\\s-()]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
        this.value = cleaned;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
