package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AgentBrokerImpl implements AgentBroker {
    private final AiBrokerProperties properties;
    private final AiScheduleJsonParser jsonParser;
    private final Map<AiProvider, AgentProviderAdapter> adaptersByProvider;

    public AgentBrokerImpl(AiBrokerProperties properties,
                           AiScheduleJsonParser jsonParser,
                           List<AgentProviderAdapter> adapters) {
        this.properties = properties;
        this.jsonParser = jsonParser;
        this.adaptersByProvider = new EnumMap<>(AiProvider.class);
        for (AgentProviderAdapter adapter : adapters) {
            this.adaptersByProvider.put(adapter.provider(), adapter);
        }
    }

    @Override
    public AiScheduleResponseDto requestSchedule(String requestPayload) {
        AiProvider provider = properties.getProvider();
        AgentProviderAdapter adapter = adaptersByProvider.get(provider);
        if (adapter == null) {
            throw new IllegalStateException("No AI adapter registered for provider: " + provider);
        }
        String responsePayload = adapter.requestSchedule(requestPayload);
        return jsonParser.parse(responsePayload);
    }
}
