package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


public interface IControllerGiustificaForzatura {

    public void init();

    public void saveGiustificazione(GiustificazioneForzaturaVincoli giustificazioneForzaturaVincoli);

    public void save(MultipartFile file);

    public Resource load(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();


}
