package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableConfigurationProperties(AiBrokerProperties.class)
public class AiBrokerConfiguration {

    @Bean
    public AgentProviderAdapter gemmaAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        return new GemmaAgentAdapter(restTemplate, properties);
    }

    @Bean
    public AgentProviderAdapter llama70bAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        return new Llama70bAgentAdapter(restTemplate, properties);
    }

    @Bean
    public AgentBroker agentBroker(AiBrokerProperties properties,
                                   List<AgentProviderAdapter> adapters,
                                   AiScheduleJsonParser jsonParser) {
        return new AgentBrokerImpl(properties, adapters, jsonParser);
    }
}
