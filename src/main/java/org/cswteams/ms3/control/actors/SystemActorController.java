package org.cswteams.ms3.control.actors;

import org.cswteams.ms3.dto.systemactor.AllSystemActorsDTO;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SystemActorController implements ISystemActorController{

    @Override
    public AllSystemActorsDTO getAllSystemActors(){
        return new AllSystemActorsDTO(Set.of(SystemActor.DOCTOR.name(), SystemActor.PLANNER.name(),SystemActor.CONFIGURATOR.name()));
    }
}
