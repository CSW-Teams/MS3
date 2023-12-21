package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaUtenti {

    public static Doctor utenteDTOtoEntity(DoctorDTO doctorDTO) {
        //return new Doctor(doctorDTO.getId(), doctorDTO.getNome(), doctorDTO.getCognome(), doctorDTO.getCodiceFiscale(), doctorDTO.getDataNascita(), doctorDTO.getEmail(), doctorDTO.getPassword(), doctorDTO.getSeniority(), doctorDTO.getPermanentConditions(), doctorDTO.getAttore());
        return null;
    }

    public static Set<Doctor> utenteDTOtoEntity(Set<DoctorDTO> utentiDto) {
        Set<Doctor> utenti = new HashSet<>();
        for (DoctorDTO dto: utentiDto){
            utenti.add(utenteDTOtoEntity(dto));
        }
        return utenti;
    }


    public static DoctorDTO utenteEntityToDTO(Doctor doctor) {
        //return new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getBirthday(), doctor.getTaxCode(), doctor.getRole(), doctor.getEmail(), doctor.getPassword(), doctor.getPermanentConditions(), doctor.getSpecializations(), doctor.getAttore());
        return null;
    }

    public static Set<DoctorDTO> utentiEntityToDTO(Set<Doctor> utenti){
        Set<DoctorDTO> doctorDTOS = new HashSet<>();
        for (Doctor entity: utenti){
            doctorDTOS.add(utenteEntityToDTO(entity));
        }
        return doctorDTOS;
    }

    /* todo: dà errore, non lo correggo perché non credo serva più
    public static Set<DoctorDTO> utentiEntityToDTO(List<User> users){
        Set<DoctorDTO> doctorDTOS = new HashSet<>();
        for (User user: users){
            doctorDTOS.add(utenteEntityToDTO(user));
        }
        return doctorDTOS;
    }

     */

}
