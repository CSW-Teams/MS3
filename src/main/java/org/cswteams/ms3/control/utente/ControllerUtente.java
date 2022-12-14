package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class ControllerUtente implements IControllerUtente {

    @Autowired
    private UtenteDao utenteDao;

    @Override
    public Set<UtenteDTO> leggiUtenti() {
        List<Utente> utentiList = utenteDao.findAll();
        return MappaUtenti.utenteENTITYtoDTO(utentiList);
    }

    @Override
    public Object creaUtente(UtenteDTO s) {
        return utenteDao.save(MappaUtenti.utenteDTOtoENTITY(s));
    }

}
