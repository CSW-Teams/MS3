package org.cswteams.ms3.audit.validation;

import java.util.Map;

public class MetricValidationErrorResponse {
    private final String status;
    private final ErrorCategory category;
    private final String errorCode;
    private final Map<String, Object> details;
    private final String correlationId;

    public MetricValidationErrorResponse(String status,
                                         ErrorCategory category,
                                         String errorCode,
                                         Map<String, Object> details,
                                         String correlationId) {
        this.status = status;
        this.category = category;
        this.errorCode = errorCode;
        this.details = details;
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
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
