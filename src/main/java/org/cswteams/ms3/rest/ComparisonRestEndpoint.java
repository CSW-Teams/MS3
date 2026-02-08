package org.cswteams.ms3.rest;

import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleSelectionRequestDto;
import org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/comparison")
public class ComparisonRestEndpoint {

    @Autowired
    private AiScheduleGenerationOrchestrationService aiScheduleGenerationOrchestrationService;

    @PreAuthorize("hasAnyRole('PLANNER')")
    @GetMapping
    public ResponseEntity<?> getComparison() {
        AiScheduleComparisonResponseDto response = aiScheduleGenerationOrchestrationService.getLatestComparison();
        if (response == null) {
            return new ResponseEntity<>(Map.of(
                    "errorCode", "NO_ACTIVE_COMPARISON",
                    "message", "No comparison is currently available."
            ), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PLANNER')")
    @PostMapping("/selection")
    public ResponseEntity<?> selectSchedule(@RequestBody AiScheduleSelectionRequestDto selection) {
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                aiScheduleGenerationOrchestrationService.selectSchedule(selection);
        if (result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.PERSISTED) {
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        }
        if (result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.INVALID_SELECTION) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }
}
