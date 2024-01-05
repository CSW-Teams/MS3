package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ConcreteShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcreteShiftDAO extends JpaRepository<ConcreteShift,Long> {
    List<ConcreteShift> findByDoctorAssignmentList_Doctor_Id(@Param("idName") Long doctorId);

}


