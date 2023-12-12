package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.DoctorDTO;
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
    public Set<DoctorDTO> leggiUtenti() {
        List<Doctor> utentiList = utenteDao.findAll();
        return MappaUtenti.utentiEntityToDTO(utentiList);
    }

    @Override
    public Object creaUtente(DoctorDTO s) {
        return utenteDao.save(MappaUtenti.utenteDTOtoEntity(s));
    }

    @Override
    public DoctorDTO leggiUtente(long idUtente) {
        Doctor doctor = utenteDao.findById(idUtente);
        return MappaUtenti.utenteEntityToDTO(doctor);
    }

}
