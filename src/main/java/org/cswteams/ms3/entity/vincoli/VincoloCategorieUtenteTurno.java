package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
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
        TipoCategoriaEnum tipoCategoria = ucp.getCategoria().getTipo();
        if((tipoCategoria == TipoCategoriaEnum.SPECIALIZZAZIONE && utente.getRuoloEnum() == RuoloEnum.SPECIALIZZANDO) || (tipoCategoria == TipoCategoriaEnum.TURNAZIONE && utente.getRuoloEnum() == RuoloEnum.STRUTTURATO)){
            return true;
        }

        Categoria categoria = ucp.getCategoria();
        List<CategoriaUtente> daControllare = new ArrayList<>();
        switch(tipoCategoria.toString()){
            case "STATO":
                daControllare = utente.getStato();
                break;
            case "SPECIALIZZAZIONE":
                daControllare = utente.getSpecializzazioni();
                break;
            case "TURNAZIONE":
                daControllare = utente.getTurnazioni();
                break;
        }
        
        for(CategoriaUtente categoriaUtente: daControllare){
            if(categoriaUtente.getCategoria().getNome().equals(categoria.getNome()) && categoriaUtente.isValid(dataTurno)){
                return ucp.getPolicy() != UserCategoryPolicyValue.EXCLUDE;

            }
        }

        return ucp.getPolicy() == UserCategoryPolicyValue.EXCLUDE;
    }
    
    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        Utente utente = contesto.getUserScheduleState().getUtente();
        AssegnazioneTurno aTurno = contesto.getAssegnazioneTurno();
        List<UserCategoryPolicy> brokenPolicies = new ArrayList<>();

        // confronta le policies con le categorie dell'utente
        for(UserCategoryPolicy ucp : aTurno.getTurno().getCategoryPolicies()){

            if(!checkPolicy(ucp, utente, aTurno.getData())){
                brokenPolicies.add(ucp);
            }
                
        }
        if(!brokenPolicies.isEmpty()){
            throw new ViolatedVincoloCategorieUtenteTurnoException(aTurno, brokenPolicies, utente);
        }

    }
}
