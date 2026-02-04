package org.cswteams.ms3.ai.priority;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.priority-scale")
public class PriorityScaleProperties {

    private String defaultResource = "classpath:ai/priority-scale-default.json";
    private String overrideResource;

    public String getDefaultResource() {
        return defaultResource;
    }

    public void setDefaultResource(String defaultResource) {
        this.defaultResource = defaultResource;
    }

    public String getOverrideResource() {
        return overrideResource;
    }

    public void setOverrideResource(String overrideResource) {
        this.overrideResource = overrideResource;
    }
}
