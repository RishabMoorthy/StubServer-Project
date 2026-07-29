package org.framework.utils;

import io.jsonwebtoken.Claims;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Authz {

    public static String getUsername(Claims claims) {
        // Prefer 'sub' if your IdP sets it; fall back to custom 'username'
        String sub = claims.getSubject();
        if (sub != null && !sub.isBlank())
            return sub;
        Object u = claims.get("username");
        return u != null ? u.toString() : null;
    }

    public static Set<String> getRoles(Claims claims) {
        // Your token uses a single 'userRole' string (e.g., "Admin")
        Object role = claims.get("userRole");
        if (role == null)
            return Collections.emptySet();
        Set<String> s = new HashSet<>();
        s.add(role.toString());
        return s;
    }

    public static boolean hasAnyRole(Set<String> userRoles, String... required) {
        for (String r : required) {
            if (userRoles.contains(r))
                return true;
        }
        return false;
    }

    public static boolean isKnownActiveUser(String username) {
        // Replace with a real check (DB/service). For now, basic non-empty check:
        return username != null && !username.isBlank();
    }
}
