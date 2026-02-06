package org.cswteams.ms3.ai.broker.domain;

import org.cswteams.ms3.enums.Seniority;

public class AiAssignment {

    private final String shiftId;
    private final Integer doctorId;
    private final Seniority roleCovered;
    private final boolean forced;
    private final String violationNote;

    public AiAssignment(String shiftId, Integer doctorId, Seniority roleCovered, boolean forced, String violationNote) {
        this.shiftId = shiftId;
        this.doctorId = doctorId;
        this.roleCovered = roleCovered;
        this.forced = forced;
        this.violationNote = violationNote;
    }

    public String getShiftId() {
        return shiftId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public Seniority getRoleCovered() {
        return roleCovered;
    }

    public boolean isForced() {
        return forced;
    }

    public String getViolationNote() {
        return violationNote;
    }
}
