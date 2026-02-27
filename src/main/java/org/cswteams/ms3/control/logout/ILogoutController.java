package org.cswteams.ms3.control.logout;

/**
 * Defines the logout contract for the authentication lifecycle.
 *
 * <p>Business rule: once a user logs out, the current JWT must no longer be accepted,
 * even if its signature and expiration are still valid.</p>
 */
public interface ILogoutController {

    /**
     * Logs out a user by invalidating the given JWT token. If the JWT token is valid and not empty,
     * it is added to the blacklist to prevent further usage.
     *
     * @param token     the JWT token to be invalidated
     * @param userEmail the email address of the user to be logged out
     */
    void logout(String token, String userEmail);
}
