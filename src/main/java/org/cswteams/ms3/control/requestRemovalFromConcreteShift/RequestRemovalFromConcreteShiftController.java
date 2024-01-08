package org.cswteams.ms3.control.requestRemovalFromConcreteShift;

import org.cswteams.ms3.control.concreteShift.IConcreteShiftController;
import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.RequestRemovalFromConcreteShiftDAO;
import org.cswteams.ms3.dto.RequestRemovalFromConcreteShiftDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RequestRemovalFromConcreteShiftController implements IRequestRemovalFromConcreteShiftController {

    @Autowired
    private RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO;

    @Autowired
    private IConcreteShiftController concreteShiftController;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private DoctorDAO doctorDAO;

    @Override
    public RequestRemovalFromConcreteShiftDTO createRequest(@NotNull RequestRemovalFromConcreteShiftDTO requestRemovalFromConcreteShiftDTO) throws DatabaseException, AssegnazioneTurnoException {
        // Some sanity check before calling the actual creation method:
        // 1. check if the provided ConcreteShift is existing
        Long concreteShiftId = requestRemovalFromConcreteShiftDTO.getIdShift();
        if (concreteShiftId == null) {
            throw new DatabaseException("Invalid ConcreteShift ID.");
        }

        Optional<ConcreteShift> concreteShift = concreteShiftDAO.findById(concreteShiftId);
        if (concreteShift.isEmpty()) {
            throw new DatabaseException("ConcreteShift not found for id = " + concreteShiftId);
        }

        // 2. check if the provided Doctor is existing
        Long requestingDoctorId = requestRemovalFromConcreteShiftDTO.getIdRequestingUser();
        if (requestingDoctorId == null) {
            throw new DatabaseException("Invalid Doctor ID.");
        }
        Doctor requestingDoctor = _getDoctor(requestingDoctorId);

        // ... now call the internal method
        return buildDTO(_createRequestRemovalFromConcreteShift(requestingDoctor, concreteShift.get(), requestRemovalFromConcreteShiftDTO.getJustification()));
    }

    /**
     * Some preliminary check on the relations between <code>Doctor</code> and <code>ConcreteShift</code> objects,
     * prior to working on them.
     *
     * @param requestingDoctor doctor issuing a request
     * @param concreteShift    shift against which the request is issued
     * @throws DatabaseException          in case an error during database lookup occurs
     * @throws AssegnazioneTurnoException in case an error during database lookup occurs
     */
    private void checkRequestAllowance(Doctor requestingDoctor, ConcreteShift concreteShift) throws DatabaseException, AssegnazioneTurnoException {
        if (!requestRemovalFromConcreteShiftDAO.findAllByAssegnazioneTurnoIdAndUtenteId(concreteShift.getId(), requestingDoctor.getId()).isEmpty()) {
            throw new DatabaseException("A request of removal is already existing for doctor " + requestingDoctor + " for the concrete shift " + concreteShift);
        }
        if (!concreteShift.isDoctorAssigned(requestingDoctor)) {
            throw new AssegnazioneTurnoException("Doctor " + requestingDoctor + " is not assigned to the concrete shift " + concreteShift);
        }
    }

    /**
     * Internal method, actually handling the creation of a request.
     *
     * @param requestingDoctor doctor issuing a request
     * @param concreteShift    shift against which the request is issued
     * @param reason           reason for the request
     * @return object related to the request, if no error occur.
     * @throws DatabaseException          in case an error during database lookup occurs
     * @throws AssegnazioneTurnoException in case an error during database lookup occurs
     */
    private RequestRemovalFromConcreteShift _createRequestRemovalFromConcreteShift(@NotNull Doctor requestingDoctor, @NotNull ConcreteShift concreteShift, @NotNull String reason) throws AssegnazioneTurnoException, DatabaseException {
        checkRequestAllowance(requestingDoctor, concreteShift);
        RequestRemovalFromConcreteShift richiestaRimozioneDaTurno = new RequestRemovalFromConcreteShift(concreteShift, requestingDoctor, reason);
        requestRemovalFromConcreteShiftDAO.saveAndFlush(richiestaRimozioneDaTurno);
        return richiestaRimozioneDaTurno;
    }

    /**
     * Internal method for getting a request via DAO, by ID.
     * This method returns the <code>RequestRemovalFromConcreteShift</code>, not the related <i>DTO</i>,
     * that can instead be obtained by the <code>getRequest</code> public method.
     *
     * @param requestId ID of the request
     * @return object related to the request, if any.
     * @throws DatabaseException in case an error during database lookup occurs
     */
    private RequestRemovalFromConcreteShift _getRequest(Long requestId) throws DatabaseException {
        Optional<RequestRemovalFromConcreteShift> request = requestRemovalFromConcreteShiftDAO.findById(requestId);
        if (request.isEmpty()) {
            throw new DatabaseException("RequestRemovalFromConcreteShift not found for id = " + requestId);
        }
        return request.get();
    }

    @Override
    public RequestRemovalFromConcreteShiftDTO getRequest(Long requestId) throws DatabaseException {
        RequestRemovalFromConcreteShift request = _getRequest(requestId);
        return buildDTO(request);
    }

    @Override
    public Set<RequestRemovalFromConcreteShiftDTO> getAllRequests() {
        return buildDTOList(requestRemovalFromConcreteShiftDAO.findAll());
    }

    @Override
    public Set<RequestRemovalFromConcreteShiftDTO> getPendingRequests() {
        return buildDTOList(requestRemovalFromConcreteShiftDAO.findAllPending());
    }

    @Override
    @Transactional
    public Set<RequestRemovalFromConcreteShiftDTO> getRequestsByRequestingDoctorId(Long requestingDoctorId) {
        return buildDTOList(requestRemovalFromConcreteShiftDAO.findAllByUser(requestingDoctorId));
    }

    private Doctor _getDoctor(Long doctorId) throws DatabaseException {
        Optional<Doctor> doctor = doctorDAO.findById(doctorId);
        if (doctor.isEmpty()) {
            throw new DatabaseException("Doctor not found for id = " + doctorId);
        }
        return doctor.get();
    }

    @Override
    @Transactional
    public RequestRemovalFromConcreteShiftDTO reviewRequest(RequestRemovalFromConcreteShiftDTO requestRemovalFromConcreteShiftDTO) throws DatabaseException, AssegnazioneTurnoException {
        RequestRemovalFromConcreteShift request = _getRequest(requestRemovalFromConcreteShiftDTO.getIdRequest());
        if (request.isReviewed()) {
            throw new RuntimeException("RequestRemovalFromConcreteShift with id = " + request.getId() + " is already reviewed.");
        }
        if (requestRemovalFromConcreteShiftDTO.isOutcome()) {
            ConcreteShift concreteShift = request.getConcreteShift();
            Doctor requestingDoctor = _getDoctor(requestRemovalFromConcreteShiftDTO.getIdRequestingUser());
            Doctor substituteDoctor = _getDoctor(requestRemovalFromConcreteShiftDTO.getIdSubstitute());

            concreteShiftController.substituteAssignedDoctor(concreteShift, requestingDoctor, substituteDoctor);

            request.setAccepted(true);
            request.setSubstituteDoctor(substituteDoctor);
        } else {
            request.setAccepted(false);
        }
        request.setReviewed(true);
        requestRemovalFromConcreteShiftDAO.saveAndFlush(request);
        return buildDTO(request);
    }

    @Override
    @Transactional
    public RequestRemovalFromConcreteShiftDTO uploadFile(Long requestId, MultipartFile file) throws IOException, DatabaseException {
        RequestRemovalFromConcreteShift request = _getRequest(requestId);
        request.setFile(file.getBytes());
        requestRemovalFromConcreteShiftDAO.saveAndFlush(request);
        return buildDTO(request);
    }

    private RequestRemovalFromConcreteShiftDTO buildDTO(RequestRemovalFromConcreteShift request) {
        return new RequestRemovalFromConcreteShiftDTO(
                request.getId(),
                request.getConcreteShift().getId(),
                request.getRequestingDoctor().getId(),
                request.getSubstituteDoctor() == null ? null : request.getSubstituteDoctor().getId(),
                request.isAccepted(),
                request.getReason(),
                request.getFile(),
                request.isReviewed());
    }

    private Set<RequestRemovalFromConcreteShiftDTO> buildDTOList(List<RequestRemovalFromConcreteShift> richiestaRimozioneDaTurnoList) {
        Set<RequestRemovalFromConcreteShiftDTO> richiestaRimozioneDaTurnoDTOS = new HashSet<>();
        for (RequestRemovalFromConcreteShift entity : richiestaRimozioneDaTurnoList) {
            richiestaRimozioneDaTurnoDTOS.add(buildDTO(entity));
        }
        return richiestaRimozioneDaTurnoDTOS;
    }
}
