package org.cswteams.ms3.ai.broker;

import java.util.Objects;

public class AiBrokerRequest {

    private final String toonPayload;
    private final String instructions;
    private final String correlationId;

    public AiBrokerRequest(String toonPayload, String instructions, String correlationId) {
        this.toonPayload = Objects.requireNonNull(toonPayload, "toonPayload");
        this.instructions = instructions;
        this.correlationId = correlationId;
    }

    public static AiBrokerRequest forToon(String toonPayload) {
        return new AiBrokerRequest(toonPayload, null, null);
    }

    public String getToonPayload() {
        return toonPayload;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
