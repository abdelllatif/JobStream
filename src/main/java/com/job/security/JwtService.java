package com.job.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    "jobstream-secret-key-jobstream-secret-key"
                            .getBytes(StandardCharsets.UTF_8)
            );

    private final long EXPIRATION = 86400000; // 24 hours
    private final long REFRESH_EXPIRATION = 604800000; // 7 days

    public String generateToken(String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
