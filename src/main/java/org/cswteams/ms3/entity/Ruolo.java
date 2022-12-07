package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.cswteams.ms3.enums.RuoloEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ruolo {
    @Id
    @GeneratedValue
    private Long id;

    private RuoloEnum ruoloEnum;



    public Ruolo(RuoloEnum ruoloEnum) {
        this.ruoloEnum=ruoloEnum;
    }

    protected Ruolo() {

    }
}