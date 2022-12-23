package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.RuoloEnum;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cognome;

    private LocalDate dataNascita;

    private String codiceFiscale;

    private RuoloEnum ruoloEnum;

    private String email;


    protected Utente() {

    }

    public Utente(String nome, String cognome, String codiceFiscale, LocalDate dataNascita, String email, RuoloEnum ruoloEnum) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.email = email;
        this.ruoloEnum = ruoloEnum;
    }

    public Utente(Long id,String nome, String cognome, String codiceFiscale, LocalDate dataNascita, String email, RuoloEnum ruoloEnum) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.email = email;
        this.ruoloEnum = ruoloEnum;
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public RuoloEnum getRuoloEnum() {
        return ruoloEnum;
    }

    public String getEmail() {
        return email;
    }
}
