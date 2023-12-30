package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceDAO extends JpaRepository<Preference, Long> {

    List<Preference> findAllByDoctorsId(Long doctorId);

}
