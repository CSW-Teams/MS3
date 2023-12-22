package org.cswteams.ms3.control.desiderata;

import org.cswteams.ms3.control.utils.MappaDesiderata;
import org.cswteams.ms3.dao.DesiderataDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControllerDesiderata implements IControllerDesiderata{

    @Autowired
    DesiderataDAO desiderataDao;

    @Autowired
    DoctorDAO doctorDao;

    @Override
    public Preference aggiungiDesiderata(DesiderataDTO dto, long utenteId) throws DatabaseException {
        Doctor doctor = doctorDao.findById(utenteId);
        if (doctor == null){
            throw new DatabaseException("Utente non trovato");
        }

       /* Preference nuovaPreference = desiderataDao.save(MappaDesiderata.desiderataDtoToEntity(dto, Collections.singletonList(doctor)));
        doctor.getPreferenceList().add(nuovaPreference);
        doctorDao.save(doctor);
        return nuovaPreference;*/
        return null;
    }

    @Override
    public List<Preference> aggiungiDesiderate(List<DesiderataDTO> dtos, long utenteId) throws DatabaseException {
        Doctor doctor = doctorDao.findById(utenteId);
        if (doctor == null){
            throw new DatabaseException("Utente non trovato");
        }
/*
        List<Preference> nuoveDesiderata = desiderataDao.saveAll(MappaDesiderata.desiderataDtoToEntity(dtos, doctor));
        doctor.getPreferenceList().addAll(nuoveDesiderata);
        doctorDao.save(doctor);
        return nuoveDesiderata;*/
        return null;
    }

    @Override
    public void cancellaDesiderata(Long idDesiderata, long utenteId) throws DatabaseException {
        Doctor doctor = doctorDao.findById(utenteId);
        Preference preferenceDaEliminare = null;
        if(doctor == null){
            throw new DatabaseException("Utente non trovato");
        }

        List<Preference> preferenceList = doctor.getPreferenceList();
        for(Preference preference : preferenceList){
            if(preference.getId().equals(idDesiderata)){
                preferenceDaEliminare = preference;
                break;
            }
        }

        if(preferenceDaEliminare != null){
            doctor.getPreferenceList().remove(preferenceDaEliminare);
            desiderataDao.delete(preferenceDaEliminare);
            doctorDao.save(doctor);
        }


    }

    @Override
    public List<DesiderataDTO> getDesiderateDtoUtente(long utenteId) {
        return MappaDesiderata.desiderataToDto(getDesiderateUtente(utenteId));
    }

    @Override
    public List<Preference> getDesiderateUtente(long doctorID) {
        return desiderataDao.findAllByDoctorsId(doctorID);
    }
}
