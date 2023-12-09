package org.cswteams.ms3.control.desiderata;

import org.cswteams.ms3.control.utils.MappaDesiderata;
import org.cswteams.ms3.dao.DesiderataDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControllerDesiderata implements IControllerDesiderata{

    @Autowired
    DesiderataDao desiderataDao;

    @Autowired
    UtenteDao utenteDao;

    @Override
    public Desiderata aggiungiDesiderata(DesiderataDTO dto, long utenteId) throws DatabaseException {
        Doctor doctor = utenteDao.findById(utenteId);
        if (doctor == null){
            throw new DatabaseException("Utente non trovato");
        }

        Desiderata nuovaDesiderata = desiderataDao.save(MappaDesiderata.desiderataDtoToEntity(dto, doctor));
        doctor.getDesiderataList().add(nuovaDesiderata);
        utenteDao.save(doctor);
        return nuovaDesiderata;
    }

    @Override
    public List<Desiderata> aggiungiDesiderate(List<DesiderataDTO> dtos, long utenteId) throws DatabaseException {
        Doctor doctor = utenteDao.findById(utenteId);
        if (doctor == null){
            throw new DatabaseException("Utente non trovato");
        }

        List<Desiderata> nuoveDesiderata = desiderataDao.saveAll(MappaDesiderata.desiderataDtoToEntity(dtos, doctor));
        doctor.getDesiderataList().addAll(nuoveDesiderata);
        utenteDao.save(doctor);
        return nuoveDesiderata;
    }

    @Override
    public void cancellaDesiderata(Long idDesiderata, long utenteId) throws DatabaseException {
        Doctor doctor = utenteDao.findById(utenteId);
        Desiderata desiderataDaEliminare = null;
        if(doctor == null){
            throw new DatabaseException("Utente non trovato");
        }

        List<Desiderata> desiderataList = doctor.getDesiderataList();
        for(Desiderata desiderata : desiderataList){
            if(desiderata.getId().equals(idDesiderata)){
                desiderataDaEliminare = desiderata;
                break;
            }
        }

        if(desiderataDaEliminare!= null){
            doctor.getDesiderataList().remove(desiderataDaEliminare);
            desiderataDao.delete(desiderataDaEliminare);
            utenteDao.save(doctor);
        }


    }

    @Override
    public List<DesiderataDTO> getDesiderateDtoUtente(long utenteId) {
        return MappaDesiderata.desiderataToDto(getDesiderateUtente(utenteId));
    }

    @Override
    public List<Desiderata> getDesiderateUtente(long userID) {
        return desiderataDao.findAllByUserId(userID);
    }
}
