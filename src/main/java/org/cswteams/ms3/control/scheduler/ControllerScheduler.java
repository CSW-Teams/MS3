package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ControllerScheduler implements IControllerScheduler{

    @Autowired
    ServizioDao servizioDao;

    @Autowired
    UtenteDao utendeDao;

    @Autowired
    TurnoDao turnoDao;

    

    
}
