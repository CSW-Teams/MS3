package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    @NotNull
    private Long id;
    
    @NotNull
    private String nome;

    @NotNull
    private String cognome;

    @NotNull
    private LocalDate dataNascita;

    @NotNull
    private String codiceFiscale;

    @NotNull
    private RuoloEnum ruoloEnum;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private AttoreEnum attore;



    @OneToMany
    private List<Desiderata> desiderataList;

    @ManyToMany
    private List<CategoriaUtente> stato;

    @ManyToMany
    private  List<CategoriaUtente> specializzazioni;

    @ManyToMany
    private  List<CategoriaUtente> turnazioni;

    /** Massimo monte ore pianificabile in una settimana per questo utente */
    private int maxWeekSchedulableHours;

    protected Utente() {

    }

    public Utente(String nome, String cognome, String codiceFiscale,
                  LocalDate dataNascita, String email, String password,
                  RuoloEnum ruoloEnum, AttoreEnum attore) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.email = email;
        this.password = password;
        this.ruoloEnum = ruoloEnum;
        this.maxWeekSchedulableHours = -1;
        this.stato = new ArrayList<>();
        this.specializzazioni= new ArrayList<>();
        this.turnazioni = new ArrayList<>();
        this.desiderataList = new ArrayList<>();
        this.attore=attore;
    }

    public Utente(Long id,String nome, String cognome, String codiceFiscale,
                  LocalDate dataNascita, String email, String password,
                  RuoloEnum ruoloEnum, AttoreEnum attore) {

        this(nome, cognome, codiceFiscale, dataNascita, email, password, ruoloEnum, attore);
        this.id = id;
    }

    public Utente(Long id, String nome, String cognome, LocalDate dataNascita,
                  String codiceFiscale, RuoloEnum ruoloEnum, String email,
                  String password, int maxWeekSchedulableHours, AttoreEnum attore) {

        this(id,nome, cognome, codiceFiscale, dataNascita, email, password, ruoloEnum, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
    }

     public Utente(Long id, String nome, String cognome, LocalDate dataNascita,
                   String codiceFiscale, RuoloEnum ruoloEnum, String email,
                   String password, int maxWeekSchedulableHours, List<CategoriaUtente> stato, AttoreEnum attore) {

        this(id,nome, cognome, codiceFiscale, dataNascita, email, password, ruoloEnum, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
        this.stato = stato;
    }


    public Utente(Long id, String nome, String cognome, String codiceFiscale,
                  LocalDate dataNascita, String email, String password,
                  RuoloEnum ruoloEnum, List<CategoriaUtente> stato,AttoreEnum attore) {
        this(id,nome, cognome, codiceFiscale, dataNascita, email, password, ruoloEnum, attore);
        this.stato = stato;
    }

    public Utente(Long id, String nome, String cognome, String codiceFiscale, LocalDate dataNascita, String email, RuoloEnum ruoloEnum, List<CategoriaUtente> stato,
                String password, List<Desiderata> desiderataList,AttoreEnum attore) {
        this(id,nome, cognome, codiceFiscale, dataNascita, email, password, ruoloEnum, attore);
        this.stato = stato;
        this.desiderataList = desiderataList;
    }


}
