package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchedulazioneUtility {

    public static List<Doctor> getSpecificNumberOfDoctors(int structored, int senior, int junior) {
        List<Doctor> doctors = new ArrayList<Doctor>();
        for (int i = 1; i <= structored; i++) {
            doctors.add(new Doctor("structored_name"+i,
                    "structored_lastname"+i,
                    "taxcode",
                    LocalDate.of(2000, 1,1),
                    "email@gmail.com",
                    "passw",
                    Seniority.STRUCTURED,
                    Set.of(SystemActor.DOCTOR)));
        }
        for (int i = 1; i <= senior; i++) {
            doctors.add(new Doctor("senior_name"+i,
                    "senior_lastname"+i,
                    "taxcode",
                    LocalDate.of(2000, 1,1),
                    "email@gmail.com",
                    "passw",
                    Seniority.SPECIALIST_SENIOR,
                    Set.of(SystemActor.DOCTOR)));
        }
        for (int i = 1; i <= junior; i++) {
            doctors.add(new Doctor("junior_name"+i,
                    "junior_lastname"+i,
                    "taxcode",
                    LocalDate.of(2000, 1,1),
                    "email@gmail.com",
                    "passw",
                    Seniority.SPECIALIST_JUNIOR,
                    Set.of(SystemActor.DOCTOR)));
        }
        return doctors;
    }
}
