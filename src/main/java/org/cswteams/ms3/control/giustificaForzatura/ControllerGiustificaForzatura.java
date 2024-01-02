package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.GiustificazioneFozaturaDAO;
import org.cswteams.ms3.dao.LiberatoriaDAO;
import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Waiver;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ControllerGiustificaForzatura implements IControllerGiustificaForzatura {

    @Autowired
    GiustificazioneFozaturaDAO giustificazioneFozaturaDao;

    @Autowired
    LiberatoriaDAO liberatoriaDao;

    @Autowired
    DoctorDAO doctorDAO;

    @Override
    public void saveGiustificazione(GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoliDTO) throws DatabaseException {
        Doctor giustificatore = doctorDAO.findById(Long.parseLong(giustificazioneForzaturaVincoliDTO.getUtenteGiustificatoreId()));
        GiustificazioneForzaturaVincoli giustificazioneForzaturaVincoli =
                new GiustificazioneForzaturaVincoli(giustificazioneForzaturaVincoliDTO.getMessage(),
                        giustificazioneForzaturaVincoliDTO.getTimeSlot(),
                        buildServiceDTO(giustificazioneForzaturaVincoliDTO.getServizio()),
                        LocalDate.of(
                                giustificazioneForzaturaVincoliDTO.getGiorno(),
                                giustificazioneForzaturaVincoliDTO.getMese(),
                                giustificazioneForzaturaVincoliDTO.getAnno()),
                        convertDTOSetToEntitySet(giustificazioneForzaturaVincoliDTO.getUtentiAllocati()),
                        giustificatore);
        giustificazioneFozaturaDao.save(giustificazioneForzaturaVincoli);
    }

    @Override
    public Waiver saveDelibera(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Waiver liberatoria = new Waiver(fileName, file.getContentType(), file.getBytes());
        return liberatoriaDao.save(liberatoria);
    }

    @Override
    public Waiver getDelibera(String filename) {
        return liberatoriaDao.findDeliberaByName(filename);
    }

    public static MedicalService buildServiceDTO(MedicalServiceDTO dto) {
        return new MedicalService(dto.getMansioni(), dto.getNome());
    }

    private Doctor _getDoctor(Long doctorId) throws DatabaseException {
        Optional<Doctor> doctor = doctorDAO.findById(doctorId);
        if (doctor.isEmpty()) {
            throw new DatabaseException("Doctor not found for id = " + doctorId);
        }
        return doctor.get();
    }

    public Set<Doctor> convertDTOSetToEntitySet(Set<UserDTO> dtoSet) throws DatabaseException {
        Set<Doctor> entitySet = new HashSet<>();
        for (UserDTO dto : dtoSet) {

            /*
             Get the Doctor entities directly from the db, instead of by using the
             (pontentially) fewer/incomplete DTO parameters.
             Moreover, DTO does not include the last parameter "roles" of the entity
             so it is not possible to build a Doctor entity
            */
            entitySet.add(_getDoctor(dto.getId()));

            // old code temporarily below, for reference
            /*
            entitySet.add(new Doctor(
                    dto.getName(),
                    dto.getLastname(),
                    dto.getCodiceFiscale(),
                    dto.getDataNascita(),
                    dto.getEmail(),
                    dto.getPassword(),
                    dto.getSeniority(),
                    dto.getActor() // <-- entity/DTO mismatch!
                    )
            );
            */
        }
        return entitySet;
    }
}
