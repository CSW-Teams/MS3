package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.TurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
@Service
public class ControllerAssegnazioniTurni implements IControllerAssegnazioneTurni{
    @Autowired
    private AssegnazioneTurnoDao turnoDao;

    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniAssegnati() throws ParseException {
        List<AssegnazioneTurno> turni = turnoDao.findAll();
        return MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turni);
    }

    @Override
    public AssegnazioneTurno creaTurnoAssegnato(@NotNull AssegnazioneTurnoDTO c) throws AssegnazioneTurnoException {
        AssegnazioneTurno assegnazioneTurno = MappaAssegnazioneTurni.assegnazioneTurnoDTOToEntity(c);
        if(!checkAssegnazioneTurno(assegnazioneTurno)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
        }
        return turnoDao.save(assegnazioneTurno);
    }


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<AssegnazioneTurnoDTO> turni = MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turnoDao.findTurniUtente(idPersona));
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
