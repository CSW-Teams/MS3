package org.cswteams.ms3.control.scheduler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Structured result for a single violated constraint check.
 */
@Data
@AllArgsConstructor
public class ConstraintCheckResult {
    private Long constraintId;
    private String constraintName;
    private String description;
    private boolean violable;
    private ConstraintViolationSeverity severity;

    public boolean isBlocking() {
        return severity == ConstraintViolationSeverity.HARD;
    }
}

