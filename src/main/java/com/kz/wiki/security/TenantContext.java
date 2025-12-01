package com.kz.wiki.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to access tenant context from security context.
 * The tenant ID is stored in JWT claims and can be extracted during authentication.
 */
public class TenantContext {

    private static final ThreadLocal<String> tenantIdHolder = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        tenantIdHolder.set(tenantId);
    }

    public static String getTenantId() {
        String tenantId = tenantIdHolder.get();
        if (tenantId == null) {
            // Try to extract from security context if available
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getTenantId();
            }
        }
        return tenantId;
    }

    public static void clear() {
        tenantIdHolder.remove();
    }
}






