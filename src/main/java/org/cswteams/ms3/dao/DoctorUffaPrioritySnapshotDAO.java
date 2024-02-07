package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.DoctorUffaPrioritySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorUffaPrioritySnapshotDAO extends JpaRepository<DoctorUffaPrioritySnapshot,Long> {
    List<DoctorUffaPrioritySnapshot> findAll();
}
