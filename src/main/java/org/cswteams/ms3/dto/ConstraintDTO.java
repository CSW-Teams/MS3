package org.cswteams.ms3.dto;

import lombok.Getter;

@Getter
public class ConstraintDTO {

    private int periodDaysNo;

    /**
     * in minutes
     */
    private int periodMaxTime;

    //Parametro ConstraintTurniContigui
    private int horizonNightShift;

    //Parametri ConstraintMaxPeriodoConsecutivo
    /**
     * in minutes
     */
    private int maxConsecutiveTimeForEveryone;

    private int maxConsecutiveTimeForOver62;

    private int maxConsecutiveTimeForPregnant;

    public ConstraintDTO() {

    }

    public ConstraintDTO(int periodDaysNo, int periodMaxTime, int horizonNightShift, int maxConsecutiveTimeForEveryone,
                         int maxConsecutiveTimeForOver62, int maxConsecutiveTimeForPregnant) {

        this.periodDaysNo = periodDaysNo;
        this.periodMaxTime = periodMaxTime;
        this.horizonNightShift = horizonNightShift;
        this.maxConsecutiveTimeForEveryone = maxConsecutiveTimeForEveryone;
        this.maxConsecutiveTimeForOver62 = maxConsecutiveTimeForOver62;
        this.maxConsecutiveTimeForPregnant = maxConsecutiveTimeForPregnant;

    }

}
