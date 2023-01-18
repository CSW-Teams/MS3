package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.LocalTime;
import java.util.Set;

@Data
public class TurnoDTO {

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private LocalTime oraFine;

    private boolean giornoSuccessivo;

    private ServizioDTO servizio;

    private Set<Categoria> categorieVietate;

    private long id;

    public TurnoDTO(){}

    public TurnoDTO(long id,TipologiaTurno tipologiaTurno, LocalTime inizio, LocalTime fine, ServizioDTO servizio, Set<Categoria> categorieVietate, boolean giornoSuccessivo){
        this.oraFine = fine;
        this.oraInizio = inizio;
        this.giornoSuccessivo = giornoSuccessivo;
        this.servizio = servizio;
        this.tipologiaTurno = tipologiaTurno;
        this.id = id;
        this.categorieVietate = categorieVietate;
    }

    public TipologiaTurno getTipologiaTurno() {
        return tipologiaTurno;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public ServizioDTO getServizio() {
        return servizio;
    }

    public Set<Categoria> getCategorieVietate(){
        return this.categorieVietate;
    }
}
