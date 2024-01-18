package org.cswteams.ms3.control.condition;

import org.cswteams.ms3.dto.category.PermanentConditionDTO;
import org.cswteams.ms3.dto.category.SpecializationDTO;
import org.cswteams.ms3.dto.category.TemporaryConditionDTO;

import java.util.Set;

public interface IConditionController {
    Set<PermanentConditionDTO> readPermanentConditions();
    Set<TemporaryConditionDTO> readTemporaryConditions();

}
