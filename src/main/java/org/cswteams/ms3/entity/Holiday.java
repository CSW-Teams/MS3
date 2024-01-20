package org.cswteams.ms3.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.utils.temporal_consistency.BeforeInTime;
import org.cswteams.ms3.utils.temporal_consistency.EpochDayComparator;

/**
 * Questa Entità modella un periodo di giorni appartenenti ad una festività.
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

    public Holiday() {
    }

    public Holiday(String name, HolidayCategory category, long startDateEpochDay, long endDateEpochDay, String Location) {
        this.name = name;
        this.category = category;
        this.startDateEpochDay = startDateEpochDay;
        this.endDateEpochDay = endDateEpochDay;
        this.location=Location;
    }

    @Id
    @GeneratedValue
    private Long id;

    /** nome della festività */
    @NotNull
    private String name;
    /** una targetta per raggruppare diverse festività */
    @NotNull
    private HolidayCategory category;
    /** data di inizio della festività, in giorni dall'Epoch */
    @NotNull
    private long startDateEpochDay;
    /** data di fine della festività, in giorni dall'Epoch */
    @NotNull
    private long endDateEpochDay;
    /** locazione */
    private String location;


    /** Metodi di convenienza per lavorare con oggetti LocalDate anziché con timestamp */

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

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public HolidayCategory getCategory() {
        return category;
    }

    public long getStartDateEpochDay() {
        return startDateEpochDay;
    }

    public long getEndDateEpochDay() {
        return endDateEpochDay;
    }
}
