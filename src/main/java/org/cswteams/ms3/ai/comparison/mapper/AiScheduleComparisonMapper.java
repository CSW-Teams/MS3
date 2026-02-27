package org.cswteams.ms3.ai.comparison.mapper;

import org.cswteams.ms3.ai.comparison.domain.AiScheduleComparisonCandidate;
import org.cswteams.ms3.ai.comparison.domain.AiScheduleDecisionOutcome;
import org.cswteams.ms3.ai.comparison.domain.DecisionMetricValues;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleCandidateMetadataDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonCandidateDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionMetricValuesDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionMetricsDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleDecisionOutcomeDto;
import org.cswteams.ms3.ai.decision.AiScheduleCandidateMetrics;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AiScheduleComparisonMapper {

    public AiScheduleComparisonResponseDto toDto(List<AiScheduleComparisonCandidate> candidates,
                                                 AiScheduleDecisionOutcome outcome) {
        return toDto(candidates, outcome, null, null, null, false);
    }

    public AiScheduleComparisonResponseDto toDto(List<AiScheduleComparisonCandidate> candidates,
                                                 AiScheduleDecisionOutcome outcome,
                                                 String errorType,
                                                 String errorCode,
                                                 String failureStage,
                                                 boolean retryable) {
        List<AiScheduleComparisonCandidateDto> mappedCandidates = mapCandidates(candidates);
        AiScheduleDecisionOutcomeDto decisionOutcome = outcome == null ? null : new AiScheduleDecisionOutcomeDto(
                toMetadata(outcome.getCandidateId(), outcome.getScheduleId(), outcome.getType().getLabel(), true, null, null, false, Collections.emptyList())
        );
        String generationStatus = resolveGenerationStatus(mappedCandidates, errorCode);
        return new AiScheduleComparisonResponseDto(mappedCandidates,
                decisionOutcome,
                generationStatus,
                errorType,
                errorCode,
                failureStage,
                retryable);
    }

    private List<AiScheduleComparisonCandidateDto> mapCandidates(List<AiScheduleComparisonCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        return candidates.stream()
                .filter(Objects::nonNull)
                .map(this::toCandidateDto)
                .collect(Collectors.toList());
    }

    private AiScheduleComparisonCandidateDto toCandidateDto(AiScheduleComparisonCandidate candidate) {
        AiScheduleDecisionMetricsDto metrics = new AiScheduleDecisionMetricsDto(
                toMetricValues(candidate.getRawMetrics()),
                toMetricValues(candidate.getNormalizedMetrics())
        );
        return new AiScheduleComparisonCandidateDto(
                toMetadata(candidate.getCandidateId(),
                        candidate.getScheduleId(),
                        candidate.getType().getLabel(),
                        candidate.isValid(),
                        candidate.getValidationCode(),
                        candidate.getValidationMessage(),
                        candidate.isMaxRetriesReached(),
                        candidate.getValidationViolations()),
                candidate.getRawScheduleText(),
                metrics,
                candidate.isValid(),
                candidate.getAttemptCount(),
                candidate.isMaxRetriesReached(),
                candidate.getValidationErrors()
        );
    }

    private AiScheduleCandidateMetadataDto toMetadata(String candidateId,
                                                      Long scheduleId,
                                                      String typeLabel,
                                                      boolean valid,
                                                      String validationCode,
                                                      String validationMessage,
                                                      boolean maxRetriesReached,
                                                      List<String> validationViolations) {
        String resolvedCandidateId = scheduleId == null ? candidateId : null;
        return new AiScheduleCandidateMetadataDto(resolvedCandidateId,
                scheduleId,
                typeLabel,
                valid,
                validationCode,
                validationMessage,
                maxRetriesReached,
                validationViolations);
    }

    private AiScheduleDecisionMetricValuesDto toMetricValues(DecisionMetricValues values) {
        if (values == null) {
            return null;
        }
        return new AiScheduleDecisionMetricValuesDto(
                values.getCoverage(),
                values.getUffaBalance(),
                values.getUpDelta(),
                values.getVarianceDelta()
        );
    }

    private AiScheduleDecisionMetricValuesDto toMetricValues(AiScheduleCandidateMetrics values) {
        if (values == null) {
            return null;
        }
        return new AiScheduleDecisionMetricValuesDto(
                values.getCoverage(),
                values.getUffaBalance(),
                values.getUpDelta(),
                values.getVarianceDelta()
        );
    }

    private String resolveGenerationStatus(List<AiScheduleComparisonCandidateDto> candidates, String errorCode) {
        if (errorCode == null || errorCode.trim().isEmpty()) {
            return "success";
        }
        if (candidates == null || candidates.isEmpty()) {
            return "error";
        }
        return "partial";
    }
}
