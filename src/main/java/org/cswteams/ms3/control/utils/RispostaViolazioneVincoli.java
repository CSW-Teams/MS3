package org.cswteams.ms3.control.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RispostaViolazioneVincoli {
    
    /** messaggi informativi per l'utente riguardo all'esito di un'assegnazione
     */
    List<String> messagges = new ArrayList<>();
}
