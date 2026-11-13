package dev.vdrenkov.biblium.configuration;

import dev.vdrenkov.biblium.jwt.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;

@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration {
    private static final String ADMIN = "ADMIN";
    private static final String LIBRARIAN = "LIBRARIAN";

    private static final String[] AUTH_PATH = { "/csrf", "/login"
    };

    private static final String[] ADMIN_LIST = { "/users", "/users/**", "/roles", "/roles/**",
    };

    private static final String[] LIBRARIAN_LIST = { "/authors", "/authors/**", "/books", "/books/**", "/clients",
        "/clients/**", "/orders", "/orders/**"
    };

    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfiguration(final JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) {
        httpSecurity
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfTokenRequestHandler())
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_PATH)
                .permitAll()
                .requestMatchers(ADMIN_LIST)
                .hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.POST, LIBRARIAN_LIST)
                .hasAnyAuthority(ADMIN, LIBRARIAN)
                .requestMatchers(HttpMethod.GET, LIBRARIAN_LIST)
                .hasAnyAuthority(ADMIN, LIBRARIAN)
                .requestMatchers(HttpMethod.PUT, LIBRARIAN_LIST)
                .hasAnyAuthority(ADMIN, LIBRARIAN)
                .requestMatchers(HttpMethod.DELETE, LIBRARIAN_LIST)
                .hasAuthority(ADMIN)
                .anyRequest()
                .authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies(JWT_COOKIE_NAME)
                .logoutSuccessHandler((_, response, _) -> response.setStatus(HttpServletResponse.SC_OK)));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilterRegistration() {
        final var registration = new FilterRegistrationBean<>(jwtRequestFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private static CsrfTokenRequestHandler csrfTokenRequestHandler() {
        final var encodedTokens = new XorCsrfTokenRequestAttributeHandler();
        final var cookieTokens = new CsrfTokenRequestAttributeHandler();
        return new CsrfTokenRequestHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response,
                final Supplier<CsrfToken> csrfToken) {
                encodedTokens.handle(request, response, csrfToken);
            }

            @Override
            public String resolveCsrfTokenValue(final HttpServletRequest request, final CsrfToken csrfToken) {
                final String encodedToken = encodedTokens.resolveCsrfTokenValue(request, csrfToken);
                return encodedToken != null ? encodedToken : cookieTokens.resolveCsrfTokenValue(request, csrfToken);
            }
        };
    }
}

