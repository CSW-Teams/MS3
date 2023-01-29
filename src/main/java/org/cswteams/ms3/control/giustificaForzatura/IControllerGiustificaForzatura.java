package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.cswteams.ms3.entity.Liberatoria;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface IControllerGiustificaForzatura {

    void saveGiustificazione(GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoli);

    Liberatoria saveDelibera(MultipartFile file) throws IOException;

    Liberatoria getDelibera(String filename);



}
