package org.cswteams.ms3.control.scambioTurno;

import org.cswteams.ms3.dto.RequestTurnChangeDto;
import org.cswteams.ms3.dto.ViewUserTurnRequestsDTO;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;

import javax.validation.constraints.NotNull;
import java.util.List;


public interface IControllerScambioTurno {

    void requestTurnChange(@NotNull RequestTurnChangeDto requestTurnChangeDto) throws AssegnazioneTurnoException;

    List<ViewUserTurnRequestsDTO> getRequestsBySender(@NotNull Long id);

    List<ViewUserTurnRequestsDTO>  getRequestsToSender(@NotNull Long id);
}

