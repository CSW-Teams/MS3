package org.cswteams.ms3.ai.priority;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PriorityScaleMerger {

    private PriorityScaleMerger() {
    }

    public static PriorityScaleConfig merge(PriorityScaleConfig base, PriorityScaleConfig override) {
        if (override == null) {
            return base;
        }
        PriorityScaleConfig merged = new PriorityScaleConfig();
        merged.setVersion(override.getVersion() != null ? override.getVersion() : base.getVersion());

        Map<String, PriorityScaleConfig.DimensionConfig> mergedDimensions = new LinkedHashMap<>();
        Map<String, PriorityScaleConfig.DimensionConfig> baseDimensions = base.getDimensions();
        Map<String, PriorityScaleConfig.DimensionConfig> overrideDimensions = override.getDimensions();

        for (Map.Entry<String, PriorityScaleConfig.DimensionConfig> entry : baseDimensions.entrySet()) {
            String key = entry.getKey();
            PriorityScaleConfig.DimensionConfig mergedDimension = mergeDimension(entry.getValue(), overrideDimensions.get(key));
            mergedDimensions.put(key, mergedDimension);
        }

        for (Map.Entry<String, PriorityScaleConfig.DimensionConfig> entry : overrideDimensions.entrySet()) {
            mergedDimensions.putIfAbsent(entry.getKey(), entry.getValue());
        }

        merged.setDimensions(mergedDimensions);
        return merged;
    }

    private static PriorityScaleConfig.DimensionConfig mergeDimension(PriorityScaleConfig.DimensionConfig base,
                                                                     PriorityScaleConfig.DimensionConfig override) {
        if (override == null) {
            return base;
        }
        PriorityScaleConfig.DimensionConfig merged = new PriorityScaleConfig.DimensionConfig();
        merged.setWeight(override.getWeight() != null ? override.getWeight() : base.getWeight());

        Map<String, PriorityScaleConfig.MetricConfig> mergedMetrics = new LinkedHashMap<>();
        Map<String, PriorityScaleConfig.MetricConfig> baseMetrics = base.getMetrics();
        Map<String, PriorityScaleConfig.MetricConfig> overrideMetrics = override.getMetrics();

        for (Map.Entry<String, PriorityScaleConfig.MetricConfig> entry : baseMetrics.entrySet()) {
            String key = entry.getKey();
            PriorityScaleConfig.MetricConfig mergedMetric = mergeMetric(entry.getValue(), overrideMetrics.get(key));
            mergedMetrics.put(key, mergedMetric);
        }

        for (Map.Entry<String, PriorityScaleConfig.MetricConfig> entry : overrideMetrics.entrySet()) {
            mergedMetrics.putIfAbsent(entry.getKey(), entry.getValue());
        }

        merged.setMetrics(mergedMetrics);
        return merged;
    }

    private static PriorityScaleConfig.MetricConfig mergeMetric(PriorityScaleConfig.MetricConfig base,
                                                                PriorityScaleConfig.MetricConfig override) {
        if (override == null) {
            return base;
        }
        PriorityScaleConfig.MetricConfig merged = new PriorityScaleConfig.MetricConfig();
        merged.setWeight(override.getWeight() != null ? override.getWeight() : base.getWeight());
        merged.setDirection(override.getDirection() != null ? override.getDirection() : base.getDirection());
        merged.setType(override.getType() != null ? override.getType() : base.getType());
        merged.setValueType(override.getValueType() != null ? override.getValueType() : base.getValueType());
        merged.setThreshold(override.getThreshold() != null ? override.getThreshold() : base.getThreshold());
        merged.setBooleanScoreTrue(override.getBooleanScoreTrue() != null ? override.getBooleanScoreTrue() : base.getBooleanScoreTrue());
        merged.setBooleanScoreFalse(override.getBooleanScoreFalse() != null ? override.getBooleanScoreFalse() : base.getBooleanScoreFalse());
        merged.setEnabled(override.getEnabled() != null ? override.getEnabled() : base.getEnabled());
        return merged;
    }
}
