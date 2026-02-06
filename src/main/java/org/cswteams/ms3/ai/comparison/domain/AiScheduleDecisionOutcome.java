package org.cswteams.ms3.ai.comparison.domain;

import java.util.Objects;

public class AiScheduleDecisionOutcome {

    private final String candidateId;
    private final Long scheduleId;
    private final ScheduleCandidateType type;

    public AiScheduleDecisionOutcome(String candidateId, Long scheduleId, ScheduleCandidateType type) {
        this.candidateId = candidateId;
        this.scheduleId = scheduleId;
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public ScheduleCandidateType getType() {
        return type;
    }
}
