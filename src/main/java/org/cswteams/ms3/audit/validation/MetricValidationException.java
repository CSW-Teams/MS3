package org.cswteams.ms3.audit.validation;

import org.slf4j.MDC;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricValidationException extends RuntimeException {
    private static final String CORRELATION_KEY = "requestId";

    private final ErrorCategory category;
    private final String errorCode;
    private final Map<String, Object> details;
    private final String correlationId;

    public MetricValidationException(ErrorCategory category,
                                     String errorCode,
                                     String message,
                                     Map<String, Object> details,
                                     Throwable cause) {
        super(message, cause);
        this.category = category;
        this.errorCode = errorCode;
        this.details = details == null ? null : Collections.unmodifiableMap(new HashMap<>(details));
        this.correlationId = MDC.get(CORRELATION_KEY);
    }

    public static MetricValidationException withViolations(ErrorCategory category,
                                                           String errorCode,
                                                           String message,
                                                           List<ValidationViolation> violations) {
        Map<String, Object> details = new HashMap<>();
        details.put("violations", violations == null ? List.of() : List.copyOf(violations));
        return new MetricValidationException(category, errorCode, message, details, null);
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
