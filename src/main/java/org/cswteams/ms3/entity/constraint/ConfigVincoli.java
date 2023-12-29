package org.cswteams.ms3.entity.constraint;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private int numGiorniPeriodo;
    @NotNull
    private int maxMinutiPeriodo;

    //Parametro ConstraintTipologieTurniContigue
    @NotNull
    private int horizonTurnoNotturno;

    //Parametri ConstraintMaxPeriodoConsecutivo
    @NotNull
    private int numMaxMinutiConsecutiviPerTutti;

    @OneToMany
    @NotNull
    private List<ConfigVincMaxPerCons> configVincMaxPerConsPerCategoria;

    public ConfigVincoli(int numGiorniPeriodo, int maxOrePeriodo, int horizonTurnoNotturno, int numMaxOreConsecutivePerTutti, List<ConfigVincMaxPerCons> configVincMaxPerConsPerCategoria) {
        this.numGiorniPeriodo = numGiorniPeriodo;
        this.maxMinutiPeriodo = maxOrePeriodo;
        this.horizonTurnoNotturno = horizonTurnoNotturno;
        this.numMaxMinutiConsecutiviPerTutti = numMaxOreConsecutivePerTutti;
        this.configVincMaxPerConsPerCategoria = configVincMaxPerConsPerCategoria;
    }

    public ConfigVincoli(){

    }
}
