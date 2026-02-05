package org.cswteams.ms3.ai.priority;

import org.junit.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PriorityScaleConfigTest {

    @Test
    public void getPriorityScale_mergesOverrides() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.4);
        defaults.put("UFFA_BALANCE", 0.2);
        defaults.put("SENTIMENT_TRANSITIONS", 0.2);
        defaults.put("UP_DELTA", 0.1);
        defaults.put("VARIANCE_DELTA", 0.1);
        properties.setDefaults(defaults);
        Map<String, Double> overrides = new HashMap<>();
        overrides.put("UFFA_BALANCE", 0.25);
        overrides.put("UP_DELTA", 0.05);
        overrides.put("VARIANCE_DELTA", 0.1);
        overrides.put("SENTIMENT_TRANSITIONS", 0.2);
        overrides.put("COVERAGE", 0.4);
        properties.setOverrides(overrides);

        PriorityScaleConfig config = new PriorityScaleConfig(properties);

        Map<PriorityDimension, Double> expected = new EnumMap<>(PriorityDimension.class);
        expected.put(PriorityDimension.COVERAGE, 0.4);
        expected.put(PriorityDimension.UFFA_BALANCE, 0.25);
        expected.put(PriorityDimension.SENTIMENT_TRANSITIONS, 0.2);
        expected.put(PriorityDimension.UP_DELTA, 0.05);
        expected.put(PriorityDimension.VARIANCE_DELTA, 0.1);

        assertEquals(expected, config.getPriorityScale());
    }

    @Test(expected = PriorityScaleValidationException.class)
    public void getPriorityScale_missingDimensionThrows() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.5);
        defaults.put("UFFA_BALANCE", 0.5);
        properties.setDefaults(defaults);

        new PriorityScaleConfig(properties).getPriorityScale();
    }

    @Test(expected = PriorityScaleValidationException.class)
    public void getPriorityScale_invalidSumThrows() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.5);
        defaults.put("UFFA_BALANCE", 0.2);
        defaults.put("SENTIMENT_TRANSITIONS", 0.2);
        defaults.put("UP_DELTA", 0.1);
        defaults.put("VARIANCE_DELTA", 0.2);
        properties.setDefaults(defaults);

        new PriorityScaleConfig(properties).getPriorityScale();
    }

    @Test(expected = PriorityScaleValidationException.class)
    public void getPriorityScale_unknownDimensionThrows() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.4);
        defaults.put("UFFA_BALANCE", 0.2);
        defaults.put("SENTIMENT_TRANSITIONS", 0.2);
        defaults.put("UP_DELTA", 0.1);
        defaults.put("UNKNOWN", 0.1);
        properties.setDefaults(defaults);

        new PriorityScaleConfig(properties).getPriorityScale();
    }

    @Test(expected = PriorityScaleValidationException.class)
    public void getPriorityScale_negativeWeightThrows() {
        PriorityScaleProperties properties = new PriorityScaleProperties();
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("COVERAGE", 0.4);
        defaults.put("UFFA_BALANCE", -0.2);
        defaults.put("SENTIMENT_TRANSITIONS", 0.2);
        defaults.put("UP_DELTA", 0.1);
        defaults.put("VARIANCE_DELTA", 0.5);
        properties.setDefaults(defaults);

        new PriorityScaleConfig(properties).getPriorityScale();
    }
}
