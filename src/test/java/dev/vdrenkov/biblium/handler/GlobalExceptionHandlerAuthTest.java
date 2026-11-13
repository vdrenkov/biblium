package dev.vdrenkov.biblium.handler;

import dev.vdrenkov.biblium.controller.UserController;
import dev.vdrenkov.biblium.request.UserRequest;
import dev.vdrenkov.biblium.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerAuthTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void testBadCredentials_returnsUnauthorized() throws Exception {
        when(userService.login(any(UserRequest.class))).thenThrow(new BadCredentialsException("bad credentials"));
        final String json = new ObjectMapper().writeValueAsString(new UserRequest("user123", "password123"));

        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$", is("Invalid username or password")));
    }

    @Test
    void testInternalAuthenticationService_returnsUnauthorized() throws Exception {
        when(userService.login(any(UserRequest.class))).thenThrow(
            new InternalAuthenticationServiceException("user not found"));
        final String json = new ObjectMapper().writeValueAsString(new UserRequest("user123", "password123"));

        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$", is("Invalid username or password")));
    }
}
