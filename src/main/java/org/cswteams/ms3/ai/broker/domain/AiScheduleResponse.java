package org.cswteams.ms3.ai.broker.domain;

import org.cswteams.ms3.ai.protocol.utils.AiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiScheduleResponse {

    private final AiStatus status;
    private final AiMetadata metadata;
    private final List<AiAssignment> assignments;
    private final List<AiUncoveredShift> uncoveredShifts;
    private final List<AiUffaDelta> uffaDelta;

    public AiScheduleResponse(AiStatus status,
                              AiMetadata metadata,
                              List<AiAssignment> assignments,
                              List<AiUncoveredShift> uncoveredShifts,
                              List<AiUffaDelta> uffaDelta) {
        this.status = status;
        this.metadata = metadata;
        this.assignments = unmodifiableCopy(assignments);
        this.uncoveredShifts = unmodifiableCopy(uncoveredShifts);
        this.uffaDelta = unmodifiableCopy(uffaDelta);
    }

    private static <T> List<T> unmodifiableCopy(List<T> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public AiStatus getStatus() {
        return status;
    }

    public AiMetadata getMetadata() {
        return metadata;
    }

    public List<AiAssignment> getAssignments() {
        return assignments;
    }

    public List<AiUncoveredShift> getUncoveredShifts() {
        return uncoveredShifts;
    }

    public List<AiUffaDelta> getUffaDelta() {
        return uffaDelta;
    }
}
