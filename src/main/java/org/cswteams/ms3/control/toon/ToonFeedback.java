package org.cswteams.ms3.control.toon;

import lombok.Getter;

@Getter
public class ToonFeedback {
    private final String shiftId;
    private final Long doctorId;
    private final String reasonCode;
    private final int severity;

    public ToonFeedback(String shiftId, Long doctorId, String reasonCode, int severity) {
        this.shiftId = shiftId;
        this.doctorId = doctorId;
        this.reasonCode = reasonCode;
        this.severity = severity;
    }
}
