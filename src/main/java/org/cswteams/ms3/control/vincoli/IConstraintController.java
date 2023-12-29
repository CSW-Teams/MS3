package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.constraint.ConfigVincoli;
import org.cswteams.ms3.entity.constraint.Constraint;

import java.util.List;

public interface IConstraintController {

    List<Constraint> readConstraints();

    ConfigVincoli updateConstraints(ConfigVincoli configurazione);

    ConfigVincoli readConfigConstraints();

}
