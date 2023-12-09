package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
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
    private boolean checkPolicy(ConditionPolicy ucp, Doctor doctor, LocalDate dataTurno){
        /*
        // Se sto considerando lo specializzando non prendo in considerazione categorie di tipo specializzazione,
        // Se sto considerando lo strutturato, non prendo in considerazione categorie di tipo turnazione
        TipoCategoriaEnum tipoCategoria = ucp.getCategoria().getTipo();
        if((tipoCategoria == TipoCategoriaEnum.SPECIALIZZAZIONE && doctor.getRuoloEnum() == RuoloEnum.SPECIALIZZANDO) || (tipoCategoria == TipoCategoriaEnum.TURNAZIONE && doctor.getRuoloEnum() == RuoloEnum.STRUTTURATO)){
            return true;
        }

        Categoria categoria = ucp.getCategoria();
        List<CategoriaUtente> daControllare = new ArrayList<>();
        switch(tipoCategoria.toString()){
            case "STATO":
                daControllare = doctor.getStato();
                break;
            case "SPECIALIZZAZIONE":
                daControllare = doctor.getSpecializzazioni();
                break;
            case "TURNAZIONE":
                daControllare = doctor.getTurnazioni();
                break;
        }
        
        for(CategoriaUtente categoriaUtente: daControllare){
            if(categoriaUtente.getCategoria().getNome().equals(categoria.getNome()) && categoriaUtente.isValid(dataTurno)){
                return ucp.getPolicy() != UserCategoryPolicyValue.EXCLUDE;

            }
        }

        return ucp.getPolicy() == UserCategoryPolicyValue.EXCLUDE;*/
        return true;
    }
    
    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
        /*Doctor doctor = contesto.getUserScheduleState().getDoctor();
        AssegnazioneTurno aTurno = contesto.getAssegnazioneTurno();
        List<ConditionPolicy> brokenPolicies = new ArrayList<>();

        // confronta le policies con le categorie dell'utente
        for(ConditionPolicy ucp : aTurno.getTurno().getCategoryPolicies()){

            if(!checkPolicy(ucp, doctor, aTurno.getData())){
                brokenPolicies.add(ucp);
            }
                
        }
        if(!brokenPolicies.isEmpty()){
            throw new ViolatedVincoloCategorieUtenteTurnoException(aTurno, brokenPolicies, doctor);
        }
*/
    }
}
