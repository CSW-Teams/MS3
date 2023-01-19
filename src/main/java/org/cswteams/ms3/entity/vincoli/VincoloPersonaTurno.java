package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.UserCategoryPolicy;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloPersonaTurnoException;

import javax.persistence.Entity;
import java.util.List;
import java.util.Objects;

/*
 * Vincolo che esclude utenti che rientrano nelle categorie vietate per quel turno
 */
@Entity
public class VincoloPersonaTurno extends Vincolo {

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUserScheduleState().getUtente();
        AssegnazioneTurno turno = contesto.getAssegnazioneTurno();
        List<CategoriaUtente> categorieUtente = utente.getStato();

        for(UserCategoryPolicy p : turno.getTurno().getCategoryPolicies()){
            for(CategoriaUtente categoriaUtente : categorieUtente){
                if(Objects.equals(p.getCategoria().getId(), categoriaUtente.getCategoria().getId())){
                    if( (categoriaUtente.getInizioValidità().isBefore(turno.getData()) || categoriaUtente.getInizioValidità().isEqual(turno.getData())) && (categoriaUtente.getFineValidità().isAfter(turno.getData()) || categoriaUtente.getFineValidità().isEqual(turno.getData()) && p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE) )){
                        throw new ViolatedVincoloPersonaTurnoException(contesto.getAssegnazioneTurno(),categoriaUtente, utente);
                    }
                }
            }
        }

    }
}
