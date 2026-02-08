package org.cswteams.ms3.ai.broker;

public interface AgentProviderAdapter {

    AgentProvider provider();

    String execute(AiBrokerRequest request);
}
