package org.cswteams.ms3.exception;

import org.cswteams.ms3.control.utils.ConvertitoreData;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.doctor.Doctor;

public class ViolatedVincoloRuoloNumeroException extends ViolatedConstraintException{

    public ViolatedVincoloRuoloNumeroException(ConcreteShift assegnaione, Doctor doctor) {
        super(String.format("Il turno %s %s del giorno %s in %s non può essere allocato. Sono stati allocati troppi utenti con ruolo %s. Il problema riguarda l'utente %s %s",
        assegnaione.getShift().getMansione(), assegnaione.getShift().getTipologiaTurno(), ConvertitoreData.daStandardVersoTestuale(assegnaione.getData().toString()), assegnaione.getShift().getServizio().getNome(), doctor.getRole().toString(), doctor.getName(), doctor.getLastname()));
    }

    public ViolatedVincoloRuoloNumeroException(ConcreteShift assegnaione, RuoloNumero ruoloNumero, int numero) {
        super(String.format("Il turno %s del giorno %s in %s non può essere allocato. Sono stati allocati troppi/pochi utenti con ruolo %s. Come minimo devono essere associati %d utenti di quel ruolo, ma ne sono stati assegnati %d ",
                assegnaione.getShift().getTipologiaTurno(), ConvertitoreData.daStandardVersoTestuale(assegnaione.getData().toString()), assegnaione.getShift().getServizio().getNome(), ruoloNumero.getRuolo().toString(), ruoloNumero.getNumero(), numero));
    }


}
