package org.cswteams.ms3.control.passwordChange;

import org.cswteams.ms3.AbstractMultiTenantIntegrationTest;
import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dao.TenantUserDAO;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.entity.TenantUser;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.exception.changePassword.WrongOldPasswordException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PasswordChangeIT extends AbstractMultiTenantIntegrationTest {

    @Autowired
    private PasswordChange passwordChange;

    @Autowired
    private TenantUserDAO tenantUserDAO;

    @Autowired
    private SystemUserDAO systemUserDAO;

    @Autowired
    private BlacklistedTokenDAO blacklistedTokenDAO;

    @Autowired
    private Clock clock;

    /**
     * Test Case: Verifies that when the correct old password is provided:
     * 1. The password is updated in the TenantUser table.
     * 2. The global SystemUser tokens are invalidated (via a wildcard token in BlacklistedToken table).
     */
    @Test
    void changePassword_CorrectOldPassword_Success() throws DatabaseException, WrongOldPasswordException {
        // Arrange
        String email = "integration.test.real@example.com";
        String oldPass = "oldPass123";
        String newPass = "newPass456";

        // 1. Setup TenantUser (saved in the tenant schema)
        TenantUser tenantUser = new TenantUser(
                "Test", "User", "TSTUSR99T99X999X",
                LocalDate.of(1990, 1, 1), email, oldPass,
                Set.of(SystemActor.DOCTOR));
        tenantUser = tenantUserDAO.save(tenantUser);
        Long userId = tenantUser.getId();

        // 2. Setup SystemUser (saved in the public/default schema)
        // Needed because the blacklist service looks up the SystemUser by email.
        SystemUser systemUser = new SystemUser(
                "Test", "User", "TSTUSR99T99X999X",
                LocalDate.of(1990, 1, 1), email, oldPass,
                Set.of(SystemActor.DOCTOR), TEST_TENANT);
        systemUser = systemUserDAO.save(systemUser);

        ChangePasswordDTO dto = new ChangePasswordDTO(userId, oldPass, newPass);

        // Act
        ChangePasswordDTO result = passwordChange.changePassword(dto);

        // Assert
        // 1. Verify DTO Return
        assertNotNull(result);
        assertEquals(newPass, result.getNewPassword());

        // 2. Verify Database Update (TenantUser)
        TenantUser updatedUser = tenantUserDAO.findById(userId).orElseThrow();
        assertEquals(newPass, updatedUser.getPassword(), "The password in the tenant database should be updated.");

        // 3. Verify Token Blacklisting (Side Effect)
        // Check that a wildcard token "*" has been inserted for this SystemUser
        // indicating all previous tokens are invalid.
        boolean isBlacklisted = blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(
                email,
                "*",
                LocalDateTime.now(clock).minusMinutes(1) // Check if added recently
        );

        assertTrue(isBlacklisted, "A wildcard token should be created in the blacklist table to invalidate all user tokens.");
    }

    /**
     * Test Case: Verifies that providing a wrong old password throws exception
     * and ensures no changes are committed to the database (password remains old, no blacklist entry).
     */
    @Test
    void changePassword_WrongOldPassword_ThrowsWrongOldPasswordException() {
        // Arrange
        String email = "wrong.pass.test@example.com";
        String realPass = "realPass";
        String wrongInputPass = "wrongPass";

        TenantUser tenantUser = new TenantUser(
                "Wrong", "Pass", "WRNGPS99T99X999X",
                LocalDate.of(1990, 1, 1), email, realPass,
                Set.of(SystemActor.DOCTOR));
        tenantUser = tenantUserDAO.save(tenantUser);
        Long userId = tenantUser.getId();

        // We also save SystemUser to ensure the failure isn't due to missing SystemUser
        SystemUser systemUser = new SystemUser(
                "Wrong", "Pass", "WRNGPS99T99X999X",
                LocalDate.of(1990, 1, 1), email, realPass,
                Set.of(SystemActor.DOCTOR), TEST_TENANT);
        systemUserDAO.save(systemUser);

        ChangePasswordDTO dto = new ChangePasswordDTO(userId, wrongInputPass, "newPass");

        // Act & Assert
        assertThrows(WrongOldPasswordException.class, () -> passwordChange.changePassword(dto));

        // Verify Database Unchanged
        TenantUser storedUser = tenantUserDAO.findById(userId).orElseThrow();
        assertEquals(realPass, storedUser.getPassword(), "Password should not change if old password validation fails.");

        // Verify NO Blacklist Token
        boolean isBlacklisted = blacklistedTokenDAO.existsBySystemUser_EmailAndTokenAndBlacklistedAtAfter(
                email, "*", LocalDateTime.now(clock).minusMinutes(1));
        assertFalse(isBlacklisted, "No blacklist token should be created if password change fails.");
    }

    /**
     * Test Case: Verifies that trying to change a password for a non-existent user ID
     * throws a DatabaseException.
     */
    @Test
    void changePassword_UserNotFound_ThrowsDatabaseException() {
        // Arrange
        Long nonExistentId = -999L;
        ChangePasswordDTO dto = new ChangePasswordDTO(nonExistentId, "old", "new");

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> passwordChange.changePassword(dto));
        assertEquals("TenantUser not found.", exception.getMessage());
    }
}