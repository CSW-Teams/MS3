package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.dao.VincoloDao;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControllerVincolo implements IControllerVincolo {

    @Autowired
    VincoloDao vincoloDao;

    @Override
    public List<Vincolo> leggiVincoli() {
        return vincoloDao.findAll();
    }



}
