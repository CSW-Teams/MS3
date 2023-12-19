package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.condition.TemporaryCondition;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class ConfigVincMaxPerCons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "max_periodo_id", nullable = false)
    private Long id;

    @ManyToOne
    @NotNull
    private TemporaryCondition categoriaVincolata;

    @Transient
    private int numMaxMinutiConsecutivi;


    public ConfigVincMaxPerCons(){

    }

    public ConfigVincMaxPerCons(TemporaryCondition categoriaVincolata, int numMaxOreConsecutive) {
        this.categoriaVincolata = categoriaVincolata;
        this.numMaxMinutiConsecutivi = numMaxOreConsecutive;
    }
}
