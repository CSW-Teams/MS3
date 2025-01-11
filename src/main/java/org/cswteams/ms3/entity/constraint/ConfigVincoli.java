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
@Table(name = "config_vincoli")
@Getter
@Setter
public class ConfigVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_vincoli_id", nullable = false)
    private Long id;

    //Parametri ConstraintMaxOrePeriodo
    @NotNull
    @Column(name = "period_days_no")
    private int periodDaysNo;

    /**
     * in minutes
     */
    @NotNull
    @Column(name = "period_max_time")
    private int periodMaxTime;

    //Parametro ConstraintTurniContigui
    @NotNull
    @Column(name = "horizon_night_shift")
    private int horizonNightShift;

    //Parametri ConstraintMaxPeriodoConsecutivo
    /**
     * in minutes
     */
    @NotNull
    @Column(name = "max_consecutive_time_for_everyone")
    private int maxConsecutiveTimeForEveryone;

    @OneToMany
    @NotNull
    @JoinTable(
            name = "config_vincoli_config_vinc_max_per_cons_per_categoria",
            joinColumns = @JoinColumn(name = "config_vincoli_config_vincoli_id"),
            inverseJoinColumns = @JoinColumn(name = "config_vinc_max_per_cons_per_categoria_max_periodo_id")
    )
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
