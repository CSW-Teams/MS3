package org.cswteams.ms3.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.enums.TipologiaTurno;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Desiderata {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate data;
    @Column
    @Enumerated
    @ElementCollection(targetClass = TipologiaTurno.class)

    private List<TipologiaTurno> tipologieTurnoCoinvolte;

    @ManyToOne
    private Utente utente;

    public Desiderata(LocalDate data, List<TipologiaTurno> tipologieTurnoCoinvolte, Utente utente){
        this.data = data;
        this.tipologieTurnoCoinvolte = tipologieTurnoCoinvolte;
        this.utente = utente;
    }

    public Desiderata(){

    }
}
