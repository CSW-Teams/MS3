package org.cswteams.ms3.ai.decision;

import org.cswteams.ms3.ai.priority.PriorityScaleConfig;
import org.cswteams.ms3.ai.priority.PriorityScaleProperties;
import org.cswteams.ms3.ai.priority.PriorityScaleValidationException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DecisionAlgorithmServiceTest {

    @Test
    public void selectPreferred_usesWeightedScoreAndTieBreak() {
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(defaultConfig());

        AiScheduleCandidateMetrics candidateA = new AiScheduleCandidateMetrics("A",
                0.9, 0.5, 0.4, 0.3, 0.2);
        AiScheduleCandidateMetrics candidateB = new AiScheduleCandidateMetrics("B",
                0.8, 0.9, 0.6, 0.4, 0.3);

        AiScheduleCandidateMetrics preferred = service.selectPreferred(Arrays.asList(candidateA, candidateB));

        assertEquals("B", preferred.getCandidateId());
    }

    @Test
    public void selectPreferred_respectsOverrideWeights() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.35);
        defaults.put("UFFA_BALANCE", 0.25);
        defaults.put("SENTIMENT_TRANSITIONS", 0.15);
        defaults.put("UP_DELTA", 0.15);
        defaults.put("VARIANCE_DELTA", 0.10);
        Map<String, Double> overrides = new HashMap<>();
        overrides.put("COVERAGE", 0.10);
        overrides.put("UFFA_BALANCE", 0.50);
        overrides.put("SENTIMENT_TRANSITIONS", 0.20);
        overrides.put("UP_DELTA", 0.10);
        overrides.put("VARIANCE_DELTA", 0.10);
        properties.setDefaults(defaults);
        properties.setOverrides(overrides);

        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(new PriorityScaleConfig(properties));

        AiScheduleCandidateMetrics candidateA = new AiScheduleCandidateMetrics("A",
                0.9, 0.4, 0.8, 0.6, 0.4);
        AiScheduleCandidateMetrics candidateB = new AiScheduleCandidateMetrics("B",
                0.7, 0.9, 0.4, 0.5, 0.5);

        AiScheduleCandidateMetrics preferred = service.selectPreferred(Arrays.asList(candidateA, candidateB));

        assertEquals("B", preferred.getCandidateId());
    }

    @Test
    public void selectPreferred_usesFixedDimensionTieBreak() {
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(defaultConfig());

        AiScheduleCandidateMetrics candidateA = new AiScheduleCandidateMetrics("A",
                0.8, 0.6, 0.5, 0.55, 0.575);
        AiScheduleCandidateMetrics candidateB = new AiScheduleCandidateMetrics("B",
                0.8, 0.5, 0.6, 0.6, 0.6);

        AiScheduleCandidateMetrics preferred = service.selectPreferred(Arrays.asList(candidateA, candidateB));

        assertEquals("A", preferred.getCandidateId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectPreferred_rejectsEmptyCandidates() {
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(defaultConfig());

        service.selectPreferred(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectPreferred_rejectsOutOfRangeMetric() {
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(defaultConfig());

        AiScheduleCandidateMetrics candidate = new AiScheduleCandidateMetrics("A",
                1.2, 0.5, 0.5, 0.5, 0.5);

        service.selectPreferred(Collections.singletonList(candidate));
    }

    @Test(expected = PriorityScaleValidationException.class)
    public void selectPreferred_propagatesInvalidScale() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        properties.setDefaults(Collections.emptyMap());
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(new PriorityScaleConfig(properties));
        AiScheduleCandidateMetrics candidate = new AiScheduleCandidateMetrics("A",
                0.8, 0.8, 0.8, 0.8, 0.8);

        service.selectPreferred(Collections.singletonList(candidate));
    }

    @Test
    public void selectPreferred_handlesRealisticScenario() {
        DecisionAlgorithmService service = new DecisionAlgorithmServiceImpl(defaultConfig());

        AiScheduleCandidateMetrics baseline = new AiScheduleCandidateMetrics("STANDARD",
                0.95, 0.55, 0.45, 0.40, 0.35);
        AiScheduleCandidateMetrics empathic = new AiScheduleCandidateMetrics("EMPATHIC",
                0.90, 0.70, 0.80, 0.60, 0.50);
        AiScheduleCandidateMetrics efficient = new AiScheduleCandidateMetrics("EFFICIENT",
                0.98, 0.60, 0.30, 0.35, 0.40);

        AiScheduleCandidateMetrics preferred = service.selectPreferred(Arrays.asList(baseline, empathic, efficient));

        assertEquals("EMPATHIC", preferred.getCandidateId());
    }

    private PriorityScaleConfig defaultConfig() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.35);
        defaults.put("UFFA_BALANCE", 0.25);
        defaults.put("SENTIMENT_TRANSITIONS", 0.15);
        defaults.put("UP_DELTA", 0.15);
        defaults.put("VARIANCE_DELTA", 0.10);
        properties.setDefaults(defaults);
        return new PriorityScaleConfig(properties);
    }
}
