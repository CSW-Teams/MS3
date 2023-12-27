package org.cswteams.ms3.control.richiestaRimozioneDaTurno;

import org.cswteams.ms3.dto.RequestRemovalFromConcreteShiftDTO;
import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Set;

public interface IControllerRequestRemovalFromConcreteShift {

    /**
     * Create a new request of removal from concrete shift, given a DTO.
     *
     * @param requestRemovalFromConcreteShiftDTO DTO related to the request
     * @return DTO object related to the request
     * @throws DatabaseException          in case an error during database lookup occurs
     * @throws AssegnazioneTurnoException in case an error during database lookup occurs
     */
    RequestRemovalFromConcreteShiftDTO createRequest(RequestRemovalFromConcreteShiftDTO requestRemovalFromConcreteShiftDTO) throws DatabaseException, AssegnazioneTurnoException;

    /**
     * Get a specific request, given an ID.
     *
     * @param requestId request id
     * @return object related to the request, if any
     * @throws DatabaseException in case an error during database lookup occurs
     */
    RequestRemovalFromConcreteShiftDTO getRequest(Long requestId) throws DatabaseException;

    /**
     * Get all the requests from the database.
     *
     * @return <code>Set</code> of all the requests from the database
     */
    Set<RequestRemovalFromConcreteShiftDTO> getAllRequests();

    /**
     * Get all pending requests (i.e. not reviewed).
     *
     * @return <code>Set</code> of all the pending requests from the database
     */
    Set<RequestRemovalFromConcreteShiftDTO> getPendingRequests();

    /**
     * Get all the requests, filtered by requesting doctor ID.
     *
     * @param requestingDoctorId requesting doctor ID, for filtering
     * @return <code>Set</code> of all the requests related to the specific doctor whose ID is provided
     */
    Set<RequestRemovalFromConcreteShiftDTO> getRequestsByRequestingDoctorId(Long requestingDoctorId);

    /**
     * Review a request (done by a <i>Planner</i> - the outcome is embedded into the DTO parameter).
     *
     * @param requestRemovalFromConcreteShiftDTO DTO related to the request
     * @return DTO related to the request, updated with the outcome of the review
     * @throws DatabaseException in case an error during database lookup occurs
     */
    RequestRemovalFromConcreteShiftDTO reviewRequest(RequestRemovalFromConcreteShiftDTO requestRemovalFromConcreteShiftDTO) throws DatabaseException, AssegnazioneTurnoException;

    /**
     * Upload a file (not mandatory) to be attached to a request, given the request ID.
     *
     * @param requestId request id
     * @param file      file to be attached
     * @return DTO related to the request, updated with the file
     * @throws IOException       in case of I/O errors
     * @throws DatabaseException in case an error during database lookup occurs
     */
    RequestRemovalFromConcreteShiftDTO uploadFile(@NotNull Long requestId, @NotNull MultipartFile file) throws IOException, DatabaseException;
}
