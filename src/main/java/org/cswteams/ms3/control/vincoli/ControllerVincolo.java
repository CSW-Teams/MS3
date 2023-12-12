package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.dao.ConfigVincoliDao;
import org.cswteams.ms3.dao.ConfigVincoloMaxPeriodoConsecutivoDao;
import org.cswteams.ms3.dao.VincoloDao;
import org.cswteams.ms3.entity.vincoli.*;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ControllerVincolo implements IControllerVincolo {

    @Autowired
    VincoloDao vincoloDao;

    @Autowired
    ConfigVincoliDao configVincoliDao;

    @Autowired
    ConfigVincoloMaxPeriodoConsecutivoDao configVincoloMaxPeriodoConsecutivoDao;

    @Override
    public List<Vincolo> leggiVincoli() {
        return vincoloDao.findAll();
    }

    @Override
    public ConfigVincoli aggiornaVincoli(ConfigVincoli configurazione) {
        for(ConfigVincoloMaxPeriodoConsecutivo config: configurazione.getConfigVincoloMaxPeriodoConsecutivoPerCategoria()){
            ConfigVincoloMaxPeriodoConsecutivo configVincoloMaxPeriodoConsecutivo = configVincoloMaxPeriodoConsecutivoDao.findAllByCategoriaVincolataType(config.getCategoriaVincolata().getType()).get(0);
            config.setId(configVincoloMaxPeriodoConsecutivo.getId());
            configVincoloMaxPeriodoConsecutivoDao.save(config);
        }
        //Aggiorno configurazione
        ConfigVincoli configVincoli = configVincoliDao.findAll().get(0);
        configurazione.setId(configVincoli.getId());
        configVincoliDao.save(configurazione);
        //Aggiorno i vincoli
        VincoloTipologieTurniContigue vincoloTipologieTurniContigue = (VincoloTipologieTurniContigue) vincoloDao.findByType("VincoloTipologieTurniContigue").get(0);
        vincoloTipologieTurniContigue.setHorizon(configurazione.getHorizonTurnoNotturno());

        VincoloMaxOrePeriodo vincoloMaxOrePeriodo = (VincoloMaxOrePeriodo) vincoloDao.findByType("VincoloMaxOrePeriodo").get(0);
        vincoloMaxOrePeriodo.setNumGiorniPeriodo(configurazione.getNumGiorniPeriodo());
        vincoloMaxOrePeriodo.setNumMinutiMaxPeriodo(configurazione.getMaxMinutiPeriodo());

        List<Vincolo> vincoliMaxPeriodoConsecutivo = vincoloDao.findByType("VincoloMaxPeriodoConsecutivo");
        for(Vincolo vincolo: vincoliMaxPeriodoConsecutivo){
            VincoloMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivo = (VincoloMaxPeriodoConsecutivo)vincolo;
            if(vincoloMaxPeriodoConsecutivo.getCategoriaVincolata() == null){
                vincoloMaxPeriodoConsecutivo.setMaxConsecutiveMinutes(configurazione.getNumMaxMinutiConsecutiviPerTutti());
            }else{
                for(ConfigVincoloMaxPeriodoConsecutivo config: configurazione.getConfigVincoloMaxPeriodoConsecutivoPerCategoria()){
                    if(vincoloMaxPeriodoConsecutivo.getCategoriaVincolata().getType().equals(config.getCategoriaVincolata().getType())){
                        vincoloMaxPeriodoConsecutivo.setMaxConsecutiveMinutes(config.getNumMaxMinutiConsecutivi());
                    }
                }
            }
        }

        vincoloDao.saveAndFlush(vincoloTipologieTurniContigue);
        vincoloDao.saveAll(vincoliMaxPeriodoConsecutivo);
        vincoloDao.saveAndFlush(vincoloMaxOrePeriodo);

        return configurazione;
     }

    @Override
    public ConfigVincoli leggiConfigurazioneVincoli() {
        return configVincoliDao.findAll().get(0);
    }


}
