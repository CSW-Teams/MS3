package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.control.utils.MappaServizio;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.GiustificazioneFozaturaDao;
import org.cswteams.ms3.dao.LiberatoriaDao;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Waiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ControllerGiustificaForzatura implements IControllerGiustificaForzatura {

    @Autowired
    GiustificazioneFozaturaDao giustificazioneFozaturaDao;

    @Autowired
    LiberatoriaDao liberatoriaDao;

    @Autowired
    DoctorDAO doctorDAO;

    @Override
    public void saveGiustificazione(GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoliDTO) {
        //giustificazioneFozaturaDao.save(giustificazioneForzaturaVincoli);
        Doctor giustificatore = doctorDAO.findById(Long.parseLong(giustificazioneForzaturaVincoliDTO.getUtenteGiustificatoreId()));
        //GiustificazioneForzaturaVincoli giustificazioneForzaturaVincoli = new GiustificazioneForzaturaVincoli(giustificazioneForzaturaVincoliDTO.getMessage(),giustificazioneForzaturaVincoliDTO.getTipologiaTurno(), MappaServizio.servizioDTOtoEntity(giustificazioneForzaturaVincoliDTO.getServizio()), giustificazioneForzaturaVincoliDTO.getGiorno(), MappaUtenti.utenteDTOtoEntity(giustificazioneForzaturaVincoliDTO.getUtentiAllocati()), giustificatore);

        //giustificazioneFozaturaDao.save(giustificazioneForzaturaVincoli);
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


}
