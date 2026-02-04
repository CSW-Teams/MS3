package org.cswteams.ms3.ai.priority;

import java.util.LinkedHashMap;
import java.util.Map;

public class PriorityScaleConfig {

    private Integer version;
    private Map<String, DimensionConfig> dimensions = new LinkedHashMap<>();

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, DimensionConfig> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, DimensionConfig> dimensions) {
        this.dimensions = dimensions == null ? new LinkedHashMap<>() : new LinkedHashMap<>(dimensions);
    }

    public static class DimensionConfig {
        private Double weight;
        private Map<String, MetricConfig> metrics = new LinkedHashMap<>();

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Map<String, MetricConfig> getMetrics() {
            return metrics;
        }

        public void setMetrics(Map<String, MetricConfig> metrics) {
            this.metrics = metrics == null ? new LinkedHashMap<>() : new LinkedHashMap<>(metrics);
        }
    }

    public static class MetricConfig {
        private Double weight;
        private MetricDirection direction;
        private MetricType type;
        private MetricValueType valueType;
        private Double threshold;
        private Double booleanScoreTrue;
        private Double booleanScoreFalse;
        private Boolean enabled;

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public MetricDirection getDirection() {
            return direction;
        }

        public void setDirection(MetricDirection direction) {
            this.direction = direction;
        }

        public MetricType getType() {
            return type;
        }

        public void setType(MetricType type) {
            this.type = type;
        }

        public MetricValueType getValueType() {
            return valueType;
        }

        public void setValueType(MetricValueType valueType) {
            this.valueType = valueType;
        }

        public Double getThreshold() {
            return threshold;
        }

        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public Double getBooleanScoreTrue() {
            return booleanScoreTrue;
        }

        public void setBooleanScoreTrue(Double booleanScoreTrue) {
            this.booleanScoreTrue = booleanScoreTrue;
        }

        public Double getBooleanScoreFalse() {
            return booleanScoreFalse;
        }

        public void setBooleanScoreFalse(Double booleanScoreFalse) {
            this.booleanScoreFalse = booleanScoreFalse;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
