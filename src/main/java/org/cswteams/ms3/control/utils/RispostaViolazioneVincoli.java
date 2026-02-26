package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.control.scheduler.ConstraintCheckResult;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RispostaViolazioneVincoli {
    
    /** messaggi informativi per l'utente riguardo all'esito di un'assegnazione
     */
    List<String> messagges = new ArrayList<>();

    /** Structured violated constraints with severity. */
    List<ConstraintCheckResult> violations = new ArrayList<>();
}
