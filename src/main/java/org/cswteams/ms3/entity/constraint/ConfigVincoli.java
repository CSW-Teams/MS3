package org.cswteams.ms3.entity.constraint;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Parameters for <i>constraint</i> management.
 */
@Entity
@Getter
@Setter
public class ConfigVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_vincoli_id", nullable = false)
    private Long id;

    //Parametri ConstraintMaxOrePeriodo
    @NotNull
    private int periodDaysNo;

    /**
     * in minutes
     */
    @NotNull
    private int periodMaxTime;

    //Parametro ConstraintTurniContigui
    @NotNull
    private int horizonNightShift;

    //Parametri ConstraintMaxPeriodoConsecutivo
    /**
     * in minutes
     */
    @NotNull
    private int maxConsecutiveTimeForEveryone;

    @OneToMany
    @NotNull
    private List<ConfigVincMaxPerCons> configVincMaxPerConsPerCategoria;

    /**
     * Constructor
     *
     * @param periodDaysNo
     * @param periodMaxTime                    [minutes]
     * @param horizonNightShift
     * @param maxConsecutiveTimeForEveryone    [minutes]
     * @param configVincMaxPerConsPerCategoria
     */
    public ConfigVincoli(int periodDaysNo, int periodMaxTime, int horizonNightShift, int maxConsecutiveTimeForEveryone, List<ConfigVincMaxPerCons> configVincMaxPerConsPerCategoria) {
        this.periodDaysNo = periodDaysNo;
        this.periodMaxTime = periodMaxTime;
        this.horizonNightShift = horizonNightShift;
        this.maxConsecutiveTimeForEveryone = maxConsecutiveTimeForEveryone;
        this.configVincMaxPerConsPerCategoria = configVincMaxPerConsPerCategoria;
    }

    /**
     * Default constructor needed by Lombok
     */
    public ConfigVincoli() {

    }
}
