package org.cswteams.ms3.ai.broker.domain;

import org.cswteams.ms3.ai.protocol.utils.AiUffaQueue;

public class AiUffaDelta {

    private final Integer doctorId;
    private final AiUffaQueue queue;
    private final Integer points;

    public AiUffaDelta(Integer doctorId, AiUffaQueue queue, Integer points) {
        this.doctorId = doctorId;
        this.queue = queue;
        this.points = points;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public AiUffaQueue getQueue() {
        return queue;
    }

    public Integer getPoints() {
        return points;
    }
}
