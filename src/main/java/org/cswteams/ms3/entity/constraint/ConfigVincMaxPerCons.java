package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import org.cswteams.ms3.entity.condition.Condition;

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
    private Condition categoriaVincolata;

    @Transient
    private int numMaxMinutiConsecutivi;


    public ConfigVincMaxPerCons(){

    }

    public ConfigVincMaxPerCons(Condition categoriaVincolata, int numMaxOreConsecutive) {
        this.categoriaVincolata = categoriaVincolata;
        this.numMaxMinutiConsecutivi = numMaxOreConsecutive;
    }
}
