package org.cswteams.ms3.audit.validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MetricValidationExceptionHandler {

    @ExceptionHandler(MetricValidationException.class)
    public ResponseEntity<MetricValidationErrorResponse> handleMetricValidation(MetricValidationException ex) {
        MetricValidationErrorResponse response = new MetricValidationErrorResponse(
                "FAILURE",
                ex.getCategory(),
                ex.getErrorCode(),
                ex.getDetails(),
                ex.getCorrelationId()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
