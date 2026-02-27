package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.logout.LogoutController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutRestEndpointTest {

    private static final String authHeaderPrefix = "Bearer ";
    private static final String token = "valid_token";
    private static final String userEmail = "user@tenant.com";

    // Non-static attribute due to the user of Mockito
    private CustomUserDetails customUserDetails;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogoutController logoutController;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        this.customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getEmail()).thenReturn(userEmail);
        when(jwtUtil.extractUsername(anyString())).thenReturn(userEmail);
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);
        when(jwtUtil.extractExpiration(token)).thenReturn(new Date(System.currentTimeMillis() + 10000));
    }

    @Test
    void logout_whenUserIsLoggedAndTokenIsValid_shouldExecuteLogout() throws Exception {
        // Given an authenticated user with a Bearer token, when /logout is called, then controller revocation must execute.
        // Regression guard: catches logout paths that return 200 but never revoke the JWT.
        mockMvc.perform(post("/logout/").header(HttpHeaders.AUTHORIZATION, authHeaderPrefix + token).with(user(customUserDetails))).andExpect(status().isOk());
        verify(logoutController, times(1)).logout(token, userEmail);
    }

    @Test
    void logout_whenMissingAuthorizationHeader_shouldBlockTheRequest() throws Exception {
        // Given no Authorization header, when /logout is called, then request must be rejected before controller logic.
        mockMvc.perform(post("/logout/").with(user(customUserDetails))).andExpect(status().isUnauthorized());
        verify(logoutController, times(0)).logout(anyString(), anyString());
    }

    @Test
    void logout_whenAuthorizationHeaderHasWrongFormat_shouldBlockTheRequest() throws Exception {
        // Given a malformed Authorization header, when /logout is called, then it must not be treated as a valid session token.
        mockMvc.perform(post("/logout/").header(HttpHeaders.AUTHORIZATION, token).with(user(customUserDetails))).andExpect(status().isUnauthorized());
        verify(logoutController, times(0)).logout(anyString(), anyString());
    }

    @Test
    void logout_whenUserNotLoggedIn_shouldBlockTheRequest() throws Exception {
        // Given no authenticated principal, when /logout is called, then endpoint must refuse logout attempts.
        mockMvc.perform(post("/logout/").header(HttpHeaders.AUTHORIZATION, authHeaderPrefix + token)).andExpect(status().isUnauthorized());
        verify(logoutController, times(0)).logout(anyString(), anyString());
    }

    @Test
    void logout_whenAuthorizationHeaderIsEmpty_shouldBlockTheRequest() throws Exception {
        // Given an empty Authorization header, when /logout is called, then token parsing must fail closed.
        mockMvc.perform(post("/logout/").header(HttpHeaders.AUTHORIZATION, "").with(user(customUserDetails))).andExpect(status().isUnauthorized());
        verify(logoutController, times(0)).logout(anyString(), anyString());
    }
}