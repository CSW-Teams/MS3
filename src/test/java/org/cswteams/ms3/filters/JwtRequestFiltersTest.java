package org.cswteams.ms3.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.control.logout.JwtBlacklistService;
import org.cswteams.ms3.utils.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtRequestFiltersTest {

    private JwtRequestFilters jwtRequestFilters;
    private JwtUtil jwtUtil;
    private LoginController loginController;
    private JwtBlacklistService jwtBlacklistService;

    @BeforeEach
    void setUp() {
        // Non-trivial fixture: wire real JwtUtil into the filter so generated tokens match runtime signature validation.
        jwtRequestFilters = new JwtRequestFilters();
        jwtUtil = new JwtUtil();
        loginController = mock(LoginController.class);
        jwtBlacklistService = mock(JwtBlacklistService.class);
        jwtRequestFilters = new JwtRequestFilters(jwtUtil, loginController, jwtBlacklistService);

        when(jwtBlacklistService.isBlacklisted(anyString())).thenReturn(false);
        when(jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(anyString(), any(Date.class)))
                .thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void expiredJwtReturnsUnauthorized() throws Exception {
        // Given an expired JWT, when the request hits the filter, then it must be blocked as unauthorized.
        // Regression guard: prevents expired tokens from being accepted after auth/logout/2FA transitions.
        String expiredJwt = buildTokenWithExpirationOffset(-1000);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.addHeader("Authorization", "Bearer " + expiredJwt);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        jwtRequestFilters.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Token expired", response.getContentAsString());
        verify(filterChain, never()).doFilter(any(), any());
        verifyNoInteractions(loginController);
    }

    @Test
    void malformedJwtReturnsUnauthorized() throws Exception {
        // Given a malformed JWT, when the filter parses it, then request processing must stop with 401.
        // Regression guard: avoids malformed token bypass that could skip normal authentication checks.
        String malformedJwt = "not-a-valid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.addHeader("Authorization", "Bearer " + malformedJwt);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        jwtRequestFilters.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Invalid token", response.getContentAsString());
        verify(filterChain, never()).doFilter(any(), any());
        verifyNoInteractions(loginController);
    }

    private String buildTokenWithExpirationOffset(long offsetMillis) {
        Key key = ReflectionTestUtils.invokeMethod(jwtUtil, "getSigningKey");
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + offsetMillis);

        return Jwts.builder()
                .setSubject("test-user")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
