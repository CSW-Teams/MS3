package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.Seniority;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

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
    private HashMap<Seniority,Integer> seniorityMap;

    @ManyToOne
    private Task task;
    //Numero di utenti di un ruolo specifico da allocare per un turno specifico

    public QuantityShiftSeniority(Long id, Map<Seniority,Integer> seniority,Task task) {
        this.id = id;
        this.seniorityMap =new HashMap<>(seniority);
        this.task = task;
    }

    public QuantityShiftSeniority(Map<Seniority,Integer> seniority, Task task) {
        this.seniorityMap =new HashMap<>(seniority);
        this.task = task;
    }

    public QuantityShiftSeniority(){

    }


}
