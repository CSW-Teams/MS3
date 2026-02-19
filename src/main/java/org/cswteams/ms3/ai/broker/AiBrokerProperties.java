package org.cswteams.ms3.ai.broker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ai.broker")
public class AiBrokerProperties {

    private AgentProvider provider = AgentProvider.GEMMA;
    private String gemmaUrl;
    private String gemmaApiKey;
    private String llama70bUrl;
    private String llama70bApiKey;
    private String llama70bModel = "llama3-70b-8192";
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(60);
    private Duration totalTimeout = Duration.ofSeconds(90);
    private int maxRetries = 3;
    private Duration retryBackoff = Duration.ZERO;
    private int scheduleValidationMaxRetries = 2;

    public AgentProvider getProvider() {
        return provider;
    }

    public void setProvider(AgentProvider provider) {
        this.provider = provider;
    }

    public String getGemmaUrl() {
        return gemmaUrl;
    }

    public void setGemmaUrl(String gemmaUrl) {
        this.gemmaUrl = gemmaUrl;
    }

    public String getGemmaApiKey() {
        return gemmaApiKey;
    }

    public void setGemmaApiKey(String gemmaApiKey) {
        this.gemmaApiKey = gemmaApiKey;
    }

    public String getLlama70bUrl() {
        return llama70bUrl;
    }

    public void setLlama70bUrl(String llama70bUrl) {
        this.llama70bUrl = llama70bUrl;
    }

    public String getLlama70bApiKey() {
        return llama70bApiKey;
    }

    public void setLlama70bApiKey(String llama70bApiKey) {
        this.llama70bApiKey = llama70bApiKey;
    }

    public String getLlama70bModel() {
        return llama70bModel;
    }

    public void setLlama70bModel(String llama70bModel) {
        this.llama70bModel = llama70bModel;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getTotalTimeout() {
        return totalTimeout;
    }

    public void setTotalTimeout(Duration totalTimeout) {
        this.totalTimeout = totalTimeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(Duration retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public int getScheduleValidationMaxRetries() {
        return scheduleValidationMaxRetries;
    }

    public void setScheduleValidationMaxRetries(int scheduleValidationMaxRetries) {
        if (scheduleValidationMaxRetries < 0) {
            throw new IllegalArgumentException("ai.broker.schedule-validation-max-retries must be >= 0");
        }
        this.scheduleValidationMaxRetries = scheduleValidationMaxRetries;
    }
}
