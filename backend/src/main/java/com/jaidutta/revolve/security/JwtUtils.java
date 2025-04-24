package com.jaidutta.revolve.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component // Make it a Spring Bean
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}") // Inject secret from properties/env vars
    private String jwtSecretString;

    @Value("${app.jwt.expirationMs}") // Inject expiration time
    private int jwtExpirationMs;

    // Method to generate the SecretKey object from the string property
    private SecretKey key() {
        // Make sure your jwtSecretString is Base64 encoded and long enough for the algorithm
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecretString);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT secret key: {}", e.getMessage());
            // Handle error appropriately - maybe throw a specific exception
            // or ensure the key is validated on startup
            throw new RuntimeException("Invalid JWT key configuration", e);
        }
    }

    // Method to generate the JWT string
    public String generateToken(Authentication authentication) {
        // The authenticated user's details (usually includes username/email)
        String username = authentication.getName();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        logger.info("Generating token for user: {}", username);

        // Build the JWT
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                // Correct way: Pass the SecretKey and the desired Algorithm enum
                .signWith(key()) // Or HS256, HS384
                .compact();
    }

    boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // This can happen if the token string is empty or null
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            // Catch any other potential parsing errors
            logger.error("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

}