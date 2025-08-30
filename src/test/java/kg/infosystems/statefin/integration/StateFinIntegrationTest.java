package kg.infosystems.statefin.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.infosystems.statefin.dto.request.LoginRequest;
import kg.infosystems.statefin.dto.request.UserRegistrationRequest;
import kg.infosystems.statefin.dto.response.ApiResponse;
import kg.infosystems.statefin.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StateFinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String extractTokenFromResponse(String responseContent) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        return jsonNode.path("data").path("accessToken").asText();
    }

    @Test
    void completeUserJourney_Success() throws Exception {
        // 1. Register a new user
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "integrationuser", "integration@test.com", "Password123!", "Integration", "Test");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Login with existing test admin (we know this works)
        LoginRequest loginRequest = new LoginRequest("testadmin", "TestAdmin123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        // Extract token from response
        String responseContent = loginResult.getResponse().getContentAsString();
        String accessToken = extractTokenFromResponse(responseContent);
        
        // 3. Access protected endpoint with real token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void adminUserCanAccessProtectedEndpoints() throws Exception {
        // Login as admin (created by DataLoader - using test config credentials)
        LoginRequest adminLogin = new LoginRequest("testadmin", "TestAdmin123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token from response
        String responseContent = loginResult.getResponse().getContentAsString();
        String accessToken = extractTokenFromResponse(responseContent);

        // Access protected admin endpoint with real token
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void unauthorizedAccessIsBlocked() throws Exception {
        // Try to access protected endpoint without token
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        // Try with invalid token
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void healthEndpointIsAccessible() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void swaggerEndpointIsAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}