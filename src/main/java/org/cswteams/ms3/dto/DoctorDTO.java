package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.Seniority;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.enums.SystemActor;

import java.time.LocalDate;
import java.util.List;

@Data
public class DoctorDTO {

    private Long id;

    private String name;

    private String lastname;

    private LocalDate dataNascita;

    private String codiceFiscale;

    private Seniority seniority;

    private String email;

    private String password;

    private List<PermanentCondition> permanentConditions;

    private List<Specialization> specializations;

    private SystemActor attore;

    public DoctorDTO(){}

    public DoctorDTO(Long id, String name, String lastname,
                     LocalDate dataNascita, String codiceFiscale,
                     Seniority seniority, String email, String password,
                     List<PermanentCondition> permanentConditions, List<Specialization> specializations, SystemActor attore) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.seniority = seniority;
        this.email = email;
        this.password = password;
        this.permanentConditions = permanentConditions;
        this.specializations=specializations;
        this.attore=attore;
    }

    public DoctorDTO(Long id, String name, String lastname,Seniority seniority){
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.seniority = seniority;
    }

}
