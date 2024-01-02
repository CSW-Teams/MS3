package org.cswteams.ms3.exception;

import java.util.List;

import org.cswteams.ms3.control.utils.ConvertitoreData;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.UserCategoryPolicy;
import org.cswteams.ms3.entity.Utente;

public class ViolatedVincoloCategorieUtenteTurnoException extends ViolatedConstraintException{


	public ViolatedVincoloCategorieUtenteTurnoException(AssegnazioneTurno assegnazione, List<UserCategoryPolicy> brokenPolicies, Utente utente) {
        super(String.format("l'utente %s %s non rispetta le policies %s " +
				" per il turno %s in %s. La violazione riguarda il giorno %s", 
				utente.getNome(), utente.getCognome(), printBrokenPolicies(brokenPolicies), assegnazione.getTurno().getTipologiaTurno(),
				assegnazione.getTurno().getServizio().getNome(), ConvertitoreData.daStandardVersoTestuale(assegnazione.getData().toString())));
	}

	private static String printBrokenPolicies(List<UserCategoryPolicy> brokenPolicies) {
		StringBuilder sb = new StringBuilder();
		for(UserCategoryPolicy ucp: brokenPolicies){
			sb.append(ucp.getCategoria().getNome());
			sb.append(":");
			sb.append(ucp.getPolicy());
			sb.append(" ");
		}
		return sb.toString();
	}
}