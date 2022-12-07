package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ControllerUtente implements IControllerUtente {

    @Autowired
    private UtenteDao utenteDao;

    @Override
    public List<Utente> leggiUtenti() {
        return utenteDao.findAll();
    }

    @Override
    public Object creaUtente(Utente s) {
        return utenteDao.save(s);
    }


}
