package org.cswteams.ms3.dao;

import java.util.List;

import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayDAO extends JpaRepository<Holiday, Long>{

    /** Find all registered holidays*/
    List<Holiday> findAll();

    /** Finds all holiday periods with the given name */
    List<Holiday>findByName(String name);

    /** finds all holiday periods contained in the indicated range */
    List<Holiday> findByStartDateEpochDayGreaterThanEqualAndEndDateEpochDayLessThanEqual(long start, long end);

    /**finds all holidays of a given category */
    List<Holiday> findByCategory(HolidayCategory category);

    @Query("select h from Holiday h where (h.startDateEpochDay >= ?1 and h.startDateEpochDay <= ?2) or (h.endDateEpochDay >= ?1 and h.endDateEpochDay <= ?2)")
    List<Holiday> areThereHolidaysInYear(long start, long end) ;
}
