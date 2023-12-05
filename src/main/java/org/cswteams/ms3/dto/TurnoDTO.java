package org.cswteams.ms3.dto;

import lombok.Data;
import lombok.Getter;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
public class TurnoDTO {

    @Getter
    private TipologiaTurno tipologiaTurno;

    @Getter
    private LocalTime oraInizio;

    private Duration durata;

    @Getter
    private ServizioDTO servizio;

    private MansioneEnum mansione;

    @Getter
    private Set<Categoria> categorieVietate;

    private long id;

    private boolean reperibilitaAttiva;

    private List<RuoloNumero> ruoliNumero;

    public TurnoDTO(){}

    public TurnoDTO(long id,TipologiaTurno tipologiaTurno, LocalTime inizio, Duration durata, ServizioDTO servizio, MansioneEnum mansione, boolean reperibilitaAttiva, List<RuoloNumero> ruoliNumero){
        this.durata = durata;
        this.oraInizio = inizio;
        this.servizio = servizio;
        this.mansione = mansione;
        this.tipologiaTurno = tipologiaTurno;
        this.id = id;
        this.reperibilitaAttiva = reperibilitaAttiva;
        this.ruoliNumero = ruoliNumero;
    }
}
