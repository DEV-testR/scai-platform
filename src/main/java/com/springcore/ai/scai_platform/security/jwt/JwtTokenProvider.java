package com.springcore.ai.scai_platform.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Data
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-in-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration-in-ms}")
    private long refreshTokenExpirationMs;

    private Key signingKey;

    // private final CustomUserDetailsService userDetailsService;

    /*public JwtTokenProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }*/

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        return buildToken(email, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, refreshTokenExpirationMs);
    }

    private String buildToken(String subject, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT expired: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT format: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("JWT validation error: " + ex.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
