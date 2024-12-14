package org.cswteams.ms3.dto;

import lombok.Getter;
import org.cswteams.ms3.utils.input_integer.IntegerValue;

import javax.validation.constraints.Min;

@Getter
public class ConfigConstraintDTO {

    @Min(value = 0, message = "not negative")
    private Integer periodDaysNo;

    /**
     * in minutes
     */
    @Min(value = 0, message = "not negative")
    private Integer periodMaxTime;

    //Parametro ConstraintTurniContigui

    @Min(value = 0, message = "not negative")
    private Integer horizonNightShift;

    //Parametri ConstraintMaxPeriodoConsecutivo
    /**
     * in minutes
     */
    @Min(value = 0, message = "not negative")
    private Integer maxConsecutiveTimeForEveryone;

    @Min(value = 0, message = "not negative")
    private Integer maxConsecutiveTimeForOver62;

    @Min(value = 0, message = "not negative")
    private Integer maxConsecutiveTimeForPregnant;

    public ConfigConstraintDTO() {

    }

    public ConfigConstraintDTO(@IntegerValue String periodDaysNo, @IntegerValue String periodMaxTime,
                               @IntegerValue String horizonNightShift, @IntegerValue String maxConsecutiveTimeForEveryone,
                               @IntegerValue String maxConsecutiveTimeForOver62, @IntegerValue String maxConsecutiveTimeForPregnant) {

        this.horizonNightShift = Integer.parseInt(horizonNightShift);
        this.periodDaysNo = Integer.parseInt(periodDaysNo);
        this.periodMaxTime = Integer.parseInt(periodMaxTime)*60;
        this.maxConsecutiveTimeForEveryone = Integer.parseInt(maxConsecutiveTimeForEveryone)*60;
        this.maxConsecutiveTimeForOver62 = Integer.parseInt(maxConsecutiveTimeForOver62)*60;
        this.maxConsecutiveTimeForPregnant = Integer.parseInt(maxConsecutiveTimeForPregnant)*60;

    }

}
