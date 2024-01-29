package org.cswteams.ms3.control.actors;

import org.cswteams.ms3.dto.systemactor.AllSystemActorsDTO;

public interface ISystemActorController {

    /**
     * Generate a DTO containing all the available roles that a <i>user</i> can have in the system.
     *
     * @return DTO with available roles
     * @see org.cswteams.ms3.enums.SystemActor
     */
    AllSystemActorsDTO getAllSystemActors();

}
