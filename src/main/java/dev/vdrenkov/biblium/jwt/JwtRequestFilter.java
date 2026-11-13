package dev.vdrenkov.biblium.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtTokenUtil tokenUtil;
    private final JwtUserDetailsService userDetailsService;

    public JwtRequestFilter(final JwtTokenUtil tokenUtil, final JwtUserDetailsService userDetailsService) {
        this.tokenUtil = tokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain) throws ServletException, IOException {
        final String token = getJwtToken(request.getCookies());
        String username = null;

        try {
            if (token != null && !token.isEmpty()) {
                log.debug("JWT cookie detected on request");
                username = tokenUtil.getUsernameFromToken(token);
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null && username != null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (Boolean.TRUE.equals(tokenUtil.validateToken(token, userDetails))) {
                    final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception exception) {
            log.warn("Failed authentication request from JWT cookie", exception);
        }

        chain.doFilter(request, response);
    }

    private static String getJwtToken(final Cookie[] cookies) {
        if (cookies != null) {
            return Arrays
                .stream(cookies)
                .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        }
        return null;
    }
}

