package org.cswteams.ms3.control.specializations;

import org.cswteams.ms3.dto.singleDoctorSpecializations.SingleDoctorSpecializationsDTO;
import org.cswteams.ms3.entity.Specialization;

import java.util.Set;

public interface ISpecializationsController {
    SingleDoctorSpecializationsDTO getSingleDoctorSpecializations(Long doctorID);
}
