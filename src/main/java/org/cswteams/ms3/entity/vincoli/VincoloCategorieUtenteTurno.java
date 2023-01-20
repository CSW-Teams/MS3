package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.UserCategoryPolicy;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloCategorieUtenteTurnoException;

import javax.persistence.Entity;
import java.util.List;

/*
 * Vincolo che esclude utenti le cui categorie non rispettano le
 * policies di inclusione per una assegnazione turno
 */
@Entity
public class VincoloCategorieUtenteTurno extends Vincolo {

    /** Controlla se la policy è rispettata per le categorie in argomento,
     * 
     * @return @{code true} se la policy è rispettata, {@code false} altrimenti
     */
    private boolean checkPolicy(UserCategoryPolicy ucp, List<CategoriaUtente> categorieUtente){
        
        for(CategoriaUtente cu : categorieUtente){
            
            switch(ucp.getPolicy()){
                case INCLUDE:{
                    if(ucp.getCategoria().equals(cu.getCategoria()))
                        return true;
                    return false;
                }
                case EXCLUDE:{
                    if(ucp.getCategoria().equals(cu.getCategoria()))
                        return false;
                    return true;
                }
            }
        }
        return ucp.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE);      
    }
    
    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUserScheduleState().getUtente();
        AssegnazioneTurno aTurno = contesto.getAssegnazioneTurno();
        List<CategoriaUtente> categorieUtente = utente.getStato();

        // confronta le policies con le categorie dell'utente
        for(UserCategoryPolicy ucp : aTurno.getTurno().getCategoryPolicies()){

            if(!checkPolicy(ucp, categorieUtente)){
                throw new ViolatedVincoloCategorieUtenteTurnoException(aTurno, ucp, utente);
            }
                
        }

    }
}
