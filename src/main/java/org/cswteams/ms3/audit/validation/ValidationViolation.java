package org.cswteams.ms3.audit.validation;

public class ValidationViolation {
    private final String path;
    private final String message;

    public ValidationViolation(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}
