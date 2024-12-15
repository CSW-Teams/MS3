package org.cswteams.ms3.dto.holidays;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.cswteams.ms3.utils.validators.admissible_values.AdmissibleValues;
import org.cswteams.ms3.utils.validators.day_and_month.DayAndMonth;
import org.cswteams.ms3.utils.validators.start_end_day_month.StartEndDayMonth;
import org.cswteams.ms3.utils.validators.temporal_consistency.BeforeInTime;
import org.cswteams.ms3.utils.validators.temporal_consistency.EpochDayComparator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@DayAndMonth(day = "startDay", month = "startMonth") //This annotation checks also for ranges!
@DayAndMonth(day = "endDay", month = "endMonth")
@BeforeInTime(firstParam = "startEpochDay", secondParam = "endEpochDay", comparator = EpochDayComparator.class)
@StartEndDayMonth(startDay = "startDay", startMonth = "startMonth", endDay = "endDay", endMonth = "endMonth")
public class CustomHolidayDTOIn {

    @NotNull
    @NotEmpty
    private final String name ;

    @NotNull
    private final String location ;

    @NotNull
    @AdmissibleValues(values = {"Secular", "Civil", "National", "Religious", "Corporate"})
    private final String kind ;

    @NotNull
    private final Integer startDay ;

    @NotNull
    private final Integer startMonth ;

    @NotNull
    private final Integer endDay ;

    @NotNull
    private final Integer endMonth ;

    @NotNull
    private final Long startEpochDay ;

    @NotNull
    private final Long endEpochDay ;

    private final boolean isRecurrent ;

    /*private CustomHolidayDTOIn(
            @JsonProperty("name") String name,
            @JsonProperty("location") String location,
            @JsonProperty("kind") String kind,
            @JsonProperty("startDay") Integer startDay,
            @JsonProperty("startMonth") Integer startMonth,
            @JsonProperty("endDay") Integer endDay,
            @JsonProperty("endMonth") Integer endMonth) {
        this.name = name;
        this.location = location;
        this.kind = kind;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.startEpochDay = this.endEpochDay = 0 ;
        this.isRecurrent = true ;
    }

    private CustomHolidayDTOIn(
            @JsonProperty("name") String name,
            @JsonProperty("location") String location,
            @JsonProperty("kind") String kind,
            @JsonProperty("startEpochDay") Integer startEpochDay,
            @JsonProperty("endEpochDay") Integer endEpochDay) {
        this.name = name;
        this.location = location;
        this.kind = kind;
        this.startEpochDay = startEpochDay;
        this.endEpochDay = endEpochDay;
        this.startDay = this.startMonth = this.endDay = this.endMonth = 1 ;
        this.isRecurrent = false ;
    }*/

    public CustomHolidayDTOIn(
            @JsonProperty("recurrent") boolean isRecurrent,
            @JsonProperty("name") String name,
            @JsonProperty("location") String location,
            @JsonProperty("kind") String kind,
            @JsonProperty("startEpochDay") Long startEpochDay,
            @JsonProperty("endEpochDay") Long endEpochDay,
            @JsonProperty("startDay") Integer startDay,
            @JsonProperty("startMonth") Integer startMonth,
            @JsonProperty("endDay") Integer endDay,
            @JsonProperty("endMonth") Integer endMonth
    ) {
        if(isRecurrent) {
            this.name = name;
            this.location = location;
            this.kind = kind;
            this.startDay = startDay;
            this.startMonth = startMonth;
            this.endDay = endDay;
            this.endMonth = endMonth;
            this.startEpochDay = this.endEpochDay = 0L ;
            this.isRecurrent = true ;
        } else {
            this.name = name;
            this.location = location;
            this.kind = kind;
            this.startEpochDay = startEpochDay;
            this.endEpochDay = endEpochDay;
            this.startDay = this.startMonth = this.endDay = this.endMonth = 1 ;
            this.isRecurrent = false ; ;
        }
    }
}
