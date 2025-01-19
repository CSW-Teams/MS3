package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftDAO extends JpaRepository<Shift,Long> {

    List<Shift> findAllByMedicalServiceLabel(String nomeServizio);
    List<Shift> findAllByMedicalServiceLabelAndTimeSlot(String nomeServizio, TimeSlot timeSlot);

    // Recupera tutti gli shift non cancellati
    List<Shift> findByDeletedFalse();

}
