package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.logout.JwtBlacklistService;
import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.utils.JwtUtil;
import org.cswteams.ms3.config.soft_delete.SoftDeleteService;
import org.cswteams.ms3.control.logout.LogoutController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogoutRestEndpoint.class, secure = false)
public class LogoutRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogoutController logoutController;

    // --- MOCK DI INFRASTRUTTURA (Necessari per avviare i Filtri Web) ---
    @MockBean
    private SoftDeleteService softDeleteService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginController loginController;
    @MockBean
    private JwtBlacklistService jwtBlacklistService;

    @Test
    void testLogout_success() throws Exception {
        String token = "valid_token";
        String authHeader = "Bearer " + token;

        mockMvc.perform(post("/logout/")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk());

        verify(logoutController, times(1)).logout(token);
    }

    @Test
    void testLogout_missingAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/logout/"))
                .andExpect(status().isOk());

        verify(logoutController, times(0)).logout(anyString());
    }

    @Test
    void testLogout_invalidAuthorizationHeaderFormat() throws Exception {
        // Caso: Header presente ma senza "Bearer " (es. solo il token)
        String token = "InvalidTokenFormat";

        mockMvc.perform(post("/logout/")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());

        verify(logoutController, times(0)).logout(anyString());
    }

    @Test
    void testLogout_emptyAuthorizationHeader() throws Exception {
        // Caso: Header presente ma stringa vuota
        String token = "";

        mockMvc.perform(post("/logout/")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());

        verify(logoutController, times(0)).logout(anyString());
    }
}