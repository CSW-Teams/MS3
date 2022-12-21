package org.cswteams.ms3.control.servizi;

import org.cswteams.ms3.control.utils.MappaServizio;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.Servizio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ControllerServizi implements IControllerServizi{

    @Autowired
    ServizioDao servizioDao;

    @Override
    public Set<ServizioDTO> leggiServizi() {
        return MappaServizio.servizioEntitytoDTO(servizioDao.findAll());
    }

    @Override
    public Servizio creaServizio(ServizioDTO servizio) {
        return servizioDao.save(MappaServizio.servizioDTOtoEntity(servizio));
    }
}
