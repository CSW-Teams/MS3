package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorAssignmentDAO {

    DoctorAssignment findByDoctorAndConcreteShift(Doctor doctor, ConcreteShift concreteShift);

}
