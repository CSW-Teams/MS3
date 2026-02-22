package org.cswteams.ms3.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoctorUffaPriorityTest {

    @Test
    void getAssegnazioniTurnoCacheReturnsEmptyListWhenScheduleIsMissing() {
        Doctor doctor = new Doctor();
        doctor.setId(42L);

        DoctorUffaPriority priority = new DoctorUffaPriority(doctor);

        List<ConcreteShift> assignments = priority.getAssegnazioniTurnoCache();

        assertNotNull(assignments);
        assertTrue(assignments.isEmpty());
    }
}
