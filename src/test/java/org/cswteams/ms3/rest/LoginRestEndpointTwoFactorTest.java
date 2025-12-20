package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.security.BlacklistService;
import org.cswteams.ms3.security.TwoFactorAuthenticationService;
import org.cswteams.ms3.security.TwoFactorResult;
import org.cswteams.ms3.utils.JwtUtil;
import org.cswteams.ms3.utils.TurnstileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginRestEndpoint.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginRestEndpointTwoFactorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private LoginController loginController;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TurnstileService turnstileService;

    @MockBean
    private BlacklistService blacklistService;

    @MockBean
    private TwoFactorAuthenticationService twoFactorAuthenticationService;

    @MockBean
    private SystemUserDAO systemUserDAO;

    @Test
    void jwtIsNotIssuedWhenTwoFactorChallengeIsRequired() throws Exception {
        when(blacklistService.isInBlackList(any())).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(Mockito.mock(Authentication.class));

        SystemUser user = new SystemUser(
                "John",
                "Doe",
                "TAXCODE",
                java.time.LocalDate.of(1990, 1, 1),
                "user@example.com",
                "password",
                Set.of(SystemActor.PLANNER),
                "tenant"
        );
        user.setTwoFactorEnabled(true);

        CustomUserDetails details = new CustomUserDetails(1L, "John", "Doe", user.getEmail(), "encoded", user.getSystemActors(), "tenant");
        when(loginController.loadUserByUsername(user.getEmail())).thenReturn(details);
        when(systemUserDAO.findByEmail(user.getEmail())).thenReturn(user);
        when(twoFactorAuthenticationService.processTwoFactor(eq(user), isNull())).thenReturn(TwoFactorResult.challenge("Two-factor code required."));

        String payload = "{\"email\":\"user@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/login/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.requiresTwoFactor").value(true))
                .andExpect(jsonPath("$.jwt").value(nullValue()));

        verifyNoInteractions(jwtUtil);
    }
}
