package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@Service
public class ControllerAssegnazioniTurni implements IControllerAssegnazioneTurni{
    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private  TurnoDao turnoDao;

    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniAssegnati() throws ParseException {
        List<AssegnazioneTurno> turni = assegnazioneTurnoDao.findAll();
        return MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turni);
    }

    @Override
    public AssegnazioneTurno creaTurnoAssegnato(@NotNull RegistraAssegnazioneTurnoDTO dto) throws AssegnazioneTurnoException {

        Turno turno = turnoDao.findAllByServizioNomeAndTipologiaTurno(dto.getServizio().getNome(), dto.getTipologiaTurno());
        if(turno == null)
            throw new AssegnazioneTurnoException("Non esiste un turno con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia turno: "+dto.getTipologiaTurno().toString());

        AssegnazioneTurno assegnazioneTurno= new AssegnazioneTurno(LocalDate.of(dto.getAnno(),dto.getMese(),dto.getGiorno()),turno, MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili()),MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia()));
        System.out.println(dto.getAnno());
        System.out.println(dto.getMese());
        System.out.println(dto.getGiorno());
        System.out.println(assegnazioneTurno.getData());

        if(!checkAssegnazioneTurno(assegnazioneTurno)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
        }
        return assegnazioneTurnoDao.save(assegnazioneTurno);
    }


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<AssegnazioneTurnoDTO> turni = MappaAssegnazioneTurni.assegnazioneTurnoToDTO(assegnazioneTurnoDao.findTurniUtente(idPersona));
        System.out.println(turni);
        return turni;
    }

    private boolean checkAssegnazioneTurno(AssegnazioneTurno turno) {

        for(Utente utente1: turno.getUtentiDiGuardia()){
            for(Utente utente2: turno.getUtentiReperibili()){
                if (utente1.getId().longValue() == utente2.getId().longValue()){
                    return false;
                }
            }
        }
        return true;
    }

}
