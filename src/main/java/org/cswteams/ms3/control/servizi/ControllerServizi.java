package org.cswteams.ms3.control.servizi;

import org.cswteams.ms3.control.utils.MappaServizio;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.enums.MansioneEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
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
    public ServizioDTO leggiServizioByNome(@NotNull String nome) {
        return MappaServizio.servizioEntitytoDTO(servizioDao.findByNome(nome));
    }

    @Override
    public Servizio creaServizio(@NotNull ServizioDTO servizio) {
        return servizioDao.save(MappaServizio.servizioDTOtoEntity(servizio));
    }
}
