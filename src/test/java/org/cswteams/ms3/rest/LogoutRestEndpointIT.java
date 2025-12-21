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
        // LOGIN: otteniamo un token valido reale usando un utente esistente nel DB
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

        // VERIFICA TOKEN ATTIVO: L'accesso ad una risorsa protetta deve funzionare (200 OK).
        mockMvc.perform(get("/users/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk());

        // LOGOUT
        mockMvc.perform(post("/logout/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk());

        // VERIFICA TOKEN INVALIDATO: si esegue la stessa chiamata precedente alla risorsa protetta.
        // ora deve fallire (401 o 403), ci si aspetta che il JwtRequestFilters blocchi la richiesta
        mockMvc.perform(get("/users/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isUnauthorized());
    }
}