package org.cswteams.ms3.control.actors;

import org.cswteams.ms3.dto.singleDoctorSpecializations.SingleDoctorSpecializationsDTO;
import org.cswteams.ms3.dto.systemactor.AllSystemActorsDTO;
import org.springframework.stereotype.Service;


public interface ISystemActorController {
    AllSystemActorsDTO getAllSystemActors();

}
