package kg.infosystems.statefin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.infosystems.statefin.dto.request.LoginRequest;
import kg.infosystems.statefin.dto.request.UserRegistrationRequest;
import kg.infosystems.statefin.dto.response.LoginResponse;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.service.AuthService;
import kg.infosystems.statefin.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private UserRegistrationRequest registrationRequest;
    private LoginResponse loginResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "password");
        
        registrationRequest = new UserRegistrationRequest(
                "testuser", "test@example.com", "Password123!", "Test", "User");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        loginResponse = new LoginResponse(
                "access-token", "refresh-token", 3600L, userResponse);
    }

    @Test
    void login_Success() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    void login_InvalidInput() throws Exception {
        // Given - No service mocking needed for validation errors
        LoginRequest invalidRequest = new LoginRequest("", "");

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserRegistrationRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void register_InvalidInput() throws Exception {
        // Given - No service mocking needed for validation errors
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "", "", "", "", "");

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }

}