package org.cswteams.ms3.ai.priority;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;

import javax.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriorityScaleValidatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PriorityScaleValidator validator = new PriorityScaleValidator();

    @Test
    void defaultPriorityScaleConfigIsValid() throws IOException {
        PriorityScaleConfig config = readDefaultConfig();
        assertDoesNotThrow(() -> validator.validate(config));
    }

    @Test
    void rejectsInvalidDimensionWeights() throws IOException {
        PriorityScaleConfig config = readDefaultConfig();
        config.getDimensions().get("software").setWeight(0.9);
        assertThrows(ValidationException.class, () -> validator.validate(config));
    }

    @Test
    void requiresThresholdForHardGateMetrics() throws IOException {
        PriorityScaleConfig config = readDefaultConfig();
        PriorityScaleConfig.MetricConfig metric = config.getDimensions()
                .get("operational")
                .getMetrics()
                .get("M3.2");
        metric.setThreshold(null);
        assertThrows(ValidationException.class, () -> validator.validate(config));
    }

    @Test
    void rejectsEnabledMetricWithNonZeroWeight() throws IOException {
        PriorityScaleConfig config = readDefaultConfig();
        PriorityScaleConfig.MetricConfig metric = config.getDimensions()
                .get("software")
                .getMetrics()
                .get("M2.4");
        metric.setEnabled(false);
        metric.setWeight(0.1);
        assertThrows(ValidationException.class, () -> validator.validate(config));
    }

    @Test
    void booleanMetricsRequireScoring() throws IOException {
        PriorityScaleConfig config = readDefaultConfig();
        PriorityScaleConfig.MetricConfig metric = config.getDimensions()
                .get("software")
                .getMetrics()
                .get("M2.16");
        metric.setBooleanScoreTrue(null);
        assertThrows(ValidationException.class, () -> validator.validate(config));
    }

    @Test
    void loaderUsesInjectedObjectMapper() throws IOException {
        TrackingObjectMapper mapper = new TrackingObjectMapper();
        PriorityScaleLoader loader = new PriorityScaleLoader(
                new PriorityScaleProperties(),
                new DefaultResourceLoader(),
                new PriorityScaleValidator(),
                mapper
        );
        loader.load();
        assertTrue(mapper.readCalled);
    }

    private PriorityScaleConfig readDefaultConfig() throws IOException {
        ClassPathResource resource = new ClassPathResource("ai/priority-scale-default.json");
        return objectMapper.readValue(resource.getInputStream(), PriorityScaleConfig.class);
    }

    private static class TrackingObjectMapper extends ObjectMapper {
        private boolean readCalled = false;

        @Override
        public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
            readCalled = true;
            return super.readValue(src, valueType);
        }
    }
}
