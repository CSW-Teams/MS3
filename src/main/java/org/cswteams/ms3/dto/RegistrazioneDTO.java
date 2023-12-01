package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;

import java.time.LocalDate;

@Data
public class RegistrazioneDTO {

    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String codiceFiscale;
    private RuoloEnum ruolo;
    private String email;
    private String password;
    private AttoreEnum attore;

    public RegistrazioneDTO(){}

    public RegistrazioneDTO(String nome, String cognome, LocalDate dataNascita, String codiceFiscale, RuoloEnum ruolo, String email, String password, AttoreEnum attore) {
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
