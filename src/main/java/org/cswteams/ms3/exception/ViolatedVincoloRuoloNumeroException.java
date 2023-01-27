package org.cswteams.ms3.exception;

import org.cswteams.ms3.control.utils.ConvertitoreData;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.RuoloEnum;

public class ViolatedVincoloRuoloNumeroException extends ViolatedConstraintException{

    public ViolatedVincoloRuoloNumeroException(AssegnazioneTurno assegnaione, Utente utente) {
        super(String.format("Il turno %s del giorno %s in %s non può essere allocato. Sono stati allocati troppi utenti con ruolo %s. Il problema riguarda l'utente %s %s",
                assegnaione.getTurno().getTipologiaTurno(), ConvertitoreData.daStandardVersoTestuale(assegnaione.getData().toString()), assegnaione.getTurno().getServizio().getNome(),utente.getRuoloEnum().toString(), utente.getNome(), utente.getCognome()));
    }

    public ViolatedVincoloRuoloNumeroException(AssegnazioneTurno assegnaione, RuoloNumero ruoloNumero, int numero) {
        super(String.format("Il turno %s del giorno %s in %s non può essere allocato. Sono stati allocati troppi/pochi utenti con ruolo %s. Come minimo devono essere associati %d utenti di quel ruolo, ma ne sono stati assegnati %d ",
                assegnaione.getTurno().getTipologiaTurno(), ConvertitoreData.daStandardVersoTestuale(assegnaione.getData().toString()), assegnaione.getTurno().getServizio().getNome(), ruoloNumero.getRuolo().toString(), ruoloNumero.getNumero(), numero));
    }


}
