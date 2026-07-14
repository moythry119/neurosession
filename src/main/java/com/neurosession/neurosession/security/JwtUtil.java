package com.neurosession.neurosession.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component                         // Spring manages this as a bean
public class JwtUtil {

    @Value("${app.jwt.secret}")    // reads the secret from application.yml
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // converts the plain-text secret into a cryptographic key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // creates a JWT token for a given email
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                              // who the token belongs to
                .issuedAt(new Date())                        // when it was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // when it expires
                .signWith(getSigningKey())                   // sign it with our secret
                .compact();                                  // build the string
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}