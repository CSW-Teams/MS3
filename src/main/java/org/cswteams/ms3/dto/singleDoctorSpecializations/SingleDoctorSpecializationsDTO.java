package org.cswteams.ms3.dto.singleDoctorSpecializations;

import java.util.Set;

public class SingleDoctorSpecializationsDTO {
    private Long doctorID;
    private String doctorName;
    private String doctorLastname;
    private Set<String> specializations;

    public SingleDoctorSpecializationsDTO(Long doctorID, String doctorName, String doctorLastname, Set<String> specializations) {
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.doctorLastname = doctorLastname;
        this.specializations = specializations;
    }

}
