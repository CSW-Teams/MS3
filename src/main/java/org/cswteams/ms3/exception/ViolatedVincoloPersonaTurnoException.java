package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Utente;

public class ViolatedVincoloPersonaTurnoException extends ViolatedConstraintException{


	public ViolatedVincoloPersonaTurnoException(AssegnazioneTurno assegnazione,CategoriaUtente categoriaUtente, Utente utente) {
        super(String.format("Categoria %s dell'utente %s %s non è ammissibile. Stai provando ad associare una categoria" +
				" che è proibita per il turno %s in %s. La violazione riguarda il giorno %s", categoriaUtente.getCategoria().getNome(),
				utente.getNome(), utente.getCognome(), assegnazione.getTurno().getTipologiaTurno(),
				assegnazione.getTurno().getServizio().getNome(), assegnazione.getData().toString()));
	}
}