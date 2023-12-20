package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.Seniority;
import org.cswteams.ms3.enums.SystemActor;

import java.time.LocalDate;

@Data
public class RegistrazioneDTO {

    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String codiceFiscale;
    private Seniority ruolo;
    private String email;
    private String password;
    private SystemActor attore;

    public RegistrazioneDTO(){}

    public RegistrazioneDTO(String nome, String cognome, LocalDate dataNascita, String codiceFiscale, Seniority ruolo, String email, String password, SystemActor attore) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.attore = attore;

    }

}
