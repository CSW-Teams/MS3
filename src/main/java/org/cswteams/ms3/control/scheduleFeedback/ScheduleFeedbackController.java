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
    public List<ScheduleFeedbackDTO> getFeedbacksByDoctor(String email) {
        Doctor doctor = doctorDAO.findByEmail(email);
         if (doctor == null) {
             throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dottore con email " + email + " non trovato nel database."
                );
        }

        return scheduleFeedbackDAO.findByDoctorId(doctor.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // update di un feedback lasciato dall'utente, non integrato. Valutarne integrazione futura con agenti AI?
    @Override
    @Transactional
    public ScheduleFeedbackDTO updateFeedback(ScheduleFeedbackDTO feedbackDTO, String email) {
        Doctor doctor = doctorDAO.findByEmail(email);
        if (doctor == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dottore con email " + email + " non trovato nel database."
            );
        }

        ScheduleFeedback feedback = scheduleFeedbackDAO.findById(feedbackDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback con ID " + feedbackDTO.getId() + " non trovato."));

        if (!feedback.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a modificare questo feedback.");
        }

        feedback.setComment(feedbackDTO.getComment());
        feedback.setScore(feedbackDTO.getScore());

        ScheduleFeedback updatedFeedback = scheduleFeedbackDAO.save(feedback);
        return convertToDTO(updatedFeedback);
    }

    // delete di un feedback lasciato dall'utente, non integrato. Valutarne integrazione futura con agenti AI?
    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId, String email) {
        Doctor doctor = doctorDAO.findByEmail(email);
        if (doctor == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dottore con email " + email + " non trovato nel database."
            );
        }

        ScheduleFeedback feedback = scheduleFeedbackDAO.findById(feedbackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback con ID " + feedbackId + " non trovato."));

        if (!feedback.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato ad eliminare questo feedback.");
        }

        scheduleFeedbackDAO.delete(feedback);
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
