package org.framework.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public final class JwtValidator {

    private final Key key;
    private final long allowedClockSkewSeconds;

    public JwtValidator(String hmacSecret, long allowedClockSkewSeconds) {
        if (hmacSecret == null || hmacSecret.length() < 32) {
            throw new IllegalArgumentException("HMAC secret missing or too short (>=32 bytes recommended)");
        }
        this.key = Keys.hmacShaKeyFor(hmacSecret.getBytes(StandardCharsets.UTF_8));
        this.allowedClockSkewSeconds = Math.max(0, allowedClockSkewSeconds);
    }

    /** Validates signature and standard time claims (exp/nbf/iat). Returns Claims if OK. */
    public Claims validateBearer(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring("Bearer ".length()).trim();

        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(allowedClockSkewSeconds)
                .build()
                .parseClaimsJws(token);

        return jws.getBody();
    }
}
