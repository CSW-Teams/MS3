package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.doctor.Doctor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaUtenti {

    public static Doctor utenteDTOtoEntity(UtenteDTO utenteDTO) {
        return new Doctor(utenteDTO.getId(),utenteDTO.getNome(),utenteDTO.getCognome(),utenteDTO.getCodiceFiscale(),utenteDTO.getDataNascita(),utenteDTO.getEmail(), utenteDTO.getPassword(), utenteDTO.getRuoloEnum(),utenteDTO.getCategorie(),utenteDTO.getAttore());
    }

    public static Set<Doctor> utenteDTOtoEntity(Set<UtenteDTO> utentiDto) {
        Set<Doctor> utenti = new HashSet<>();
        for (UtenteDTO dto: utentiDto){
            utenti.add(utenteDTOtoEntity(dto));
        }
        return utenti;
    }


    public static UtenteDTO utenteEntitytoDTO(Doctor doctor) {
        return new UtenteDTO(doctor.getId(), doctor.getNome(), doctor.getCognome(), doctor.getDataNascita(), doctor.getPhiscalCode(), doctor.getRuoloEnum(), doctor.getEmail(), doctor.getPassword(), doctor.getStato(), doctor.getSpecializzazioni(), doctor.getAttore());
    }

    public static Set<UtenteDTO> utentiEntitytoDTO(Set<Doctor> utenti){
        Set<UtenteDTO> utenteDTOS = new HashSet<>();
        for (Doctor entity: utenti){
            utenteDTOS.add(utenteEntitytoDTO(entity));
        }
        return utenteDTOS;
    }

    public static Set<UtenteDTO> utentiEntitytoDTO(List<Doctor> utenti){
        Set<UtenteDTO> utenteDTOS = new HashSet<>();
        for (Doctor entity: utenti){
            utenteDTOS.add(utenteEntitytoDTO(entity));
        }
        return utenteDTOS;
    }

}
