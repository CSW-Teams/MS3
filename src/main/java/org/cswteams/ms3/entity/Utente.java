package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.RuoloEnum;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToMany(fetch = FetchType.EAGER)
    private List<CategoriaUtente> categorie ;

    /** Massimo monte ore pianficabile in una settimana per questo utente */
    private int maxWeekSchedulableHours;

    protected Utente() {

    }

    public Utente(String nome, String cognome, String codiceFiscale, LocalDate dataNascita, String email, RuoloEnum ruoloEnum) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.email = email;
        this.ruoloEnum = ruoloEnum;
        this.maxWeekSchedulableHours = -1;
        this.categorie = new ArrayList<>();
    }

    public Utente(Long id,String nome, String cognome, String codiceFiscale, LocalDate dataNascita, String email, RuoloEnum ruoloEnum) {
        this(nome, cognome, codiceFiscale, dataNascita, email, ruoloEnum);
        this.id = id;
    }

    public Utente(Long id, String nome, String cognome, LocalDate dataNascita, String codiceFiscale,
            RuoloEnum ruoloEnum, String email, int maxWeekSchedulableHours) {
        this(id,nome, cognome, codiceFiscale, dataNascita, email, ruoloEnum);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
    }

     public Utente(Long id, String nome, String cognome, LocalDate dataNascita, String codiceFiscale,
            RuoloEnum ruoloEnum, String email, int maxWeekSchedulableHours, List<CategoriaUtente> categorie) {
        this(id,nome, cognome, codiceFiscale, dataNascita, email, ruoloEnum);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
        this.categorie=categorie;
    }



    
    

}
