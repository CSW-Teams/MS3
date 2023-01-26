package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.ViolatedConstraintException;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class VincoloNumeroDiRuoloTurno extends Vincolo{

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {

        int utentiRuoloAssegnati =0;
        for(Utente utente: contesto.getAssegnazioneTurno().getUtentiDiGuardia()){
            if(utente.getRuoloEnum().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum()))
                utentiRuoloAssegnati++;
        }

        System.out.println(utentiRuoloAssegnati);

        for(RuoloNumero ruoloNumero :contesto.getAssegnazioneTurno().getTurno().getRuoliNumero()){
            if(ruoloNumero.getRuolo().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum())){
                if(utentiRuoloAssegnati > ruoloNumero.getNumero())
                    throw new ViolatedConstraintException();

            }
        }

        utentiRuoloAssegnati =0;
        for(Utente utente: contesto.getAssegnazioneTurno().getUtentiReperibili()){
            if(utente.getRuoloEnum().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum()))
                utentiRuoloAssegnati++;
        }

        for(RuoloNumero ruoloNumero :contesto.getAssegnazioneTurno().getTurno().getRuoliNumero()){
            if(ruoloNumero.getRuolo().equals(contesto.getUserScheduleState().getUtente().getRuoloEnum())){
                if(utentiRuoloAssegnati > ruoloNumero.getNumero())
                    throw new ViolatedConstraintException();

            }
        }

        System.out.println("reperibili"+utentiRuoloAssegnati);
        System.out.println(contesto.getAssegnazioneTurno().getTurno().getTipologiaTurno());



    }

}
