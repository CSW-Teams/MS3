package org.cswteams.ms3.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleCandidateMetadataDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonCandidateDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionMetricValuesDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionMetricsDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionOutcomeDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleSelectionRequestDto;
import org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ComparisonRestEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiScheduleGenerationOrchestrationService aiScheduleGenerationOrchestrationService;

    @Test
    @WithMockUser(roles = "PLANNER")
    void getComparison_returnsComparisonPayload() throws Exception {
        AiScheduleDecisionMetricValuesDto raw = new AiScheduleDecisionMetricValuesDto(1.0, 2.0, 3.0, 4.0, 5.0);
        AiScheduleDecisionMetricValuesDto normalized = new AiScheduleDecisionMetricValuesDto(0.5, 0.6, 0.7, 0.8, 0.9);
        AiScheduleDecisionMetricsDto metrics = new AiScheduleDecisionMetricsDto(raw, normalized);
        AiScheduleCandidateMetadataDto metadata = new AiScheduleCandidateMetadataDto("standard", null, "STANDARD");
        AiScheduleComparisonCandidateDto candidate = new AiScheduleComparisonCandidateDto(metadata, "{}", metrics);
        AiScheduleDecisionOutcomeDto outcome = new AiScheduleDecisionOutcomeDto(metadata);
        AiScheduleComparisonResponseDto response = new AiScheduleComparisonResponseDto(List.of(candidate), outcome);

        when(aiScheduleGenerationOrchestrationService.getLatestComparison()).thenReturn(response);

        mockMvc.perform(get("/api/comparison"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidates[0].metadata.candidateId").value("standard"))
                .andExpect(jsonPath("$.candidates[0].metrics.raw.coverage").value(1.0))
                .andExpect(jsonPath("$.decisionOutcome.selectedCandidate.type").value("STANDARD"));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectSchedule_withScheduleId_returnsAccepted() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto(null, 12L);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.persisted(12L);

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/api/comparison/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.scheduleId").value(12L));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectSchedule_withCandidateId_returnsAccepted() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("ai-balanced", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.persisted(24L);

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/api/comparison/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.scheduleId").value(24L));
    }

    @Test
    @WithMockUser(roles = "PLANNER")
    void selectSchedule_withUnresolvedCandidate_returnsConflict() throws Exception {
        AiScheduleSelectionRequestDto request = new AiScheduleSelectionRequestDto("missing", null);
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                AiScheduleGenerationOrchestrationService.SelectionResult.notFound("CANDIDATE_NOT_FOUND",
                        "Candidate could not be resolved.");

        when(aiScheduleGenerationOrchestrationService.selectSchedule(any())).thenReturn(result);

        mockMvc.perform(post("/api/comparison/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("CANDIDATE_NOT_FOUND"));
    }
}
