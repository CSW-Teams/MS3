package org.cswteams.ms3.rest;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.security.TwoFactorAuthenticationService;
import org.cswteams.ms3.security.TwoFactorCodeService;
import org.cswteams.ms3.security.TwoFactorProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TwoFactorRestEndpoint.class)
@AutoConfigureMockMvc(addFilters = false)
class TwoFactorRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwoFactorCodeService twoFactorCodeService;

    @MockBean
    private TwoFactorAuthenticationService twoFactorAuthenticationService;

    @MockBean
    private SystemUserDAO systemUserDAO;

    @MockBean
    private TwoFactorProperties properties;

    @MockBean
    private Clock clock;

    private SystemUser buildUser() {
        SystemUser user = new SystemUser(
                "John",
                "Doe",
                "TAXCODE",
                java.time.LocalDate.of(1990, 1, 1),
                "user@example.com",
                "password",
                Collections.emptySet(),
                "public"
        );
        user.setTwoFactorEnabled(false);
        return user;
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void statusEndpointIsExposed() throws Exception {
        SystemUser user = buildUser();
        when(systemUserDAO.findByEmail("user@example.com")).thenReturn(user);
        when(properties.getRequiredRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/2fa/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void enrollEndpointIsExposed() throws Exception {
        SystemUser user = buildUser();
        when(systemUserDAO.findByEmail("user@example.com")).thenReturn(user);
        when(properties.getRequiredRoles()).thenReturn(Collections.emptyList());
        when(properties.getRecoveryCodeCount()).thenReturn(2);
        when(twoFactorCodeService.deriveTotpSecret(any())).thenReturn("secret-seed".getBytes());
        when(twoFactorCodeService.recoveryCodeForUser(Mockito.eq(user), Mockito.eq(1))).thenReturn("code-one");
        when(twoFactorCodeService.recoveryCodeForUser(Mockito.eq(user), Mockito.eq(2))).thenReturn("code-two");
        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        mockMvc.perform(post("/2fa/enroll").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manualKey").exists());
    }
}
