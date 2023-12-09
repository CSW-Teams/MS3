package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Desiderata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesiderataDao extends JpaRepository<Desiderata, Long> {

    List<Desiderata> findAllByDoctorId(Long doctorId);

}
