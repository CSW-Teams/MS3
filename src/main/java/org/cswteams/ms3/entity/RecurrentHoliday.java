package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.HolidayCategory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
public class RecurrentHoliday {

    @Id
    @GeneratedValue
    private Long id;

    /** nome della festività */
    @NotNull
    @NotEmpty
    private String name;
    /** una targetta per raggruppare diverse festività */
    @NotNull
    private HolidayCategory category;
    @NotNull
    private Integer startDay ;

    @NotNull
    private Integer startMonth ;

    @NotNull
    private Integer endDay ;

    @NotNull
    private Integer endMonth ;
    /** locazione */
    private String location;

    protected RecurrentHoliday() {}

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
