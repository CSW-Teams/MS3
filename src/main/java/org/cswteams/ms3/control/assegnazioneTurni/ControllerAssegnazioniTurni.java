package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
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
    public AssegnazioneTurno creaTurnoAssegnato(@NotNull AssegnazioneTurnoDTO c) {
        try {
            AssegnazioneTurno turno = MappaAssegnazioneTurni.assegnazioneTurnoDTOToEntity(c);
            return turnoDao.save(turno);
        } catch (TurnoException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<AssegnazioneTurnoDTO> turni = MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turnoDao.findTurniUtente(idPersona));
        System.out.println(turni);
        return turni;
    }

}
