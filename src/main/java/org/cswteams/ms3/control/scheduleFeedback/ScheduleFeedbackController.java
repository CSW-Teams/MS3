package org.cswteams.ms3.control.scheduleFeedback;

import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ScheduleFeedbackDAO;
import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.ScheduleFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleFeedbackController implements IScheduleFeedbackController {

    @Autowired
    private ScheduleFeedbackDAO scheduleFeedbackDAO;

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private Clock clock;

    @Override
    @Transactional
    public ScheduleFeedbackDTO addFeedback(ScheduleFeedbackDTO feedbackDTO, String email) {
        Doctor doctor = doctorDAO.findByEmail(email);
        if (doctor == null) {
             throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dottore con email " + email + " non trovato nel database."
                );
        }
        
        feedbackDTO.setDoctorId(doctor.getId());
        return addFeedbackById(feedbackDTO);
    }

    private ScheduleFeedbackDTO addFeedbackById(ScheduleFeedbackDTO feedbackDTO) {
        if (feedbackDTO.getDoctorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID del Dottore non trovato");
        }

        Doctor doctor = doctorDAO.findById(feedbackDTO.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dottore con ID " + feedbackDTO.getDoctorId() + " non trovato nel database."
                ));

        if (feedbackDTO.getConcreteShiftIds() == null || feedbackDTO.getConcreteShiftIds().isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Devi selezionare almeno un turno per inviare un feedback.");
        }

        Set<Long> requestedShiftIds = feedbackDTO.getConcreteShiftIds().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (requestedShiftIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Devi selezionare almeno un turno valido per inviare un feedback.");
        }

        List<ConcreteShift> foundShifts = concreteShiftDAO.findAllById(requestedShiftIds);

        if (foundShifts.size() != requestedShiftIds.size()) {
            Set<Long> foundIds = foundShifts.stream()
                    .map(ConcreteShift::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = requestedShiftIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile salvare il feedback. I seguenti turni non esistono o sono stati cancellati: " + missingIds
            );
        }

        ScheduleFeedback feedback = new ScheduleFeedback(
                doctor,
                foundShifts,
                feedbackDTO.getComment(),
                feedbackDTO.getScore(),
                clock.millis()
        );

        ScheduleFeedback savedFeedback = scheduleFeedbackDAO.save(feedback);

        feedbackDTO.setId(savedFeedback.getId());
        feedbackDTO.setTimestamp(savedFeedback.getTimestamp());
        feedbackDTO.setDoctorName(doctor.getName());
        feedbackDTO.setDoctorLastname(doctor.getLastname());

        return feedbackDTO;
    }

    @Override
    public List<ScheduleFeedbackDTO> getAllFeedbacks() {
        return scheduleFeedbackDAO.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleFeedbackDTO> getFeedbacksByDoctor(Long doctorId) {
        return scheduleFeedbackDAO.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleFeedbackDTO> getFeedbacksByDoctorEmail(String email) {
        Doctor doctor = doctorDAO.findByEmail(email);
         if (doctor == null) {
             throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dottore con email " + email + " non trovato nel database."
                );
        }
        return getFeedbacksByDoctor(doctor.getId());
    }

    private ScheduleFeedbackDTO convertToDTO(ScheduleFeedback feedback) {
        return new ScheduleFeedbackDTO(
                feedback.getId(),
                feedback.getDoctor().getId(),
                feedback.getDoctor().getName(),
                feedback.getDoctor().getLastname(),
                feedback.getConcreteShifts().stream().map(ConcreteShift::getId).collect(Collectors.toList()),
                feedback.getComment(),
                feedback.getScore(),
                feedback.getTimestamp()
        );
    }
}
