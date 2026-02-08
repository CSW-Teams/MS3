package org.cswteams.ms3.ai.protocol;

import lombok.Getter;

@Getter
public class ValidationError {
    private final String path;
    private final String message;

    public ValidationError(String path, String message) {
        this.path = path;
        this.message = message;
    }
}
