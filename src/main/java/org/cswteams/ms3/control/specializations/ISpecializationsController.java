package org.cswteams.ms3.control.specializations;

import org.cswteams.ms3.dto.singleDoctorSpecializations.SingleDoctorSpecializationsDTO;

public interface ISpecializationsController {
    SingleDoctorSpecializationsDTO getSingleDoctorSpecializations(Long doctorID);
}
