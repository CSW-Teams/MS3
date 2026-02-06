package org.cswteams.ms3.control.logout;

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
