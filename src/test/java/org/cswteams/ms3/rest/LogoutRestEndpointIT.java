package org.cswteams.ms3.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.dto.login.LoginRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LogoutRestEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogoutFlow_FullIntegration() throws Exception {
        // Given a real login token, when the same token is used after logout, then protected resources must reject it.
        // Regression guard: detects critical auth bug where logged-out tokens are still accepted by filters.
        // Non-trivial fixture: obtain a real JWT from the login flow to validate filter + persistence interaction.
        LoginRequestDTO loginRequest = new LoginRequestDTO("federicavillani.tenanta@gmail.com","passw");

        MvcResult loginResult = mockMvc.perform(post("/login/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        String token = jsonNode.path("jwt").asText();

        String authHeader = "Bearer " + token;
        System.out.println("TEST INTEGRATION - Token ottenuto: " + token);

        // Sanity check: token is valid before logout, so the protected endpoint must return 200.
        mockMvc.perform(get("/users/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk());

        // When logout is invoked with that same token, revocation should be persisted.
        mockMvc.perform(post("/logout/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk());

        // Then reusing the same token must fail with Unauthorized, proving the filter enforces logout revocation.
        mockMvc.perform(get("/users/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isUnauthorized());
    }
}