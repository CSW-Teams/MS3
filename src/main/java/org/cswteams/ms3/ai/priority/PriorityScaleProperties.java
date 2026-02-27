package org.cswteams.ms3.ai.priority;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ai.rescheduling.priority-scale")
@Getter
@Setter
public class PriorityScaleProperties {

    private Map<String, Double> defaults = new LinkedHashMap<>();
    private Map<String, Double> overrides = new LinkedHashMap<>();

}
