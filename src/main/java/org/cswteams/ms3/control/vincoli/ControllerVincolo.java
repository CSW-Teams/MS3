package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.dao.ConfigVincoliDao;
import org.cswteams.ms3.dao.ConfigVincoloMaxPeriodoConsecutivoDao;
import org.cswteams.ms3.dao.VincoloDao;
import org.cswteams.ms3.entity.constraint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControllerVincolo implements IControllerVincolo {

    @Autowired
    VincoloDao vincoloDao;

    @Autowired
    ConfigVincoliDao configVincoliDao;

    @Autowired
    ConfigVincoloMaxPeriodoConsecutivoDao configVincoloMaxPeriodoConsecutivoDao;

    @Override
    public List<Constraint> leggiVincoli() {
        return vincoloDao.findAll();
    }

    @Override
    public ConfigVincoli aggiornaVincoli(ConfigVincoli configurazione) {
        for(ConfigVincMaxPerCons config: configurazione.getConfigVincMaxPerConsPerCategoria()){
            ConfigVincMaxPerCons configVincMaxPerCons = configVincoloMaxPeriodoConsecutivoDao.findAllByCategoriaVincolataType(config.getCategoriaVincolata().getType()).get(0);
            config.setId(configVincMaxPerCons.getId());
            configVincoloMaxPeriodoConsecutivoDao.save(config);
        }
        //Aggiorno configurazione
        ConfigVincoli configVincoli = configVincoliDao.findAll().get(0);
        configurazione.setId(configVincoli.getId());
        configVincoliDao.save(configurazione);
        //Aggiorno i vincoli
        ConstraintTipologieTurniContigue vincoloTipologieTurniContigue = (ConstraintTipologieTurniContigue) vincoloDao.findByType("ConstraintTipologieTurniContigue").get(0);
        vincoloTipologieTurniContigue.setHorizon(configurazione.getHorizonTurnoNotturno());

        ConstraintMaxOrePeriodo vincoloMaxOrePeriodo = (ConstraintMaxOrePeriodo) vincoloDao.findByType("ConstraintMaxOrePeriodo").get(0);
        vincoloMaxOrePeriodo.setNumGiorniPeriodo(configurazione.getNumGiorniPeriodo());
        vincoloMaxOrePeriodo.setNumMinutiMaxPeriodo(configurazione.getMaxMinutiPeriodo());

        List<Constraint> vincoliMaxPeriodoConsecutivo = vincoloDao.findByType("ConstraintMaxPeriodoConsecutivo");
        for(Constraint constraint : vincoliMaxPeriodoConsecutivo){
            ConstraintMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivo = (ConstraintMaxPeriodoConsecutivo) constraint;
            if(vincoloMaxPeriodoConsecutivo.getCategoriaVincolata() == null){
                vincoloMaxPeriodoConsecutivo.setMaxConsecutiveMinutes(configurazione.getNumMaxMinutiConsecutiviPerTutti());
            }else{
                for(ConfigVincMaxPerCons config: configurazione.getConfigVincMaxPerConsPerCategoria()){
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
