package org.cswteams.ms3.exception;

import java.util.List;

import org.cswteams.ms3.control.utils.ConvertitoreData;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
import org.cswteams.ms3.entity.doctor.Doctor;

public class ViolatedVincoloCategorieUtenteTurnoException extends ViolatedConstraintException{


	public ViolatedVincoloCategorieUtenteTurnoException(ConcreteShift assegnazione, List<ConditionPolicy> brokenPolicies, Doctor doctor) {
        super(String.format("l'utente %s %s non rispetta le policies %s " +
				" per il turno %s in %s. La violazione riguarda il giorno %s", 
				doctor.getName(), doctor.getLastname(), printBrokenPolicies(brokenPolicies), assegnazione.getShift().getTipologiaTurno(),
				assegnazione.getShift().getServizio().getNome(), ConvertitoreData.daStandardVersoTestuale(assegnazione.getData().toString())));
	}

	private static String printBrokenPolicies(List<ConditionPolicy> brokenPolicies) {
		StringBuilder sb = new StringBuilder();
		for(ConditionPolicy ucp: brokenPolicies){
			sb.append(ucp.getPermanentCondition().getType());
			sb.append(":");
			sb.append(ucp.getPolicy());
			sb.append(" ");
		}
		return sb.toString();
	}
}