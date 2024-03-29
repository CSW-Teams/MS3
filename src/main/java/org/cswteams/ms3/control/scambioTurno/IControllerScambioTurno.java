package org.cswteams.ms3.control.scambioTurno;

import org.cswteams.ms3.dto.AnswerTurnChangeRequestDTO;
import org.cswteams.ms3.dto.RequestTurnChangeDto;
import org.cswteams.ms3.dto.ViewUserTurnRequestsDTO;
import org.cswteams.ms3.dto.concreteshift.GetAvailableUsersForReplacementDTO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.exception.ShiftException;

import javax.validation.constraints.NotNull;
import java.util.List;


public interface IControllerScambioTurno {

    /**
     * This method creates a shift change request.
     *
     * @param requestTurnChangeDto request dto
     */
    void requestShiftChange(@NotNull RequestTurnChangeDto requestTurnChangeDto) throws ConcreteShiftException;

    List<ViewUserTurnRequestsDTO> getRequestsBySender(@NotNull Long id);

    List<ViewUserTurnRequestsDTO>  getRequestsToSender(@NotNull Long id);

    void answerTurnChangeRequest(@NotNull AnswerTurnChangeRequestDTO answerTurnChangeRequestDTO) throws ShiftException;

    List<MedicalDoctorInfoDTO> getAvailableUsersForReplacement(@NotNull GetAvailableUsersForReplacementDTO dto);
}

