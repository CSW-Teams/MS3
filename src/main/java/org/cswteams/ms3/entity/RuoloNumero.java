package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Questa classe permette di memorizzare quanti utenti di ogni ruolo sono necessari in un turno specifico.
 * Ad esempio nel turno notturno in reparto devono esserci 1 strutturato e 1 specializzando tra gli utenti allocati.
 */
@Entity
@Data
public class RuoloNumero {

    @Id
    @GeneratedValue
    private Long id;

    //Ruolo dell'utente
    private RuoloEnum ruolo;

    //Numero di utenti di un ruolo specifico da allocare per un turno specifico
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
