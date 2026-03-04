package com.pfe.commons.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class ClaimNumber {
    private final String value;

    private ClaimNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Claim number cannot be null or empty");
        }
        this.value = value.toUpperCase().trim();
    }

    public static ClaimNumber of(String value) {
        return new ClaimNumber(value);
    }

    public static ClaimNumber generate() {
        String year = String.valueOf(LocalDate.now().getYear());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new ClaimNumber("CLM-" + year + "-" + uuid);
    }

    @Override
    public String toString() {
        return value;
    }
}
