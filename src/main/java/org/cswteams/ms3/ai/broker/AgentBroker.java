package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;

public interface AgentBroker {
    AiScheduleResponseDto requestSchedule(String requestPayload);
}
