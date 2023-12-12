package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import org.cswteams.ms3.entity.category.TemporaryCondition;

import javax.persistence.*;

@Entity
@Data
public class ConfigVincoloMaxPeriodoConsecutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "max_periodo_id", nullable = false)
    private Long id;

    @ManyToOne
    private TemporaryCondition categoriaVincolata;

    private int numMaxMinutiConsecutivi;


    public ConfigVincoloMaxPeriodoConsecutivo(){

    }

    public ConfigVincoloMaxPeriodoConsecutivo(TemporaryCondition categoriaVincolata, int numMaxOreConsecutive) {
        this.categoriaVincolata = categoriaVincolata;
        this.numMaxMinutiConsecutivi = numMaxOreConsecutive;
    }
}
