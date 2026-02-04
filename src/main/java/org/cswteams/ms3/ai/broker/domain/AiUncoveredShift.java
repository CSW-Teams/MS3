package org.cswteams.ms3.ai.broker.domain;

public class AiUncoveredShift {

    private final String shiftId;
    private final String reason;

    public AiUncoveredShift(String shiftId, String reason) {
        this.shiftId = shiftId;
        this.reason = reason;
    }

    public String getShiftId() {
        return shiftId;
    }

    public String getReason() {
        return reason;
    }
}
