package org.cswteams.ms3.control.scheduleFeedback;

import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ScheduleFeedbackDAO;
import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.ScheduleFeedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduleFeedbackControllerTest {

    @Mock
    private ScheduleFeedbackDAO scheduleFeedbackDAO;

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private ConcreteShiftDAO concreteShiftDAO;

    @Mock
    private Clock clock;

    @InjectMocks
    private ScheduleFeedbackController controller;

    private Doctor doctor;
    private ConcreteShift shift;
    private ScheduleFeedbackDTO feedbackDTO;
    private ScheduleFeedback feedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mocks for entities
        doctor = mock(Doctor.class);
        when(doctor.getId()).thenReturn(1L);
        when(doctor.getName()).thenReturn("Stanis");
        when(doctor.getLastname()).thenReturn("La Rochelle");
        when(doctor.getEmail()).thenReturn("stanis.larochelle@example.com");

        shift = mock(ConcreteShift.class);
        when(shift.getId()).thenReturn(10L);

        feedbackDTO = new ScheduleFeedbackDTO();
        feedbackDTO.setComment("Great shift!");
        feedbackDTO.setScore(5);
        feedbackDTO.setConcreteShiftIds(Collections.singletonList(10L));
        
        // For the tests where we need a return value from DAO
        feedback = mock(ScheduleFeedback.class);
        when(feedback.getId()).thenReturn(100L);
        when(feedback.getDoctor()).thenReturn(doctor);
        when(feedback.getConcreteShifts()).thenReturn(Collections.singletonList(shift));
        when(feedback.getComment()).thenReturn("Great shift!");
        when(feedback.getScore()).thenReturn(5);
        when(feedback.getTimestamp()).thenReturn(1000L);
    }

    @Test
    void addFeedback_success() {
        when(doctorDAO.findByEmail("stanis.larochelle@example.com")).thenReturn(doctor);
        when(concreteShiftDAO.findAllById(any())).thenReturn(Collections.singletonList(shift));
        when(clock.millis()).thenReturn(1000L);
        when(scheduleFeedbackDAO.save(any(ScheduleFeedback.class))).thenAnswer(invocation -> {
            ScheduleFeedback saved = invocation.getArgument(0);
            return feedback;
        });

        ScheduleFeedbackDTO result = controller.addFeedback(feedbackDTO, "stanis.larochelle@example.com");

        assertNotNull(result);
        assertEquals(100L, result.getId().longValue());
        assertEquals("Stanis", result.getDoctorName());
        assertEquals("La Rochelle", result.getDoctorLastname());
        assertEquals(1000L, result.getTimestamp());
        verify(scheduleFeedbackDAO).save(any(ScheduleFeedback.class));
    }

    @Test
    void addFeedback_doctorNotFound() {
        when(doctorDAO.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> 
            controller.addFeedback(feedbackDTO, "unknown@example.com")
        );
        verify(scheduleFeedbackDAO, never()).save(any());
    }

    @Test
    void addFeedback_shiftNotFound() {
        when(doctorDAO.findByEmail("stanis.larochelle@example.com")).thenReturn(doctor);
        when(concreteShiftDAO.findAllById(any())).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> 
            controller.addFeedback(feedbackDTO, "stanis.larochelle@example.com")
        );
        verify(scheduleFeedbackDAO, never()).save(any());
    }

    @Test
    void getAllFeedbacks() {
        when(scheduleFeedbackDAO.findAll()).thenReturn(Collections.singletonList(feedback));

        List<ScheduleFeedbackDTO> results = controller.getAllFeedbacks();

        assertEquals(1, results.size());
        assertEquals("Great shift!", results.get(0).getComment());
    }

    @Test
    void getFeedbacksByDoctor() {
        when(doctorDAO.findByEmail("stanis.larochelle@example.com")).thenReturn(doctor);
        when(scheduleFeedbackDAO.findByDoctorId(1L)).thenReturn(Collections.singletonList(feedback));

        List<ScheduleFeedbackDTO> results = controller.getFeedbacksByDoctor("stanis.larochelle@example.com");

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getDoctorId().longValue());
    }

    @Test
    void updateFeedback_success() {
        feedbackDTO.setId(100L);
        feedbackDTO.setComment("Updated comment");
        feedbackDTO.setScore(4);

        when(doctorDAO.findByEmail("stanis.larochelle@example.com")).thenReturn(doctor);
        when(scheduleFeedbackDAO.findById(100L)).thenReturn(Optional.of(feedback));
        when(scheduleFeedbackDAO.save(any(ScheduleFeedback.class))).thenReturn(feedback);

        ScheduleFeedbackDTO result = controller.updateFeedback(feedbackDTO, "stanis.larochelle@example.com");

        assertEquals("Great shift!", result.getComment());
        verify(feedback).setComment("Updated comment");
        verify(feedback).setScore(4);
    }

    @Test
    void updateFeedback_forbidden() {
        feedbackDTO.setId(100L);
        Doctor otherDoctor = mock(Doctor.class);
        when(otherDoctor.getId()).thenReturn(2L);
        
        when(doctorDAO.findByEmail("other@example.com")).thenReturn(otherDoctor);
        when(scheduleFeedbackDAO.findById(100L)).thenReturn(Optional.of(feedback)); // feedback belongs to doctor 1

        assertThrows(ResponseStatusException.class, () -> 
            controller.updateFeedback(feedbackDTO, "other@example.com")
        );
    }

    @Test
    void deleteFeedback_success() {
        when(doctorDAO.findByEmail("stanis.larochelle@example.com")).thenReturn(doctor);
        when(scheduleFeedbackDAO.findById(100L)).thenReturn(Optional.of(feedback));

        controller.deleteFeedback(100L, "stanis.larochelle@example.com");

        verify(scheduleFeedbackDAO).delete(feedback);
    }
}