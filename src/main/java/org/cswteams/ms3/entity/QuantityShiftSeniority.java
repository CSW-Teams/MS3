package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.Seniority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Questa classe permette di memorizzare quanti utenti di ogni ruolo sono necessari in un turno specifico.
 * Ad esempio nel turno notturno in reparto devono esserci 1 strutturato e 1 specializzando tra gli utenti allocati.
 */
@Entity
@Data
public class QuantityShiftSeniority {

    @Id
    @GeneratedValue
    private Long id;

    //Ruolo dell'utente
    private Seniority seniority;

    //Numero di utenti di un ruolo specifico da allocare per un turno specifico
    private int quantity;

    public QuantityShiftSeniority(Long id, Seniority seniority, int quantity) {
        this.id = id;
        this.seniority = seniority;
        this.quantity = quantity;
    }

    public QuantityShiftSeniority(Seniority seniority, int quantity) {
        this.seniority = seniority;
        this.quantity = quantity;
    }

    public QuantityShiftSeniority(){

    }


}
