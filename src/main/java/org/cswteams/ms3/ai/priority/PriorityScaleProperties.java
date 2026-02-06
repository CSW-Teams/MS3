package org.cswteams.ms3.ai.priority;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ai.rescheduling.priority-scale")
public class PriorityScaleProperties {

    private Map<String, Double> defaults = new LinkedHashMap<>();
    private Map<String, Double> overrides = new LinkedHashMap<>();

    public Map<String, Double> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, Double> defaults) {
        this.defaults = defaults;
    }

    public Map<String, Double> getOverrides() {
        return overrides;
    }

    public void setOverrides(Map<String, Double> overrides) {
        this.overrides = overrides;
    }
}
