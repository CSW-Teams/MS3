package org.cswteams.ms3.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.cswteams.ms3.control.login.LoginController;
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

    @BeforeEach
    void setUp() {
        jwtRequestFilters = new JwtRequestFilters();
        jwtUtil = new JwtUtil();
        loginController = mock(LoginController.class);

        ReflectionTestUtils.setField(jwtRequestFilters, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(jwtRequestFilters, "loginController", loginController);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void expiredJwtReturnsUnauthorized() throws Exception {
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
