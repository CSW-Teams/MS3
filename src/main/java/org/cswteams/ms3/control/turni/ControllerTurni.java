package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.exception.TurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class ControllerTurni implements IControllerTurni {

    @Autowired
    private AssegnazioneTurnoDao turnoDao;


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurni() {

        List<AssegnazioneTurno> turni = turnoDao.findAll();
        return MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turni);
    }

    @Override
    public Object creaTurno(@NotNull AssegnazioneTurnoDTO c) {
        try {
            AssegnazioneTurno turno = MappaAssegnazioneTurni.assegnazioneTurnoDTOToEntity(c);
            return turnoDao.save(turno);
        } catch (TurnoException e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) {
        return MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turnoDao.findTurniUtente(idPersona));
    }


}
