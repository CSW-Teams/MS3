package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloPersonaTurnoException;

import javax.persistence.Entity;
import java.util.List;
import java.util.Objects;

@Entity
public class VincoloPersonaTurno extends Vincolo {

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUserScheduleState().getUtente();
        AssegnazioneTurno turno = contesto.getAssegnazioneTurno();
        List<CategoriaUtente> categorieUtente = utente.getStato();

        for(Categoria categoriaVietata : turno.getTurno().getCategorieVietate()){
            for(CategoriaUtente categoriaUtente : categorieUtente){
                if(Objects.equals(categoriaVietata.getId(), categoriaUtente.getCategoria().getId())){
                    if( (categoriaUtente.getInizioValidità().isBefore(turno.getData()) || categoriaUtente.getInizioValidità().isEqual(turno.getData())) && (categoriaUtente.getFineValidità().isAfter(turno.getData()) || categoriaUtente.getFineValidità().isEqual(turno.getData()) )){
                        throw new ViolatedVincoloPersonaTurnoException(contesto.getAssegnazioneTurno(),categoriaUtente, utente);
                    }
                }
            }
        }

    }
}
