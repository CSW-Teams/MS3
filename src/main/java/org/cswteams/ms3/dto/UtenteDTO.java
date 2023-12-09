package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;

import java.time.LocalDate;
import java.util.List;

@Data
public class UtenteDTO {

    private Long id;

    private String nome;

    private String cognome;

    private LocalDate dataNascita;

    private String codiceFiscale;

    private RuoloEnum ruoloEnum;

    private String email;

    private String password;

    private List<CategoriaUtente> categorie;

    private List<CategoriaUtente> specializzazioni;

    private AttoreEnum attore;

    public UtenteDTO(){}

    public UtenteDTO(Long id, String nome, String cognome,
                     LocalDate dataNascita, String codiceFiscale,
                     RuoloEnum ruoloEnum, String email, String password,
                     List<CategoriaUtente> categorie, List<CategoriaUtente> specializzazioni, AttoreEnum attore) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.ruoloEnum = ruoloEnum;
        this.email = email;
        this.password = password;
        this.categorie = categorie;
        this.specializzazioni=specializzazioni;
        this.attore=attore;
    }

}
