package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;

public interface AgentBroker {

    AiTokenBudgetGuardResult previewTokenBudget(AiBrokerRequest request);

    AiScheduleVariantsResponse requestSchedule(AiBrokerRequest request);
}
