package dev.vdrenkov.biblium.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenUtilTest {
    private static final String SECRET = "test-signing-secret-with-at-least-sixty-four-bytes-for-hs512-signatures";
    private JwtTokenUtil tokenUtil;

    @BeforeEach
    void setUp() {
        tokenUtil = new JwtTokenUtil(SECRET);
    }

    @Test
    void jwtTokenUtil_shortSigningSecret_rejectsConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenUtil("too-short"));
    }

    @Test
    void generateToken_staffAccount_returnsVerifiableToken() {
        final var user = User.withUsername("librarian").password("encoded").authorities("LIBRARIAN").build();
        final String token = tokenUtil.generateToken(user);
        assertEquals("librarian", tokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_missingExpiry_rejectsToken() {
        final String token = Jwts.builder()
            .subject("librarian")
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512)
            .compact();

        assertThrows(JwtException.class, () -> tokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_missingSubject_rejectsToken() {
        final String token = Jwts.builder()
            .expiration(Date.from(Instant.now().plusSeconds(60)))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512)
            .compact();

        assertThrows(JwtException.class, () -> tokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_expiredToken_rejectsToken() {
        final String token = Jwts.builder()
            .subject("librarian")
            .expiration(Date.from(Instant.now().minusSeconds(60)))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512)
            .compact();

        assertThrows(JwtException.class, () -> tokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_wrongSigningKey_rejectsToken() {
        final String token = Jwts.builder()
            .subject("librarian")
            .expiration(Date.from(Instant.now().plusSeconds(60)))
            .signWith(Jwts.SIG.HS512.key().build())
            .compact();

        assertThrows(JwtException.class, () -> tokenUtil.getUsernameFromToken(token));
    }
}
