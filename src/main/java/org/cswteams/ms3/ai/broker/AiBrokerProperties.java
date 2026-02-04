package org.cswteams.ms3.ai.broker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "ai.broker")
public class AiBrokerProperties {
    /**
     * Selected AI provider for scheduling operations.
     */
    private AiProvider provider = AiProvider.GEMMA;

    /**
     * Base URL for the Gemma adapter.
     */
    private String gemmaUrl;

    /**
     * Base URL for the Llama 70B adapter.
     */
    private String llama70bUrl;

    /**
     * Connection timeout for AI provider requests.
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * Read timeout for AI provider requests.
     */
    private Duration readTimeout = Duration.ofSeconds(60);

    /**
     * Total timeout for AI provider requests.
     */
    private Duration totalTimeout = Duration.ofSeconds(90);

    /**
     * Maximum number of retries for AI provider requests.
     */
    private int maxRetries = 3;

    /**
     * Backoff between retries.
     */
    private Duration retryBackoff = Duration.ZERO;
}
