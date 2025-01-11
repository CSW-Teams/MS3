package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.HolidayCategory;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * This entity models a recurring holiday,
 * i.e. an holiday that is repeated each year on the same day(s).
 */
@Entity
@Table(name = "recurrent_holiday")
@Getter
public class RecurrentHoliday {

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

    @NotNull
    @Column(name = "start_day")
    private Integer startDay ;

    @NotNull
    @Column(name = "start_month")
    private Integer startMonth ;

    @NotNull
    @Column(name = "end_day")
    private Integer endDay ;

    @NotNull
    @Column(name = "end_month")
    private Integer endMonth;

    /**
     * holiday location
     */
    private String location;

    /**
     * Default constructor needed by Lombok
     */
    protected RecurrentHoliday() {
    }

    /**
     * Check if the provided year is leap
     *
     * @param year year to be checked
     * @return <code>true</code> if <code>year</code> is leap, <code>false</code> elsewhere.
     */
    private boolean checkLeapYear(int year) {
        if (year % 4 == 0) {

            // if the year is century
            if (year % 100 == 0) {

                // if year is divided by 400
                // then it is a leap year
                return year % 400 == 0;
            }

            // if the year is not century
            else
                return true;
        }

        else
            return false;
    }

    public RecurrentHoliday(String name, HolidayCategory category, int startDay, int startMonth, int endDay, int endMonth, String location) {
        this.name = name;
        this.category = category;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.location = location;
    }

    /**
     * Get an <code>Holiday</code> object related to this recurring holiday,
     * i.e. a single occurrence of the recurring holiday, for the specified year.
     * @param year year for which the <code>Holiday</code> object is needed
     * @return a <code>Holiday</code> object, as istance of the recurring holiday for the year <code>year</code>.
     */
    public Holiday toHolidayOfYear(int year) {

        int newStartDay = this.startDay, newEndDay = this.endDay ;
        int newStartMonth = this.startMonth ;

        if(startDay == 29 && startMonth == 2) {
            if(!checkLeapYear(year)) {
                newStartDay = 1 ;
                newStartMonth = 3 ;
            }
        }
        if(endDay == 29 && endMonth == 2) {
            if(!checkLeapYear(year)) {
                newEndDay = 28 ;
            }
        }

        LocalDate start = LocalDate.of(year, newStartMonth, newStartDay) ;
        LocalDate end = LocalDate.of(year, endMonth, newEndDay) ;

        return new Holiday(this.name, this.category, start.toEpochDay(), end.toEpochDay(), this.location) ;
    }
}
