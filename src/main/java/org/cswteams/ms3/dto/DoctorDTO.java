package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.category.Condition;
import org.cswteams.ms3.entity.category.PermanentCondition;
import org.cswteams.ms3.entity.category.Specialization;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;

import java.time.LocalDate;
import java.util.List;

@Data
public class DoctorDTO {

    private Long id;

    private String nome;

    private String cognome;

    private LocalDate dataNascita;

    private String codiceFiscale;

    private RuoloEnum ruoloEnum;

    private String email;

    private String password;

    private List<PermanentCondition> permanentConditions;

    private List<Specialization> specializations;

    private AttoreEnum attore;

    public DoctorDTO(){}

    public DoctorDTO(Long id, String nome, String cognome,
                     LocalDate dataNascita, String codiceFiscale,
                     RuoloEnum ruoloEnum, String email, String password,
                     List<PermanentCondition> permanentConditions, List<Specialization> specializations, AttoreEnum attore) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.ruoloEnum = ruoloEnum;
        this.email = email;
        this.password = password;
        this.permanentConditions = permanentConditions;
        this.specializations=specializations;
        this.attore=attore;
    }

}
