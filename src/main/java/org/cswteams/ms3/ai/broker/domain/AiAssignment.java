package org.cswteams.ms3.ai.broker.domain;

import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

public class AiAssignment {

    private final String shiftId;
    private final Integer doctorId;
    private final Seniority roleCovered;
    private final ConcreteShiftDoctorStatus assignmentStatus;
    private final boolean forced;
    private final String violationNote;

    public AiAssignment(String shiftId, Integer doctorId, Seniority roleCovered, ConcreteShiftDoctorStatus assignmentStatus, boolean forced, String violationNote) {
        this.shiftId = shiftId;
        this.doctorId = doctorId;
        this.roleCovered = roleCovered;
        this.assignmentStatus = assignmentStatus;
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

    public ConcreteShiftDoctorStatus getAssignmentStatus() {
        return assignmentStatus;
    }

    public boolean isForced() {
        return forced;
    }

    public String getViolationNote() {
        return violationNote;
    }
}
