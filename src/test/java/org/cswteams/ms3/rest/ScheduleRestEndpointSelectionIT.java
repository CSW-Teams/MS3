package org.cswteams.ms3.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleSelectionRequestDto;
import org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ScheduleRestEndpointSelectionIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISchedulerController schedulerController;

    @MockBean
    private AiScheduleGenerationOrchestrationService aiScheduleGenerationOrchestrationService;

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_persistsStandardSelection() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("standard", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.persisted(42L);

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.scheduleId").value(42L));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_persistsAiVariantSelection() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("ai-efficient", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.persisted(77L);

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.scheduleId").value(77L));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_duplicateRangeReturnsNotAcceptable() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("standard", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.duplicateRange(
                        "DUPLICATE_RANGE",
                        "Schedule already exists for this date range.");

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RANGE"));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_missingComparisonReturnsNotFound() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("standard", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.noActiveComparison(
                        "NO_ACTIVE_COMPARISON",
                        "No active comparison to resolve.");

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NO_ACTIVE_COMPARISON"));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_invalidSelectionReturnsBadRequest() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto(null, null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.invalid(
                        "MISSING_SELECTION",
                        "Provide a scheduleId or candidateId.");

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MISSING_SELECTION"));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectScheduleCandidate_unknownCandidateReturnsNotFound() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("missing", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.notFound(
                        "CANDIDATE_NOT_FOUND",
                        "Candidate could not be resolved.");

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/schedule/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("CANDIDATE_NOT_FOUND"));
    }

}
