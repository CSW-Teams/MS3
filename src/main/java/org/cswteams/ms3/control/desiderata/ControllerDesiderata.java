package org.cswteams.ms3.control.desiderata;

import org.cswteams.ms3.control.utils.MappaDesiderata;
import org.cswteams.ms3.dao.DesiderataDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.Utente;
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
        Utente utente = utenteDao.findById(utenteId);
        if (utente == null){
            throw new DatabaseException("Utente non trovato");
        }

        Desiderata nuovaDesiderata = desiderataDao.save(MappaDesiderata.desiderataDtoToEntity(dto,utente));
        utente.getDesiderataList().add(nuovaDesiderata);
        utenteDao.save(utente);
        return nuovaDesiderata;
    }

    @Override
    public List<Desiderata> aggiungiDesiderate(List<DesiderataDTO> dtos, long utenteId) throws DatabaseException {
        Utente utente = utenteDao.findById(utenteId);
        if (utente == null){
            throw new DatabaseException("Utente non trovato");
        }

        List<Desiderata> nuoveDesiderata = desiderataDao.saveAll(MappaDesiderata.desiderataDtoToEntity(dtos,utente));
        utente.getDesiderataList().addAll(nuoveDesiderata);
        utenteDao.save(utente);
        return nuoveDesiderata;
    }

    @Override
    public void cancellaDesiderata(Long idDesiderata, long utenteId) throws DatabaseException {
        Utente utente = utenteDao.findById(utenteId);
        if(utente == null){
            throw new DatabaseException("Utente non trovato");
        }
        List<Desiderata> desiderataList = utente.getDesiderataList();
        for(int i = 0; i < desiderataList.size(); i++){
            if(desiderataList.get(i).getId().equals(idDesiderata)){
                desiderataDao.deleteById(idDesiderata);
                utente.getDesiderataList().remove(i);
            }
        }
        utenteDao.save(utente);

    }

    @Override
    public List<DesiderataDTO> getDesiderateDtoUtente(long utenteId) {
        return MappaDesiderata.desiderataToDto(getDesiderateUtente(utenteId));
    }

    @Override
    public List<Desiderata> getDesiderateUtente(long utenteId) {
        return desiderataDao.findAllByUtenteId(utenteId);
    }
}
