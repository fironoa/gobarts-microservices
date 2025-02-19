package com.gobartsdev.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "mySuperSecretKey12345678901234567890"; // Change this!
    private static final long RT_EXPIRATION_TIME = 1000 * 60 * 20; // 10 hours
    private static final long AT_EXPIRATION_TIME = 1000 * 60 * 15;

    private final Key key;

    public JwtUtil() {
        // Create a signing key from your secret key (Base64-encoded)
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AT_EXPIRATION_TIME)) // 10 hours expiration
                .signWith(key)
                .compact();
    }

    // Method to parse token and retrieve claims
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Method to check token expiration
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Method to extract the username from the token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Validate the token
    public boolean isAValidToken(String token) {
        String username = extractUsername(token);
        return username != null && !isTokenExpired(token);
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + RT_EXPIRATION_TIME)) // 10 hours expiration
                .signWith(key)
                .compact();
    }
}
