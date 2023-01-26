package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dao.GiustificazioneFozaturaDao;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
@Service
public class ControllerGiustificaForzatura implements IControllerGiustificaForzatura {

    @Autowired
    GiustificazioneFozaturaDao giustificazioneFozaturaDao;

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void saveGiustificazione(GiustificazioneForzaturaVincoli giustificazioneForzaturaVincoli) {

        //Prova a salvare tutti i file
      for (org.cswteams.ms3.entity.Files file : giustificazioneForzaturaVincoli.getDelibere()) {
          try {
              Files.copy(file.getMultipartFile().getInputStream(), this.root.resolve(Objects.requireNonNull(file.getMultipartFile().getOriginalFilename())));
          } catch (Exception e) {
              if (e instanceof FileAlreadyExistsException) {
                  throw new RuntimeException("A file of that name already exists.");
              }

              throw new RuntimeException(e.getMessage());
          }
      }
      giustificazioneFozaturaDao.save(giustificazioneForzaturaVincoli);




    }
}
