package org.cswteams.ms3.dao;

import java.util.List;

import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayDAO extends JpaRepository<Holiday, Long>{

    /** Ritrova tutte le festività registrate*/
    List<Holiday> findAll();

    /** Ritrova tutti i periodi di festività con il nome indicato */
    List<Holiday>findByName(String name);

    /** ritrova tutti i periodi di festività contenuti nel range indicato */
    List<Holiday> findByStartDateEpochDayGreaterThanEqualAndEndDateEpochDayLessThanEqual(long start, long end);

    /**finds all holidays of a given category */
    List<Holiday> findByCategory(HolidayCategory category);

    @Query("select h from Holiday h where (h.startDateEpochDay >= ?1 and h.startDateEpochDay <= ?2) or (h.endDateEpochDay >= ?1 and h.endDateEpochDay <= ?2)")
    List<Holiday> areThereHolidaysInYear(long start, long end) ;
}
