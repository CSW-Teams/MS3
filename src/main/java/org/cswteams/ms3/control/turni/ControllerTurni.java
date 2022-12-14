package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ControllerTurni implements IControllerTurni {

    @Autowired
    private AssegnazioneTurnoDao turnoDao;


    @Override
    public List<AssegnazioneTurno> leggiTurni() {
        return turnoDao.findAll();
    }

    @Override
    public Object creaTurno(@NotNull AssegnazioneTurno c) {
        return turnoDao.save(c);
    }


    @Override
    public List<AssegnazioneTurno> leggiTurniUtente(@NotNull Long idPersona) {
        return turnoDao.findTurniUtente(idPersona);
    }


}
