package org.cswteams.ms3.exception;

import org.cswteams.ms3.control.utils.ConvertitoreData;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserCategoryPolicy;
import org.cswteams.ms3.entity.Utente;

public class ViolatedVincoloCategorieUtenteTurnoException extends ViolatedConstraintException{


	public ViolatedVincoloCategorieUtenteTurnoException(AssegnazioneTurno assegnazione, UserCategoryPolicy ucp, Utente utente) {
        super(String.format("l'utente %s %s non rispetta la policy %s %s" +
				" per il turno %s in %s. La violazione riguarda il giorno %s", 
				utente.getNome(), utente.getCognome(), ucp.getCategoria().getNome(), ucp.getPolicy(), assegnazione.getTurno().getTipologiaTurno(),
				assegnazione.getTurno().getServizio().getNome(), ConvertitoreData.daStandardVersoTestuale(assegnazione.getData().toString())));
	}
}