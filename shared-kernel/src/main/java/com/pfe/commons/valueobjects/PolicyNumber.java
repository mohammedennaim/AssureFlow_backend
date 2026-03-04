package com.pfe.commons.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PolicyNumber {
    private final String value;

    private PolicyNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy number cannot be null or empty");
        }
        this.value = value.toUpperCase().trim();
    }

    public static PolicyNumber of(String value) {
        return new PolicyNumber(value);
    }

    public static PolicyNumber generate() {
        String year = String.valueOf(LocalDate.now().getYear());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PolicyNumber("POL-" + year + "-" + uuid);
    }

    @Override
    public String toString() {
        return value;
    }
}
