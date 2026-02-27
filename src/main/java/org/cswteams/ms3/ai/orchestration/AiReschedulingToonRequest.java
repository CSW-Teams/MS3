package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonRequestContext;

import java.util.Collections;
import java.util.Map;

public class AiReschedulingToonRequest {
    private final ToonRequestContext toonRequestContext;
    private final Map<Long, Long> pseudonymToDoctorId;

    public AiReschedulingToonRequest(ToonRequestContext toonRequestContext,
                                     Map<Long, Long> pseudonymToDoctorId) {
        this.toonRequestContext = toonRequestContext;
        this.pseudonymToDoctorId = pseudonymToDoctorId == null ? Collections.emptyMap() : Collections.unmodifiableMap(pseudonymToDoctorId);
    }

    public ToonRequestContext getToonRequestContext() {
        return toonRequestContext;
    }

    public Map<Long, Long> getPseudonymToDoctorId() {
        return pseudonymToDoctorId;
    }
}
