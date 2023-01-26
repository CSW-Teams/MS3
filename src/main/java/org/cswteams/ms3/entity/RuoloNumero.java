package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class RuoloNumero {

    @Id
    @GeneratedValue
    private Long id;

    private RuoloEnum ruolo;
    private int numero;

    public RuoloNumero(Long id, RuoloEnum ruolo, int numero) {
        this.id = id;
        this.ruolo = ruolo;
        this.numero = numero;
    }

    public RuoloNumero( RuoloEnum ruolo, int numero) {
        this.ruolo = ruolo;
        this.numero = numero;
    }

    public RuoloNumero(){

    }
}
