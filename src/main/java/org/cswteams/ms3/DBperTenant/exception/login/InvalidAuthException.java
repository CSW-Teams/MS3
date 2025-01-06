package org.cswteams.ms3.DBperTenant.exception.login;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthException extends AuthenticationException {
    public InvalidAuthException(String message) {
        super(message);
    }
}