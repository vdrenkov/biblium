package dev.vdrenkov.biblium.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;
import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_TOKEN_VALIDITY;

@Component
public class JwtCookieUtil {
    private static final String COOKIE_PATH = "/";

    private final JwtTokenUtil tokenUtil;
    private final boolean secureCookie;
    private final String sameSitePolicy;

    public JwtCookieUtil(final JwtTokenUtil tokenUtil, @Value("${jwt.cookie.secure:false}") final boolean secureCookie,
        @Value("${jwt.cookie.same-site:Lax}") final String sameSitePolicy) {
        this.tokenUtil = tokenUtil;
        this.secureCookie = secureCookie;
        this.sameSitePolicy = sameSitePolicy;
    }

    public HttpCookie createJWTCookie(final UserDetails userDetails) {
        final String jwt = tokenUtil.generateToken(userDetails);

        return ResponseCookie
            .from(JWT_COOKIE_NAME, jwt)
            .maxAge(JWT_TOKEN_VALIDITY)
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite(sameSitePolicy)
            .path(COOKIE_PATH)
            .build();
    }
}

