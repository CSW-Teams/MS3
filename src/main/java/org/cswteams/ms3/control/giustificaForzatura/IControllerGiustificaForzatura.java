package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.cswteams.ms3.entity.Waiver;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface IControllerGiustificaForzatura {

    void saveGiustificazione(GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoli);

    Waiver saveDelibera(MultipartFile file) throws IOException;

    Waiver getDelibera(String filename);



}
