package dev.vdrenkov.biblium.jwt;

import jakarta.servlet.FilterChain;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_validToken_setsAuthentication() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(JWT_COOKIE_NAME, "jwt-token"));
        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenUtil.getUsernameFromToken("jwt-token")).thenReturn("librarian");
        when(jwtUserDetailsService.loadUserByUsername("librarian")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtRequestFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_noCookie_doesNotAuthenticate() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        jwtRequestFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_invalidToken_doesNotAuthenticate() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(JWT_COOKIE_NAME, "jwt-token"));
        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenUtil.getUsernameFromToken("jwt-token")).thenThrow(new JwtException("Invalid token"));

        jwtRequestFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}

