package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;

public interface AgentBroker {

    AiScheduleResponse requestSchedule(AiBrokerRequest request);
}
