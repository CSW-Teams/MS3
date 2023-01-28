package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloCategorieUtenteTurnoException;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private boolean checkPolicy(UserCategoryPolicy ucp, Utente utente, LocalDate dataTurno){
        // Se sto considerando lo specializzando non prendo in considerazione categorie di tipo specializzazione,
        // Se sto considerando lo strutturato, non prendo in considerazione categorie di tipo turnazione
        int tipoCategoria = ucp.getCategoria().getTipo();
        if((tipoCategoria == -1 && utente.getRuoloEnum() == RuoloEnum.SPECIALIZZANDO) || (tipoCategoria == -2 && utente.getRuoloEnum() == RuoloEnum.STRUTTURATO)){
            return true;
        }

        Categoria categoria = ucp.getCategoria();
        List<CategoriaUtente> daControllare = new ArrayList<>();
        switch(tipoCategoria){
            case 0:
                daControllare = utente.getStato();
                break;
            case -1:
                daControllare = utente.getSpecializzazioni();
                break;
            case -2:
                daControllare = utente.getTurnazioni();
                break;
        }

        for(CategoriaUtente categoriaUtente: daControllare){
            if(categoriaUtente.getCategoria().getNome() == categoria.getNome()){
                if ((categoriaUtente.getInizioValidità().isBefore(dataTurno) || categoriaUtente.getInizioValidità().isEqual(dataTurno)) && (categoriaUtente.getFineValidità().isAfter(dataTurno) || categoriaUtente.getFineValidità().isEqual(dataTurno))) {
                    return ucp.getPolicy() != UserCategoryPolicyValue.EXCLUDE;
                }

            }
        }

        return ucp.getPolicy() == UserCategoryPolicyValue.EXCLUDE;
    }
    
    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUserScheduleState().getUtente();
        AssegnazioneTurno aTurno = contesto.getAssegnazioneTurno();

        // confronta le policies con le categorie dell'utente
        for(UserCategoryPolicy ucp : aTurno.getTurno().getCategoryPolicies()){

            if(!checkPolicy(ucp, utente, aTurno.getData())){
                throw new ViolatedVincoloCategorieUtenteTurnoException(aTurno, ucp, utente);
            }
                
        }

    }
}
