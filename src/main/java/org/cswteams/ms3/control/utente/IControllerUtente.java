package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.dto.DoctorDTO;

import java.util.Set;


public interface IControllerUtente {

    Set<DoctorDTO> leggiUtenti();

    Object creaUtente(DoctorDTO c);

    DoctorDTO leggiUtente(long idUtente);

}
