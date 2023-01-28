package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dao.LiberatoriaDao;
import org.cswteams.ms3.dao.GiustificazioneFozaturaDao;
import org.cswteams.ms3.entity.Liberatoria;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
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

    @Override
    public void saveGiustificazione(GiustificazioneForzaturaVincoli giustificazioneForzaturaVincoli) {
        giustificazioneFozaturaDao.save(giustificazioneForzaturaVincoli);
    }


    @Override
    public Liberatoria saveDelibera(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Liberatoria liberatoria = new Liberatoria(fileName, file.getContentType(), file.getBytes());
        return liberatoriaDao.save(liberatoria);
    }

    @Override
    public Liberatoria getDelibera(String filename) {
        return liberatoriaDao.findDeliberaByName(filename);
    }


}
