package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.control.utils.MappaTurni;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dto.RotationDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.exception.TurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ControllerTurni implements IControllerTurni {

    @Autowired
    TurnoDao turnoDao;

    @Override
    public Set<RotationDTO> leggiTurniDiServizio(String servizio) {
        return MappaTurni.turnoEntityToDTO(turnoDao.findAllByServizioNome(servizio));
    }

    @Override
    public Set<RotationDTO> leggiTurni() {
        return MappaTurni.turnoEntityToDTO(turnoDao.findAll());
    }

    @Override
    public Shift creaTurno(RotationDTO turno) throws TurnoException {
        Shift shiftEntity = MappaTurni.turnoDTOToEntity(turno);
        return turnoDao.save(shiftEntity);
    }

}
