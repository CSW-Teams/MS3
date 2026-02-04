package org.cswteams.ms3.ai.broker;

public interface AgentProviderAdapter {
    AiProvider provider();

    String requestSchedule(String requestPayload);
}
