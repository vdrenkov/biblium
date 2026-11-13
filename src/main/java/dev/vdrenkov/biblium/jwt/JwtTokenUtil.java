package dev.vdrenkov.biblium.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_TOKEN_VALIDITY;

@Component
public class JwtTokenUtil {
    private final SecretKey signingKey;
    private final JwtParser parser;

    public JwtTokenUtil(@Value("${jwt.secret}") final String secret) {
        final byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("JWT_SECRET must contain at least 64 UTF-8 bytes");
        }

        signingKey = Keys.hmacShaKeyFor(keyBytes);
        parser = Jwts.parser()
            .verifyWith(signingKey)
            .sig()
            .clear()
            .add(Jwts.SIG.HS512)
            .and()
            .build();
    }

    public String generateToken(final UserDetails userDetails) {
        final Instant issuedAt = Instant.now();
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(issuedAt.plusSeconds(JWT_TOKEN_VALIDITY)))
            .signWith(signingKey, Jwts.SIG.HS512)
            .compact();
    }

    public String getUsernameFromToken(final String token) {
        final var claims = parser.parseSignedClaims(token).getPayload();
        final String username = claims.getSubject();

        if (username == null || username.isBlank() || claims.getExpiration() == null) {
            throw new JwtException("Authentication token requires a subject and expiry");
        }

        return username;
    }
}
