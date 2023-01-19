package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.CategoriaUtente;
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

    private List<CategoriaUtente> categorie;

    private List<CategoriaUtente> specializzazioni;

    public UtenteDTO(){}

    public UtenteDTO(Long id, String nome, String cognome, LocalDate dataNascita, String codiceFiscale, RuoloEnum ruoloEnum, String email, List<CategoriaUtente> categorie, List<CategoriaUtente> specializzazioni) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.ruoloEnum = ruoloEnum;
        this.email = email;
        this.categorie = categorie;
        this.specializzazioni=specializzazioni;
    }

}
