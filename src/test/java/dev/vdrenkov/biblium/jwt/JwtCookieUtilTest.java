package dev.vdrenkov.biblium.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.security.core.userdetails.UserDetails;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtCookieUtilTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetails userDetails;

    @Test
    void testCreateJWTCookie_appliesSecurityAttributes() {
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token");
        final JwtCookieUtil jwtCookieUtil = new JwtCookieUtil(jwtTokenUtil, true, "Strict");

        final HttpCookie cookie = jwtCookieUtil.createJWTCookie(userDetails);
        final String cookieHeader = cookie.toString();

        assertTrue(cookieHeader.contains(JWT_COOKIE_NAME + "=jwt-token"));
        assertTrue(cookieHeader.contains("Path=/"));
        assertTrue(cookieHeader.contains("HttpOnly"));
        assertTrue(cookieHeader.contains("Secure"));
        assertTrue(cookieHeader.contains("SameSite=Strict"));
    }
}

