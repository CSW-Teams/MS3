package org.cswteams.ms3.dto;

import lombok.Getter;
import org.cswteams.ms3.utils.validators.input_integer.IntegerValue;

import javax.validation.constraints.Min;

@Getter
public class ConfigConstraintDTO {

    @Min(value = 0, message = "not negative")
    private final Integer periodDaysNo;

    /**
     * in minutes
     */
    @Min(value = 0, message = "not negative")
    private final Integer periodMaxTime;

    //Parametro ConstraintTurniContigui

    @Min(value = 0, message = "not negative")
    private final Integer horizonNightShift;

    //Parametri ConstraintMaxPeriodoConsecutivo
    /**
     * in minutes
     */
    @Min(value = 0, message = "not negative")
    private final Integer maxConsecutiveTimeForEveryone;

    @Min(value = 0, message = "not negative")
    private final Integer maxConsecutiveTimeForOver62;

    @Min(value = 0, message = "not negative")
    private final Integer maxConsecutiveTimeForPregnant;

    public ConfigConstraintDTO(@IntegerValue String periodDaysNo, @IntegerValue String periodMaxTime,
                               @IntegerValue String horizonNightShift, @IntegerValue String maxConsecutiveTimeForEveryone,
                               @IntegerValue String maxConsecutiveTimeForOver62, @IntegerValue String maxConsecutiveTimeForPregnant) {

        this.horizonNightShift = Integer.parseInt(horizonNightShift);
        this.periodDaysNo = Integer.parseInt(periodDaysNo);
        this.periodMaxTime = Integer.parseInt(periodMaxTime);
        this.maxConsecutiveTimeForEveryone = Integer.parseInt(maxConsecutiveTimeForEveryone);
        this.maxConsecutiveTimeForOver62 = Integer.parseInt(maxConsecutiveTimeForOver62);
        this.maxConsecutiveTimeForPregnant = Integer.parseInt(maxConsecutiveTimeForPregnant);

    }

}
