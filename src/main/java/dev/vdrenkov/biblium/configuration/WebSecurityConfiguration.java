package dev.vdrenkov.biblium.configuration;

import dev.vdrenkov.biblium.jwt.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            .csrf(csrf -> csrf.spa())
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
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

