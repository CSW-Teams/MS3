package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import org.cswteams.ms3.entity.Categoria;

import javax.persistence.*;

@Entity
@Data
public class ConfigVincoloMaxPeriodoConsecutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Categoria categoriaVincolata;

    private int numMaxMinutiConsecutivi;


    public ConfigVincoloMaxPeriodoConsecutivo(){

    }

    public ConfigVincoloMaxPeriodoConsecutivo(Categoria categoriaVincolata, int numMaxOreConsecutive) {
        this.categoriaVincolata = categoriaVincolata;
        this.numMaxMinutiConsecutivi = numMaxOreConsecutive;
    }
}
