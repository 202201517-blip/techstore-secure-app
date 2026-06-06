package com.techstoresecureapp.security;

import com.techstoresecureapp.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    public String generateToken(AppUser user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole()
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Object userId = extractClaims(token).get("userId");

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }

        if (userId instanceof Long) {
            return (Long) userId;
        }

        return Long.valueOf(userId.toString());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}