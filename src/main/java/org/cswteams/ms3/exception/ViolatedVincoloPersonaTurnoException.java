package org.cswteams.ms3.exception;

import org.cswteams.ms3.entity.CategoriaUtente;

public class ViolatedVincoloPersonaTurnoException extends ViolatedConstraintException{


	public ViolatedVincoloPersonaTurnoException(CategoriaUtente categoriaUtente, Long idUtente) {
        super(String.format("Categoria %s dell'utente con id %d non è ammissibile", categoriaUtente.getCategoria().toString(), idUtente));
	}
}