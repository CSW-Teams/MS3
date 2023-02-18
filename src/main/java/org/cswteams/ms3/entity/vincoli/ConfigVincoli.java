package org.cswteams.ms3.entity.vincoli;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class ConfigVincoli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //Parametri VincoloMaxOrePeriodo
    private int numGiorniPeriodo;
    private int maxMinutiPeriodo;

    //Parametro VincoloTipologieTurniContigue
    private int horizonTurnoNotturno;

    //Parametri VincoloMaxPeriodoConsecutivo
    private int numMaxMinutiConsecutiviPerTutti;

    @OneToMany
    private List<ConfigVincoloMaxPeriodoConsecutivo> configMaxPeriodoConsecutivoPerCategoria;

    public ConfigVincoli(int numGiorniPeriodo, int maxOrePeriodo, int horizonTurnoNotturno, int numMaxOreConsecutivePerTutti, List<ConfigVincoloMaxPeriodoConsecutivo> configVincoloMaxPeriodoConsecutivoPerCategoria) {
        this.numGiorniPeriodo = numGiorniPeriodo;
        this.maxMinutiPeriodo = maxOrePeriodo;
        this.horizonTurnoNotturno = horizonTurnoNotturno;
        this.numMaxMinutiConsecutiviPerTutti = numMaxOreConsecutivePerTutti;
        this.configMaxPeriodoConsecutivoPerCategoria = configVincoloMaxPeriodoConsecutivoPerCategoria;
    }

    public ConfigVincoli(){

    }
}
