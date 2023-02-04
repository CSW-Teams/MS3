package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
public class TurnoDTO {

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private LocalTime oraFine;

    private boolean giornoSuccessivo;

    private ServizioDTO servizio;

    private MansioneEnum mansione;

    private Set<Categoria> categorieVietate;

    private long id;

    private boolean reperibilitaAttiva;

    //private int numUtentiGuardia;

    //private int numUtentiReperibilita;

    private List<RuoloNumero> ruoliNumero;

    public TurnoDTO(){}

    public TurnoDTO(long id,TipologiaTurno tipologiaTurno, LocalTime inizio, LocalTime fine, ServizioDTO servizio, MansioneEnum mansione, boolean giornoSuccessivo, boolean reperibilitaAttiva, List<RuoloNumero> ruoliNumero){
        this.oraFine = fine;
        this.oraInizio = inizio;
        this.giornoSuccessivo = giornoSuccessivo;
        this.servizio = servizio;
        this.mansione = mansione;
        this.tipologiaTurno = tipologiaTurno;
        this.id = id;
        this.reperibilitaAttiva = reperibilitaAttiva;
        this.ruoliNumero = ruoliNumero;
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
