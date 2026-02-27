package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.entity.BlacklistedToken;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestJwtBlacklistService {

    private static final String validToken = "valid_token";
    private static final String blacklistedToken = "blacklisted_token";
    private static final String unblacklistedToken = "unblacklisted_token";

    // Non-static attribute due to the user of Mockito
    private SystemUser validUserMock;

    @Mock
    private BlacklistedTokenDAO blacklistedTokenDAO;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private Clock clock;
    @InjectMocks
    private JwtBlacklistService jwtBlacklistService;
    // Fixed the clock for consistent time in tests
    private LocalDateTime fixedDateTime;

    private static Stream<Arguments> provideInvalidBlacklistParams() {
        // Using this other mock in a static context
        SystemUser validUserMock = mock(SystemUser.class);
        return Stream.of(Arguments.of(null, null), Arguments.of("", null), Arguments.of(null, validUserMock), Arguments.of("", validUserMock));
    }

    private static Stream<Arguments> provideInvalidIsBlacklistedParams() {
        return Stream.of(Arguments.of((Object) null), Arguments.of(""));
    }

    @BeforeEach
    void setUp() {
        this.validUserMock = mock(SystemUser.class);
        // Set up a fixed clock
        fixedDateTime = LocalDateTime.of(2025, 12, 16, 10, 0, 0);
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        lenient().when(clock.instant()).thenReturn(fixedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void blacklist_whenArgumentsAreValid_shouldCallBlacklist() {
        // Given a valid token and user, when blacklisting is requested, then token metadata must be saved consistently.
        // Regression guard: avoids partial token revocation records that could let invalid sessions slip through checks.
        when(blacklistedTokenDAO.existsByToken(validToken)).thenReturn(false);
        when(jwtUtil.extractExpiration(validToken)).thenReturn(Date.from(fixedDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        jwtBlacklistService.blacklist(validToken, validUserMock);

        ArgumentCaptor<BlacklistedToken> argument = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenDAO, times(1)).save(argument.capture());
        BlacklistedToken savedToken = argument.getValue();

        assertEquals(validToken, savedToken.getToken());
        assertEquals(fixedDateTime, savedToken.getExpiresAt());
        assertEquals(fixedDateTime, savedToken.getBlacklistedAt());
        assertEquals(validUserMock, savedToken.getSystemUser());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidBlacklistParams")
    void blacklist_whenTokenArgumentIsInvalid_shouldNotBlacklistToken(String token, SystemUser user) {
        // Given invalid token inputs, when blacklisting is attempted, then no persistence should occur.
        // Regression guard: prevents accepting malformed logout data as if it were revocation evidence.
        //when(blacklistedTokenDAO.existsByToken(token)).thenReturn(true);
        jwtBlacklistService.blacklist(token, user);
        verify(blacklistedTokenDAO, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklist_whenSystemUserArgumentIsNull_shouldNotBlacklistToken() {
        // Given a missing user context, when blacklisting is requested, then token must not be stored.
        jwtBlacklistService.blacklist(validToken, null);
        verify(blacklistedTokenDAO, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void isBlacklisted_whenTokenIsAlreadyBlacklisted_shouldReturnTrue() {
        // Given a token already in blacklist storage, when checked, then access should be denied.
        when(blacklistedTokenDAO.existsByToken(blacklistedToken)).thenReturn(true);
        assertTrue(jwtBlacklistService.isBlacklisted(blacklistedToken));
    }

    @Test
    void isBlacklisted_whenTokenIsNotAlreadyBlacklisted_shouldReturnTrue() {
        // Given a token absent from blacklist storage, when checked, then it should not be treated as revoked.
        when(blacklistedTokenDAO.existsByToken(unblacklistedToken)).thenReturn(false);
        assertFalse(jwtBlacklistService.isBlacklisted(unblacklistedToken));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidIsBlacklistedParams")
    void isBlacklisted_whenTokenIsInvalid_shouldReturnTrue(String token) {
        // Given null/blank token input, when blacklist status is requested, then validation must fail fast.
        // Regression guard: blocks edge cases where empty tokens might bypass auth checks.
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.isBlacklisted(token));
        assertEquals("Token cannot be null or empty", exc.getMessage());
    }

    @Test
    void doesUserHaveTokensBlacklistedAfterDate_withLocalDateTime_whenUserHasWildcardTokenAfterDate_shouldReturnTrue() {
        // Given a user-wide wildcard revocation after issue date, when queried, then the user must be considered revoked.
        String email = "test@example.com";
        LocalDateTime issueDate = fixedDateTime.minusHours(1);
        when(blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(email, "*", issueDate)).thenReturn(true);

        assertTrue(jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(email, issueDate));
    }

    @Test
    void doesUserHaveTokensBlacklistedAfterDate_withLocalDateTime_whenUserDoesNotHaveWildcardTokenAfterDate_shouldReturnFalse() {
        // Given no wildcard revocation after issue date, when queried, then user-wide revocation must be false.
        String email = "test@example.com";
        LocalDateTime issueDate = fixedDateTime.minusHours(1);
        when(blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(email, "*", issueDate)).thenReturn(false);

        assertFalse(jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(email, issueDate));
    }

    @Test
    void doesUserHaveTokensBlacklistedAfterDate_withLocalDateTime_whenEmailIsInvalid_shouldThrowException() {
        // Given invalid email identity, when revocation scope is checked, then the service must reject the request.
        LocalDateTime issueDate = fixedDateTime;
        assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(null, issueDate));
        assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate("", issueDate));
    }

    @Test
    void doesUserHaveTokensBlacklistedAfterDate_withDate_whenDateIsNull_shouldThrowException() {
        // Given missing issue date, when revocation is evaluated, then argument validation must protect the query contract.
        String email = "test@example.com";
        assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(email, (Date) null));
    }

    @Test
    void doesUserHaveTokensBlacklistedAfterDate_withDate_shouldCallLocalDateTimeVersion() {
        // Given Date-based API usage, when called, then conversion path must preserve wildcard revocation semantics.
        String email = "test@example.com";
        Date date = Date.from(fixedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // The service converts Date to LocalDateTime using the clock.
        // With our fixed clock, this should equal fixedDateTime.
        when(blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(email, "*", fixedDateTime)).thenReturn(true);

        assertTrue(jwtBlacklistService.doesUserHaveTokensBlacklistedAfterDate(email, date));
    }

    @Test
    void blacklistAllUserTokens_whenUserIsValid_shouldSaveWildcardToken() {
        // Given a valid user, when global logout is triggered, then a wildcard token must revoke all active sessions.
        // Regression guard: prevents user-level logout from leaving old tokens valid (2FA/session bypass risk).
        jwtBlacklistService.blacklistAllUserTokens(validUserMock);

        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenDAO).save(captor.capture());

        BlacklistedToken savedToken = captor.getValue();
        assertEquals("*", savedToken.getToken());
        assertEquals(validUserMock, savedToken.getSystemUser());
        assertEquals(fixedDateTime, savedToken.getBlacklistedAt());
        assertEquals(fixedDateTime.plusHours(1), savedToken.getExpiresAt());
    }

    @Test
    void blacklistAllUserTokens_whenUserIsNull_shouldThrowException() {
        // Given missing user context, when wildcard revocation is requested, then validation must fail.
        assertThrows(IllegalArgumentException.class, () -> jwtBlacklistService.blacklistAllUserTokens(null));
    }
}
