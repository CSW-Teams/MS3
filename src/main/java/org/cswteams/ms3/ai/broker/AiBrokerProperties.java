package org.cswteams.ms3.ai.broker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
}
