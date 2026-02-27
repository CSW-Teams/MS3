package org.cswteams.ms3.audit.validation;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MetricComputationValidatorTest {

    private final MetricComputationValidator validator = new MetricComputationValidator();

    @Test
    public void validate_missingRequiredMetric_shouldThrow() {
        MetricComputationResult result = new MetricComputationResult(
                Map.of("coverage", 0.8),
                List.of("A")
        );

        MetricValidationException ex;
        try {
            validator.validateOrThrow(result, Set.of("coverage", "uffaBalance"));
            fail("Expected MetricValidationException");
            return;
        } catch (MetricValidationException e) {
            ex = e;
        }

        assertEquals("METRICS_VALIDATION_FAILED", ex.getErrorCode());
        assertNotNull(ex.getDetails());
    }

    @Test
    public void validate_nanMetric_shouldThrow() {
        MetricComputationResult result = new MetricComputationResult(
                Map.of("coverage", Double.NaN),
                List.of("A")
        );

        MetricValidationException ex;
        try {
            validator.validateOrThrow(result, Set.of("coverage"));
            fail("Expected MetricValidationException");
            return;
        } catch (MetricValidationException e) {
            ex = e;
        }

        assertEquals("METRICS_VALIDATION_FAILED", ex.getErrorCode());
    }

    @Test
    public void validate_ratingOutOfRange_shouldThrow() {
        MetricComputationResult result = new MetricComputationResult(
                Map.of("rating", 6.5),
                List.of("A")
        );

        MetricValidationException ex;
        try {
            validator.validateOrThrow(result, Set.of("rating"));
            fail("Expected MetricValidationException");
            return;
        } catch (MetricValidationException e) {
            ex = e;
        }

        assertEquals("METRICS_VALIDATION_FAILED", ex.getErrorCode());
    }
}
