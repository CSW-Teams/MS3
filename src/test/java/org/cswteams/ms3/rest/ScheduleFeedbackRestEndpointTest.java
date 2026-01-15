package org.cswteams.ms3.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.config.soft_delete.SoftDeleteService;
import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.control.logout.JwtBlacklistService;
import org.cswteams.ms3.control.scheduleFeedback.IScheduleFeedbackController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;
import org.cswteams.ms3.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ScheduleFeedbackRestEndpoint.class, secure = false)
public class ScheduleFeedbackRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IScheduleFeedbackController scheduleFeedbackController;

    @MockBean
    private SoftDeleteService softDeleteService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginController loginController;
    @MockBean
    private JwtBlacklistService jwtBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurity() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addFeedback_success() throws Exception {
        ScheduleFeedbackDTO dto = new ScheduleFeedbackDTO();
        dto.setComment("Good");
        dto.setScore(5);
        dto.setConcreteShiftIds(Collections.singletonList(1L));

        when(scheduleFeedbackController.addFeedback(any(ScheduleFeedbackDTO.class), any())).thenReturn(dto);

        mockMvc.perform(post("/schedule-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void addFeedback_badRequest_emptyBody() throws Exception {
        mockMvc.perform(post("/schedule-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFeedbacks_success() throws Exception {
        ScheduleFeedbackDTO dto = new ScheduleFeedbackDTO();
        dto.setComment("Good");

        when(scheduleFeedbackController.getAllFeedbacks()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/schedule-feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Good"));
    }

    @Test
    void getMyFeedbacks_success() throws Exception {
        ScheduleFeedbackDTO dto = new ScheduleFeedbackDTO();
        dto.setComment("Mine");

        when(scheduleFeedbackController.getFeedbacksByDoctor(any())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/schedule-feedback/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Mine"));
    }

    @Test
    void updateFeedback_success() throws Exception {
        ScheduleFeedbackDTO dto = new ScheduleFeedbackDTO();
        dto.setId(1L);
        dto.setComment("Updated");
        dto.setScore(4);
        dto.setConcreteShiftIds(Collections.singletonList(1L));

        when(scheduleFeedbackController.updateFeedback(any(ScheduleFeedbackDTO.class), any())).thenReturn(dto);

        mockMvc.perform(put("/schedule-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated"));
    }

    @Test
    void deleteFeedback_success() throws Exception {
        mockMvc.perform(delete("/schedule-feedback/1"))
                .andExpect(status().isNoContent());
    }
}
