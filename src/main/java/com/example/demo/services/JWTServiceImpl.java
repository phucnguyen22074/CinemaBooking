package com.example.demo.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

@Service
public class JWTServiceImpl implements JWTService {

    @Autowired
    private Environment environment;

    @Override
    public String generateToken(String username) {
        try {
            String secret = environment.getProperty("secret");
            if (secret == null || secret.isEmpty()) {
                throw new IllegalStateException("JWT secret key is missing in configuration");
            }
            long expiry = Long.parseLong(environment.getProperty("expiry", "3600000")); // 1 hour default
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiry);

            return Jwts.builder()
                    .subject(username)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    @Override
    public String getUsernameFromJWT(String token) {
        try {
            String secret = environment.getProperty("secret");
            if (secret == null || secret.isEmpty()) {
                throw new IllegalStateException("JWT secret key is missing in configuration");
            }

            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty", e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    @Override
    public boolean validToken(String token) {
        try {
            String secret = environment.getProperty("secret");
            if (secret == null || secret.isEmpty()) {
                throw new IllegalStateException("JWT secret key is missing in configuration");
            }

            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid token format: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid token signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Empty token string: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
        }
        return false;
    }
}
