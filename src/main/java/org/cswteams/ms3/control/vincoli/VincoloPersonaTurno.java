package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;

import java.util.List;

public class VincoloPersonaTurno implements Vincolo {

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUtente();
        AssegnazioneTurno turno = contesto.getTurno();
        List<CategoriaUtente> categorieUtente = utente.getCategorie();

        for(CategoriaUtentiEnum categoriaVietata : turno.getTurno().getCategorieVietate()){
            for(CategoriaUtente categoriaUtente : categorieUtente){
                if(categoriaVietata.compareTo(categoriaUtente.getCategoria()) == 0){
                    if( (categoriaUtente.getInizioValidità().isBefore(turno.getData()) || categoriaUtente.getInizioValidità().isEqual(turno.getData())) && (categoriaUtente.getFineValidità().isAfter(turno.getData()) || categoriaUtente.getFineValidità().isEqual(turno.getData()) )){
                        throw new ViolatedVincoloPersonaTurnoException(categoriaUtente, utente.getId());
                    }
                }
            }
        }
        return ;
    }
}
