package com.mvs.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return this.extractClaims(token).getSubject();
    }

    public String extractUserRole(String token) {
        return this.extractClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = this.extractClaims(token);
            if (claims.getExpiration().after(new Date())) {
                return true;
            }
        } catch (JwtException e) {
            throw e;
        }
        return false;
    }

}
