package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.entity.SystemUser;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestLogoutController {

    private final static String validToken = "valid_token";
    private final static String validUserEmail = "validuser@mail.com";
    private final static SystemUser validSystemUser = mock(SystemUser.class);
    @Mock
    private JwtBlacklistService blacklistService;
    @Mock
    private SystemUserDAO systemUserDAO;
    @InjectMocks
    private LogoutController logoutController;

    // List of method params, with at least one invalid param
    private static Stream<Arguments> provideInvalidLogoutParams() {
        return Stream.of(Arguments.of(null, null), Arguments.of("", ""), Arguments.of(null, ""), Arguments.of("", null), Arguments.of(null, validUserEmail), Arguments.of("", validUserEmail), Arguments.of(validToken, null), Arguments.of(validToken, ""));
    }

    /**
     * Verifies that the logout operation does not attempt to blacklist a token when given invalid parameters.
     * This test ensures that the `blacklist` method of the blacklist service is never called when the
     * logout request contains null or empty values for the token or email.
     *
     * @param token     the JWT token provided for logout. It may be null or empty as part of the test case.
     * @param userEmail the email of the user attempting to log out. It may be null or empty as part of the test case.
     */
    @ParameterizedTest
    @MethodSource("provideInvalidLogoutParams")
    void testLogout_WithInvalidParams_NoCalls(String token, String userEmail) {
        logoutController.logout(token, userEmail);
        verify(blacklistService, never()).blacklist(anyString(), any());
    }

    @Test
    void testLogout_WithValidParams_TokenBlacklisted() {
        when(systemUserDAO.findByEmail(validUserEmail)).thenReturn(validSystemUser);
        doNothing().when(blacklistService).blacklist(validToken, validSystemUser);
        logoutController.logout(validToken, validUserEmail);
        verify(blacklistService, times(1)).blacklist(validToken, validSystemUser);
    }
}
