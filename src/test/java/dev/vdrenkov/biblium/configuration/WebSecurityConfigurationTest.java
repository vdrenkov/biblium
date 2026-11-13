package dev.vdrenkov.biblium.configuration;

import jakarta.servlet.http.Cookie;
import dev.vdrenkov.biblium.controller.AuthorController;
import dev.vdrenkov.biblium.controller.ClientController;
import dev.vdrenkov.biblium.controller.CsrfController;
import dev.vdrenkov.biblium.controller.UserController;
import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.jwt.JwtRequestFilter;
import dev.vdrenkov.biblium.jwt.JwtTokenUtil;
import dev.vdrenkov.biblium.jwt.JwtUserDetailsService;
import dev.vdrenkov.biblium.request.UserRequest;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import dev.vdrenkov.biblium.service.AuthorService;
import dev.vdrenkov.biblium.service.ClientService;
import dev.vdrenkov.biblium.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = { AuthorController.class, ClientController.class, UserController.class, CsrfController.class })
@Import({ WebSecurityConfiguration.class, WebSecurityConfigurationTest.SecurityFilterConfig.class })
class WebSecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private JwtUserDetailsService jwtUserDetailsService;

    @Test
    void testLogin_publicEndpoint_accessibleWithoutAuthentication() throws Exception {
        final HttpCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, "jwt-token").httpOnly(true).path("/").build();
        when(userService.login(any(UserRequest.class))).thenReturn(cookie);

        final String json = objectMapper.writeValueAsString(new UserRequest("librarian", "password123"));

        mockMvc
            .perform(post("/login").cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk());
    }

    @Test
    void testCsrf_publicEndpoint_accessibleWithoutAuthentication() throws Exception {
        mockMvc
            .perform(get("/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("XSRF-TOKEN=")));
    }

    @Test
    void testGetAuthors_withoutAuthentication_forbidden() throws Exception {
        mockMvc.perform(get("/authors")).andExpect(status().isForbidden());
    }

    @Test
    void testGetAuthors_withLibrarian_allowed() throws Exception {
        when(authorService.getAllAuthorsDto()).thenReturn(Collections.emptyList());

        mockMvc
            .perform(get("/authors").with(user("librarian").authorities(new SimpleGrantedAuthority("LIBRARIAN"))))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteClient_withLibrarian_forbidden() throws Exception {
        mockMvc
            .perform(delete("/clients/1")
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token")
                .with(user("librarian").authorities(new SimpleGrantedAuthority("LIBRARIAN"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteClient_withAdmin_allowed() throws Exception {
        when(clientService.deleteClient(anyInt())).thenReturn(new ClientDto(1, "N", "S", "0888888888", "n@s.com"));

        mockMvc
            .perform(
                delete("/clients/1").cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token").with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
            .andExpect(status().isNoContent());
    }

    @Test
    void createUser_administrator_returnsCreatedWithoutChangingSession() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(new User(7, "staff", "encoded", null));

        mockMvc
            .perform(post("/users")
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token")
                .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"staff","password":"password123","rolesIds":[2]}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/users/7"))
            .andExpect(cookie().doesNotExist(JWT_COOKIE_NAME));
    }

    @Test
    void createUser_librarian_returnsForbidden() throws Exception {
        mockMvc
            .perform(post("/users")
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token")
                .with(user("librarian").authorities(new SimpleGrantedAuthority("LIBRARIAN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"staff","password":"password123","rolesIds":[2]}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void createUser_missingCsrfToken_returnsForbidden() throws Exception {
        mockMvc
            .perform(post("/users")
                .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"staff","password":"password123","rolesIds":[2]}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void register_legacyPublicRoute_isUnavailable() throws Exception {
        mockMvc
            .perform(post("/register")
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token")
                .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"staff","password":"password123"}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void registerAdmin_legacyRoute_isUnavailable() throws Exception {
        mockMvc
            .perform(post("/admins/register")
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token")).header("X-XSRF-TOKEN", "test-csrf-token")
                .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"staff","password":"password123","rolesIds":[2]}
                    """))
            .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class SecurityFilterConfig {

        @Bean
        JwtRequestFilter jwtRequestFilter(final JwtTokenUtil jwtTokenUtil,
            final JwtUserDetailsService jwtUserDetailsService) {
            return new JwtRequestFilter(jwtTokenUtil, jwtUserDetailsService);
        }
    }
}

