package org.cswteams.ms3.control.passwordChange;

import org.cswteams.ms3.control.logout.JwtBlacklistService;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dao.TenantUserDAO;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.entity.TenantUser;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.exception.changePassword.WrongOldPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordChangeTest {

    @Mock
    private JwtBlacklistService blacklistService;

    @Mock
    private TenantUserDAO userDAO;

    @Mock
    private SystemUserDAO systemUserDAO;

    @InjectMocks
    private PasswordChange passwordChange;

    /**
     * Test Case: The user exists, the old password provided matches the stored password.
     * Expected: The password is updated, saved, tokens are blacklisted, and the DTO is returned.
     */
    @Test
    void changePassword_CorrectOldPassword_Success() throws DatabaseException, WrongOldPasswordException {
        // Arrange
        Long userId = 1L;
        String oldPass = "oldPassword";
        String newPass = "newPassword";
        String email = "test@example.com";

        ChangePasswordDTO dto = new ChangePasswordDTO(userId, oldPass, newPass);

        // Mock TenantUser (Context-specific user)
        TenantUser mockUser = new TenantUser(
                "Mario", "Rossi", "RSSMRA...",
                LocalDate.of(1990, 1, 1), email, oldPass,
                Set.of(SystemActor.DOCTOR)
        );
        // We simulate that the ID was set by JPA
        // (Note: Since 'id' is protected in your entity, we assume normal retrieval sets it,
        // or we rely on the object reference for this test).

        // Mock SystemUser (Global user for token invalidation)
        SystemUser mockSystemUser = new SystemUser(
                "Mario", "Rossi", "RSSMRA...",
                LocalDate.of(1990, 1, 1), email, oldPass,
                Set.of(SystemActor.DOCTOR), "tenant1"
        );

        when(userDAO.findById(userId)).thenReturn(Optional.of(mockUser));
        when(systemUserDAO.findByEmail(email)).thenReturn(mockSystemUser);

        // Act
        ChangePasswordDTO result = passwordChange.changePassword(dto);

        // Assert
        assertNotNull(result);
        assertEquals(newPass, result.getNewPassword());
        assertEquals(newPass, mockUser.getPassword()); // Verify entity state changed

        // Verify interactions
        verify(userDAO, times(1)).saveAndFlush(mockUser);
        verify(blacklistService, times(1)).blacklistAllUserTokens(mockSystemUser);
    }

    /**
     * Test Case: The user ID provided in the DTO does not exist in the database.
     * Expected: A DatabaseException is thrown.
     */
    @Test
    void changePassword_UserNotFound_ThrowsDatabaseException() {
        // Arrange
        Long nonExistentId = 999L;
        ChangePasswordDTO dto = new ChangePasswordDTO(nonExistentId, "any", "new");

        when(userDAO.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> passwordChange.changePassword(dto));

        assertEquals("TenantUser not found.", exception.getMessage());

        // Verify no changes were made
        verify(userDAO, never()).saveAndFlush(any());
        verify(blacklistService, never()).blacklistAllUserTokens(any());
    }

    /**
     * Test Case: The user exists, but the old password provided does not match the stored password.
     * Expected: A WrongOldPasswordException is thrown.
     */
    @Test
    void changePassword_WrongOldPassword_ThrowsWrongOldPasswordException() {
        // Arrange
        Long userId = 1L;
        String realPassword = "correctOldPassword";
        String wrongPassword = "wrongOldPassword";

        ChangePasswordDTO dto = new ChangePasswordDTO(userId, wrongPassword, "newPassword");

        TenantUser mockUser = new TenantUser(
                "Mario", "Rossi", "RSSMRA...",
                LocalDate.of(1990, 1, 1), "test@example.com", realPassword,
                new HashSet<>()
        );

        when(userDAO.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        WrongOldPasswordException exception = assertThrows(WrongOldPasswordException.class, () -> passwordChange.changePassword(dto));

        assertEquals("The old password is wrong.", exception.getMessage());

        // Verify no changes were made
        verify(userDAO, never()).saveAndFlush(any());
        verify(blacklistService, never()).blacklistAllUserTokens(any());
    }

    /**
     * Test Case: The input DTO has a null user ID (or the DTO itself is valid but fields are null).
     * Note: If the DTO itself is null, the method throws NPE immediately due to usage.
     * This test checks if finding by null ID is handled by the mock behavior (usually returning empty or throwing).
     */
    @Test
    void changePassword_NullIdInDTO_ThrowsDatabaseExceptionOrNPE() {
        // Arrange
        ChangePasswordDTO dto = new ChangePasswordDTO(null, "old", "new");

        // Depending on DAO implementation, findById(null) might throw IllegalArgumentException
        // or return empty. Here we assume standard JPA behavior (usually throws IllegalArgumentException).
        // However, based on the logic: userDAO.findById(dto.getUserId()) -> findById(null).
        doThrow(new IllegalArgumentException("ID cannot be null")).when(userDAO).findById(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> passwordChange.changePassword(dto));
    }
}