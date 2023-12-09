package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.doctor.Doctor;
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
        List<Doctor> utentiList = utenteDao.findAll();
        return MappaUtenti.utentiEntitytoDTO(utentiList);
    }

    @Override
    public Object creaUtente(UtenteDTO s) {
        return utenteDao.save(MappaUtenti.utenteDTOtoEntity(s));
    }

    @Override
    public UtenteDTO leggiUtente(long idUtente) {
        Doctor doctor = utenteDao.findById(idUtente);
        return MappaUtenti.utenteEntitytoDTO(doctor);
    }

}
