package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.ConcreteShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ConcreteShiftDAO extends JpaRepository<ConcreteShift,Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM concrete_shift as CS, " +
            "doctor_assignment as DA, ms3_system_user as doc " +
            "WHERE CS.concrete_shift_id=DA.concrete_shift_id and " +
            "DA.doctor_user_id=doc.user_id and " +
            "doc.user_id=:idName")
    Set<ConcreteShift> findByDoctorAssignmentList_Doctor_Id(@Param("idName") Long doctorId);

}


