package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.entity.doctor.Doctor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaUtenti {

    public static Doctor utenteDTOtoEntity(DoctorDTO doctorDTO) {
        return new Doctor(doctorDTO.getId(), doctorDTO.getNome(), doctorDTO.getCognome(), doctorDTO.getCodiceFiscale(), doctorDTO.getDataNascita(), doctorDTO.getEmail(), doctorDTO.getPassword(), doctorDTO.getRuoloEnum(), doctorDTO.getPermanentConditions(), doctorDTO.getAttore());
    }

    public static Set<Doctor> utenteDTOtoEntity(Set<DoctorDTO> utentiDto) {
        Set<Doctor> utenti = new HashSet<>();
        for (DoctorDTO dto: utentiDto){
            utenti.add(utenteDTOtoEntity(dto));
        }
        return utenti;
    }


    public static DoctorDTO utenteEntityToDTO(Doctor doctor) {
        return new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getBirthDate(), doctor.getTaxCode(), doctor.getRuoloEnum(), doctor.getEmail(), doctor.getPassword(), doctor.getPermanentConditions(), doctor.getSpecializations(), doctor.getAttore());
    }

    public static Set<DoctorDTO> utentiEntityToDTO(Set<Doctor> utenti){
        Set<DoctorDTO> doctorDTOS = new HashSet<>();
        for (Doctor entity: utenti){
            doctorDTOS.add(utenteEntityToDTO(entity));
        }
        return doctorDTOS;
    }

    public static Set<DoctorDTO> utentiEntityToDTO(List<Doctor> utenti){
        Set<DoctorDTO> doctorDTOS = new HashSet<>();
        for (Doctor entity: utenti){
            doctorDTOS.add(utenteEntityToDTO(entity));
        }
        return doctorDTOS;
    }

}
