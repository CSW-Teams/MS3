package org.cswteams.ms3.control.logout;

public interface ILogoutController {

    /**
     * Invalidates the given JWT token.
     *
     * @param token JWT token to invalidate
     */
    void logout(String token);
}
