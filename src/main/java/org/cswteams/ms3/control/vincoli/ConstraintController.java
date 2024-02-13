package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.dao.ConfigVincoliDAO;
import org.cswteams.ms3.dao.ConfigVincoloMaxPeriodoConsecutivoDAO;
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.entity.constraint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConstraintController implements IConstraintController {

    @Autowired
    ConstraintDAO constraintDAO;

    @Autowired
    ConfigVincoliDAO configVincoliDao;

    @Autowired
    ConfigVincoloMaxPeriodoConsecutivoDAO configVincoloMaxPeriodoConsecutivoDao;

    /**
     * This method retrieves all the constraints saved into the database.
     * @return List of constraints
     */
    @Override
    public List<Constraint> readConstraints() {
        return constraintDAO.findAll();
    }

    /**
     * This method updates the constraints saved into the database according to the configuration passed as parameter.
     * @param configuration Constraints configurations determining how the constraints have to be updated
     * @return Updated ConfigVincoli instance
     */
    @Override
    public ConfigVincoli updateConstraints(ConfigVincoli configuration) {
        for(ConfigVincMaxPerCons config: configuration.getConfigVincMaxPerConsPerCategoria()){
            ConfigVincMaxPerCons configVincMaxPerCons = configVincoloMaxPeriodoConsecutivoDao.findAllByConstrainedConditionType(config.getConstrainedCondition().getType()).get(0);
            config.setId(configVincMaxPerCons.getId());
            configVincoloMaxPeriodoConsecutivoDao.save(config);
        }
        //Update configuration
        ConfigVincoli configVincoli = configVincoliDao.findAll().get(0);
        configuration.setId(configVincoli.getId());
        configVincoliDao.save(configuration);
        //Update constraints
        ConstraintTurniContigui vincoloTipologieTurniContigue = (ConstraintTurniContigui) constraintDAO.findByType("ConstraintTurniContigui").get(0);
        vincoloTipologieTurniContigue.setHorizon(configuration.getHorizonNightShift());

        ConstraintMaxOrePeriodo vincoloMaxOrePeriodo = (ConstraintMaxOrePeriodo) constraintDAO.findByType("ConstraintMaxOrePeriodo").get(0);
        vincoloMaxOrePeriodo.setPeriodDuration(configuration.getPeriodDaysNo());
        vincoloMaxOrePeriodo.setPeriodMaxTime(configuration.getPeriodMaxTime());

        List<Constraint> vincoliMaxPeriodoConsecutivo = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");
        for(Constraint constraint : vincoliMaxPeriodoConsecutivo){
            ConstraintMaxPeriodoConsecutivo vincoloMaxPeriodoConsecutivo = (ConstraintMaxPeriodoConsecutivo) constraint;
            if(vincoloMaxPeriodoConsecutivo.getConstrainedCategory() == null){
                vincoloMaxPeriodoConsecutivo.setMaxConsecutiveMinutes(configuration.getMaxConsecutiveTimeForEveryone());
            }else{
                for(ConfigVincMaxPerCons config: configuration.getConfigVincMaxPerConsPerCategoria()){
                    if(vincoloMaxPeriodoConsecutivo.getConstrainedCategory().getType().equals(config.getConstrainedCondition().getType())){
                        vincoloMaxPeriodoConsecutivo.setMaxConsecutiveMinutes(config.getMaxConsecutiveMinutes());
                    }
                }
            }
        }

        constraintDAO.saveAndFlush(vincoloTipologieTurniContigue);
        constraintDAO.saveAll(vincoliMaxPeriodoConsecutivo);
        constraintDAO.saveAndFlush(vincoloMaxOrePeriodo);

        return configuration;
    }

    /**
     * This method retrieves the constraints configuration.
     * @return ConfigVincoli instance
     */
    @Override
    public ConfigVincoli readConfigConstraints() {
        return configVincoliDao.findAll().get(0);
    }


}
