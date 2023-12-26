package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;

import java.util.ArrayList;
import java.util.List;

public class DoctorAssignmentUtil {

    /**
     * This method retrieves all the doctors of some specific status (ON_DUTY, ON_CALL or REMOVED) which are assigned to
     * a determined concrete shift.
     * @param concreteShift The concrete shift from which the doctors have to be extracted
     * @param status The status of the doctors that have to be extracted
     * @return A list of the extracted doctors
     */
    public static List<Doctor> getDoctorsInConcreteShift(ConcreteShift concreteShift, List<ConcreteShiftDoctorStatus> status) {
        List<Doctor> doctorsList = new ArrayList<>();
        List<DoctorAssignment> allDoctorAssignments = concreteShift.getDoctorAssignmentList();

        for(ConcreteShiftDoctorStatus s : status) {
            for (DoctorAssignment da : allDoctorAssignments) {
                if (da.getConcreteShiftDoctorStatus == s)
                    doctorsList.add(da.getDoctor());

            }
        }
        return doctorsList;

    }

}
