package com.gobartsdev.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mySuperSecretKey12345678901234567890"; // Change this!
    private static final long RT_EXPIRATION_TIME = 1000 * 60 * 20; // 10 hours
    private static final long AT_EXPIRATION_TIME = 1000 * 60 * 15;

    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String username, Set<String> roles) {
        return generateToken(username, roles, AT_EXPIRATION_TIME);
    }

    public String generateToken(String username, Set<String> roles, Long expireTime) {
        List<String> roleList = roles == null ? List.of() : roles.stream().toList();

        Claims claims = Jwts.claims().subject(username).add(
                "roles", roleList
        ).build();

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime)) // 10 hours expiration
                .signWith(key)
                .compact();
    }



    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isAValidToken(String token) {
        String username = extractUsername(token);
        return username != null && !isTokenExpired(token);
    }

    public String generateRefreshToken(String username, Set<String> roles) {
        return generateToken(username, roles, RT_EXPIRATION_TIME);
    }

    public List<String> getRolesFromToken(String token){
        Claims claims = extractClaims(token);
        Object authoritiesObj = claims.get("roles");
        List<String> authorities = Collections.emptyList(); // Default to empty list

        if (authoritiesObj instanceof List<?>) {
            authorities = ((List<?>) authoritiesObj).stream()
                    .filter(String.class::isInstance) //added to prevent ClassCastException
                    .map(String.class::cast)
                    .toList();
        }
        return authorities;

    }
}
