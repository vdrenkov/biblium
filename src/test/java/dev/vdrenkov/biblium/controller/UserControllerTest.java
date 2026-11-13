package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.UserDto;
import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.handler.GlobalExceptionHandler;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import dev.vdrenkov.biblium.request.UserRequest;
import dev.vdrenkov.biblium.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static dev.vdrenkov.biblium.jwt.JwtConstants.JWT_COOKIE_NAME;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
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
    void testLogin_returnsOkAndCookie() throws Exception {
        final HttpCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, "jwt-token").httpOnly(true).path("/").build();
        when(userService.login(any(UserRequest.class))).thenReturn(cookie);
        final String json = new ObjectMapper().writeValueAsString(new UserRequest("librarian", "password123"));

        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Set-Cookie", org.hamcrest.Matchers.containsString(JWT_COOKIE_NAME + "=jwt-token")));
    }

    @Test
    void createUser_validStaffAccount_returnsCreatedAndLocation() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(new User(1, "admin", "encoded", null));
        final String json = """
            {"username":"admin","password":"password123","rolesIds":[1]}
            """;

        mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/users/1"));
    }

    @Test
    void testGetAllUsers_returnsOk() throws Exception {
        when(userService.getAllUsersDto()).thenReturn(
            Collections.singletonList(new UserDto("admin", Collections.emptyList())));

        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    void testGetUserById_returnsOk() throws Exception {
        when(userService.getUserDtoById(anyInt())).thenReturn(new UserDto("admin", Collections.emptyList()));

        mockMvc.perform(get("/users/" + ID)).andExpect(status().isOk());
    }

    @Test
    void testLogin_invalidPayload_returnsBadRequest() throws Exception {
        final String json = """
            {"username":"","password":"short"}
            """;

        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest());
    }
}

