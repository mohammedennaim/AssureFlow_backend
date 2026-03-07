package com.pfe.commons.utils;

public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

  
    public static String maskPII(String value) {
        if (value == null || value.length() < 4) {
            return "***";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        return maskPII(parts[0]) + "@" + parts[1];
    }

    public static String maskUUID(String uuid) {
        if (uuid == null || uuid.length() < 4) {
            return "***";
        }
        return "***" + uuid.substring(uuid.length() - 4);
    }
}
