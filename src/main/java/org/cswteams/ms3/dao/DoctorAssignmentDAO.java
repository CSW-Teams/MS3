package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorAssignmentDAO extends JpaRepository<DoctorAssignment,Long> {

    DoctorAssignment findByDoctorAndConcreteShift(Doctor doctor, ConcreteShift concreteShift);

}
