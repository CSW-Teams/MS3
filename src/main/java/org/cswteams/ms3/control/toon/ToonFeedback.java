package org.cswteams.ms3.control.toon;

import lombok.Getter;

@Getter
public class ToonFeedback {
    private final String shiftId;
    private final Long doctorId;
    private final String reasonCode;
    private final int severity;
    private final String comment;

    public ToonFeedback(String shiftId, Long doctorId, String reasonCode, int severity) {
        this(shiftId, doctorId, reasonCode, severity, null);
    }

    public ToonFeedback(String shiftId, Long doctorId, String reasonCode, int severity, String comment) {
        this.shiftId = shiftId;
        this.doctorId = doctorId;
        this.reasonCode = reasonCode;
        this.severity = severity;
        this.comment = comment;
    }

    public enum Category {
        TOO_MANY_NIGHTS,
        TOO_LONG,
        REMOVAL_PENDING,
        REMOVAL_ACCEPTED,
        REMOVAL_REJECTED;

        public static boolean isKnown(String code) {
            if (code == null) {
                return false;
            }
            for (Category category : Category.values()) {
                if (category.name().equals(code)) {
                    return true;
                }
            }
            return false;
        }
    }
}
