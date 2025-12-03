package com.kz.wiki.util;

import java.util.UUID;

/**
 * Utility class for generating unique tenant IDs.
 * In production, you might want to use a more sophisticated approach
 * (e.g., based on company name, domain, etc.)
 */
public class TenantIdGenerator {

    /**
     * Generates a unique tenant ID.
     * Format: prefix-{uuid}
     */
    public static String generate(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "tenant";
        }
        String cleanPrefix = prefix.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return cleanPrefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generates a tenant ID from company name.
     */
    public static String generateFromCompanyName(String companyName) {
        return generate(companyName);
    }
}






