package org.cswteams.ms3.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Setter;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.utils.temporal_consistency.BeforeInTime;
import org.cswteams.ms3.utils.temporal_consistency.EpochDayComparator;

/**
 * This Entity models a period of days belonging to a holiday.
 */
@Entity
/*@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "name",
        "startDateEpochDay",
        "endDateEpochDay"
    })
})*/
@Data
@BeforeInTime(firstParam = "startDateEpochDay", secondParam = "endDateEpochDay", comparator = EpochDayComparator.class)
public class Holiday implements Serializable {

    /**
     * Default constructor needed by Lombok
     */
    public Holiday() {
    }

    public Holiday(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay, String Location) {
        this.name = name;
        this.category = category;
        this.startDateEpochDay = startDateEpochDay;
        this.endDateEpochDay = endDateEpochDay;
        this.location=Location;
        this.custom = false ;
    }

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Holiday name
     */
    @NotNull
    @NotEmpty
    private String name;

    /**
     * a label to group different holidays
     */
    @NotNull
    private HolidayCategory category;

    /**
     * holiday start date, in epoch days
     */
    @NotNull
    private long startDateEpochDay;

    /**
     * holiday end date, in epoch days
     */
    @NotNull
    private long endDateEpochDay;

    /**
     * holiday location
     */
    private String location;

    @Setter
    private boolean custom;

    /**
     * Convenience methods for working with LocalDate objects instead of timestamps
     */

    public LocalDate getStartDate() {
        return LocalDate.ofEpochDay(startDateEpochDay);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDateEpochDay = startDate.toEpochDay();
    }

    public LocalDate getEndDate() {
        return LocalDate.ofEpochDay(endDateEpochDay);
    }

    public void setEndDate(LocalDate endDate) {
        this.endDateEpochDay = endDate.toEpochDay();
    }

}
