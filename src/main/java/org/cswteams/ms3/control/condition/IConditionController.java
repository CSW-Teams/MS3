package org.cswteams.ms3.control.categorie;

import org.cswteams.ms3.dto.category.PermanentConditionDTO;
import org.cswteams.ms3.dto.category.RotationDTO;
import org.cswteams.ms3.dto.category.SpecializationDTO;
import org.cswteams.ms3.dto.category.TemporaryConditionDTO;

import java.util.Set;

public interface IControllerCategorie {
    Set<PermanentConditionDTO> readPermanentConditions();
    Set<TemporaryConditionDTO> readTemporaryConditions();

    Set<RotationDTO> readRotations();

    Set<SpecializationDTO> readSpecializations();

}
