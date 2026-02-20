package com.springcore.ai.scai_platform.security;

import com.springcore.ai.scai_platform.dto.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContext {

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UserPrincipal getPrincipal() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) auth.getPrincipal();
        }
        return null;
    }

    public static String getUserName() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
    }

    public static Long getUserId() {
        UserPrincipal principal = getPrincipal();
        return (principal != null) ? principal.getId() : null;
    }

    public static Long getEmId(Long userId) {
        UserPrincipal principal = getPrincipal();
        return (principal != null) ? principal.getEmId() : null;
    }
}