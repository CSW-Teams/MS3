package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerProperties;
import org.cswteams.ms3.ai.broker.AiBrokerRequest;
import org.cswteams.ms3.ai.broker.AiTokenBudgetGuardResult;
import org.cswteams.ms3.ai.broker.domain.AiAssignment;
import org.cswteams.ms3.ai.broker.domain.AiMetadata;
import org.cswteams.ms3.ai.broker.domain.AiMetrics;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.broker.domain.AiStdDev;
import org.cswteams.ms3.ai.broker.domain.AiUffaBalance;
import org.cswteams.ms3.ai.broker.domain.AiUffaDelta;
import org.cswteams.ms3.ai.broker.domain.AiUncoveredShift;
import org.cswteams.ms3.ai.comparison.domain.AiScheduleComparisonCandidate;
import org.cswteams.ms3.ai.comparison.domain.AiScheduleDecisionOutcome;
import org.cswteams.ms3.ai.comparison.domain.DecisionMetricValues;
import org.cswteams.ms3.ai.comparison.domain.ScheduleCandidateType;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleSelectionRequestDto;
import org.cswteams.ms3.ai.comparison.mapper.AiScheduleComparisonMapper;
import org.cswteams.ms3.ai.decision.AiScheduleCandidateMetrics;
import org.cswteams.ms3.ai.decision.DecisionAlgorithmService;
import org.cswteams.ms3.ai.metrics.MetricAggregationUtils;
import org.cswteams.ms3.ai.metrics.MetricNormalizationUtils;
import org.cswteams.ms3.ai.priority.PriorityScaleValidationException;
import org.cswteams.ms3.ai.protocol.converter.AiScheduleConverterService;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiStdDevDto;
import org.cswteams.ms3.ai.protocol.dto.AiUffaBalanceDto;
import org.cswteams.ms3.ai.protocol.dto.AiUffaDeltaDto;
import org.cswteams.ms3.ai.protocol.dto.AiUncoveredShiftDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.ValidationError;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.ai.protocol.utils.AiUffaQueue;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonRequestContext;
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.DoctorHolidaysDAO;
import org.cswteams.ms3.dao.DoctorUffaPriorityDAO;
import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.DoctorUffaPrioritySnapshot;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ContextConstraint;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiScheduleGenerationOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(AiScheduleGenerationOrchestrationService.class);
    private static final String MODE_GENERATE = "generate";
    private static final String METRICS_COMPUTE_STAGE = "METRICS_COMPUTE";
    private static final String ERROR_STANDARD_METRICS = "STANDARD_METRICS_FAILED";
    private static final String ERROR_AI_METRICS = "AI_METRICS_FAILED";
    private static final String ERROR_AI_CANDIDATE_VALIDATION = "AI_CANDIDATE_VALIDATION_FAILED";
    private static final String ERROR_ROLE_LAYER_MINIMA = "ROLE_LAYER_MINIMA_VIOLATED";
    private static final String ERROR_NORMALIZATION = "METRICS_NORMALIZATION_FAILED";
    private static final String ERROR_DECISION = "METRICS_DECISION_FAILED";
    private static final String INVALID_CANDIDATE_SELECTION_ERROR_CODE = "INVALID_CANDIDATE_SELECTION";
    private static final String INVALID_CANDIDATE_SELECTION_ERROR_MESSAGE = "Selected candidate is invalid in the active comparison state.";
    private static final double METRICS_COMPARISON_TOLERANCE = 1e-6;
    private static final String EMPATHETIC_LABEL = "EMPATHETIC";
    private static final String EFFICIENT_LABEL = "EFFICIENT";
    private static final String BALANCED_LABEL = "BALANCED";
    private static final int ROLE_MISMATCH_LOG_LIMIT = 5;
    private static final int VALIDATION_MISMATCH_SAMPLE_LIMIT = 5;
    private static final int VALIDATION_FAILURE_PROMPT_MAX_DETAILS = 5;
    private static final int VALIDATION_FAILURE_PROMPT_MAX_CHARS = 1200;
    private static final Pattern SCRATCHPAD_ROLE_MISMATCH_PATTERN = Pattern.compile(".*seniority=([A-Z_]+)\\s+but\\s+role_required=([A-Z_]+).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern ASSIGNMENT_ROLE_MISMATCH_PATTERN = Pattern.compile(".*seniority\\s+([A-Z_]+).*", Pattern.CASE_INSENSITIVE);
    private static final String BASE_VARIANT_INSTRUCTION = "Return strict JSON only with top-level object {'variants': ...}. "
            + "For object form, put the variant label as the key under variants. "
            + "If variants is an array, each item must include a non-empty 'label' field and a 'variant' object payload. "
            + "Return exactly one variant entry matching the requested label.";
    private static final List<VariantDefinition> VARIANT_DEFINITIONS = List.of(
            new VariantDefinition(EMPATHETIC_LABEL,
                    "ai-empathetic",
                    ScheduleCandidateType.EMPATHETIC,
                    "Use label EMPATHETIC (either as variants.EMPATHETIC or variants[0].label=EMPATHETIC)."),
            new VariantDefinition(EFFICIENT_LABEL,
                    "ai-efficient",
                    ScheduleCandidateType.EFFICIENT,
                    "Use label EFFICIENT (either as variants.EFFICIENT or variants[0].label=EFFICIENT)."),
            new VariantDefinition(BALANCED_LABEL,
                    "ai-balanced",
                    ScheduleCandidateType.BALANCED,
                    "Use label BALANCED (either as variants.BALANCED or variants[0].label=BALANCED).")
    );

    private final ISchedulerController schedulerController;
    private final DoctorDAO doctorDAO;
    private final DoctorUffaPriorityDAO doctorUffaPriorityDAO;
    private final DoctorHolidaysDAO doctorHolidaysDAO;
    private final ConstraintDAO constraintDAO;
    private final HolidayDAO holidayDAO;
    private final ScheduleDAO scheduleDAO;
    private final AgentBroker agentBroker;
    private final AiBrokerProperties aiBrokerProperties;
    private final AiReschedulingOrchestrationService aiReschedulingOrchestrationService;
    private final DecisionAlgorithmService decisionAlgorithmService;
    private final AiScheduleConverterService aiScheduleConverterService;
    private final AiHardCoveragePromptBlockBuilder hardCoveragePromptBlockBuilder;
    private final AiRoleValidationScratchpadPromptBlockBuilder roleValidationScratchpadPromptBlockBuilder;
    private final ObjectMapper objectMapper;
    private final AiScheduleComparisonMapper comparisonMapper = new AiScheduleComparisonMapper();
    private final AtomicReference<TransientComparisonState> transientComparisonState = new AtomicReference<>();

    @Autowired
    public AiScheduleGenerationOrchestrationService(ISchedulerController schedulerController,
                                                    DoctorDAO doctorDAO,
                                                    DoctorUffaPriorityDAO doctorUffaPriorityDAO,
                                                    DoctorHolidaysDAO doctorHolidaysDAO,
                                                    ConstraintDAO constraintDAO,
                                                    HolidayDAO holidayDAO,
                                                    ScheduleDAO scheduleDAO,
                                                    AgentBroker agentBroker,
                                                    AiBrokerProperties aiBrokerProperties,
                                                    AiReschedulingOrchestrationService aiReschedulingOrchestrationService,
                                                    DecisionAlgorithmService decisionAlgorithmService,
                                                    AiScheduleConverterService aiScheduleConverterService,
                                                    AiHardCoveragePromptBlockBuilder hardCoveragePromptBlockBuilder,
                                                    AiRoleValidationScratchpadPromptBlockBuilder roleValidationScratchpadPromptBlockBuilder,
                                                    ObjectMapper objectMapper) {
        this.schedulerController = schedulerController;
        this.doctorDAO = doctorDAO;
        this.doctorUffaPriorityDAO = doctorUffaPriorityDAO;
        this.doctorHolidaysDAO = doctorHolidaysDAO;
        this.constraintDAO = constraintDAO;
        this.holidayDAO = holidayDAO;
        this.scheduleDAO = scheduleDAO;
        this.agentBroker = agentBroker;
        this.aiBrokerProperties = aiBrokerProperties;
        this.aiReschedulingOrchestrationService = aiReschedulingOrchestrationService;
        this.decisionAlgorithmService = decisionAlgorithmService;
        this.aiScheduleConverterService = aiScheduleConverterService;
        this.hardCoveragePromptBlockBuilder = hardCoveragePromptBlockBuilder;
        this.roleValidationScratchpadPromptBlockBuilder = roleValidationScratchpadPromptBlockBuilder;
        this.objectMapper = objectMapper;
    }

    public AiScheduleComparisonResponseDto generateScheduleComparison(LocalDate startDate, LocalDate endDate) {
        logger.info("event=ai_standard_generation_start start_date={} end_date={}", startDate, endDate);
        Schedule standardSchedule = schedulerController.createScheduleTransient(startDate, endDate);
        if (standardSchedule == null) {
            logger.warn("event=ai_standard_generation_empty start_date={} end_date={}", startDate, endDate);
            return null;
        }
        String metricsCorrelationId = UUID.randomUUID().toString();
        MetricsErrorMetadata errorMetadata = null;
        int standardShiftCount = standardSchedule.getConcreteShifts() == null
                ? 0
                : standardSchedule.getConcreteShifts().size();
        logger.info("event=ai_standard_generation_completed start_date={} end_date={} shifts_count={}",
                startDate, endDate, standardShiftCount);

        String aiRequestCorrelationId = UUID.randomUUID().toString();
        ToonPayloadContext toonPayloadContext = buildToonPayload(startDate,
                endDate,
                standardSchedule.getConcreteShifts(),
                aiRequestCorrelationId);

        DecisionMetricValues standardMetrics;
        try {
            standardMetrics = buildStandardMetrics(standardSchedule);
        } catch (IllegalArgumentException | PriorityScaleValidationException ex) {
            errorMetadata = buildMetricsError(errorMetadata,
                    metricsCorrelationId,
                    ERROR_STANDARD_METRICS,
                    ex);
            standardMetrics = fallbackMetrics();
        }

        CandidateData standardCandidate = buildStandardCandidate(standardSchedule, standardMetrics);
        CandidateBatch aiBatch = requestAiCandidates(toonPayloadContext,
                aiRequestCorrelationId,
                startDate,
                endDate,
                standardSchedule.getConcreteShifts());
        if (aiBatch.errorMetadata != null) {
            errorMetadata = mergeError(errorMetadata, aiBatch.errorMetadata);
        }
        List<CandidateData> aiCandidates = aiBatch.candidates;
        List<CandidateData> candidates = new ArrayList<>();
        candidates.add(standardCandidate);
        candidates.addAll(aiCandidates);
        Map<String, AiScheduleCandidateMetrics> normalizedMetrics;
        try {
            normalizedMetrics = normalizeMetrics(candidates);
        } catch (IllegalArgumentException | PriorityScaleValidationException ex) {
            errorMetadata = buildMetricsError(errorMetadata,
                    metricsCorrelationId,
                    ERROR_NORMALIZATION,
                    ex);
            normalizedMetrics = new HashMap<>();
        }

        List<AiScheduleComparisonCandidate> comparisonCandidates = new ArrayList<>();
        for (CandidateData candidate : candidates) {
            AiScheduleCandidateMetrics metrics = normalizedMetrics.get(candidate.candidateId);
            comparisonCandidates.add(new AiScheduleComparisonCandidate(
                    candidate.candidateId,
                    candidate.scheduleId,
                    candidate.type,
                    candidate.rawScheduleJson,
                    candidate.rawMetrics,
                    metrics,
                    candidate.validation.valid,
                    candidate.validation.code,
                    candidate.validation.apiMessage,
                    candidate.validation.maxRetriesReached,
                    candidate.validation.violationMessages()
            ));
        }

        AiScheduleDecisionOutcome outcome;
        try {
            outcome = selectDecisionOutcome(candidates, normalizedMetrics);
        } catch (IllegalArgumentException | PriorityScaleValidationException ex) {
            errorMetadata = buildMetricsError(errorMetadata,
                    metricsCorrelationId,
                    ERROR_DECISION,
                    ex);
            outcome = null;
        }
        AiScheduleComparisonResponseDto response;
        if (errorMetadata != null) {
            response = comparisonMapper.toDto(comparisonCandidates,
                    outcome,
                    "METRICS",
                    errorMetadata.getErrorCode(),
                    errorMetadata.getStage(),
                    errorMetadata.isRetryable());
        } else {
            response = comparisonMapper.toDto(comparisonCandidates, outcome);
        }
        cacheTransientComparison(startDate, endDate, candidates, response);
        return response;
    }

    public AiScheduleComparisonResponseDto getLatestComparison() {
        TransientComparisonState state = transientComparisonState.get();
        return state == null ? null : state.response;
    }

    public SelectionResult selectSchedule(AiScheduleSelectionRequestDto selection) {
        if (selection == null) {
            return SelectionResult.invalid("MISSING_SELECTION", "Selection payload is required.");
        }
        Long scheduleId = selection.getScheduleId();
        String candidateId = selection.getCandidateId();
        if (scheduleId == null && (candidateId == null || candidateId.trim().isEmpty())) {
            return SelectionResult.invalid("MISSING_SELECTION", "Provide a scheduleId or candidateId.");
        }
        if (scheduleId != null) {
            return persistSelectedSchedule(scheduleId);
        }
        return persistSelectedCandidate(candidateId);
    }

    public SelectionResult persistSelectedCandidate(String candidateIdOrLabel) {
        if (candidateIdOrLabel == null || candidateIdOrLabel.trim().isEmpty()) {
            return SelectionResult.invalid("MISSING_SELECTION", "Provide a candidateId.");
        }
        TransientComparisonState state = transientComparisonState.get();
        if (state == null) {
            return SelectionResult.noActiveComparison("NO_ACTIVE_COMPARISON", "No active comparison to resolve.");
        }
        CandidateData candidate = state.resolveCandidate(candidateIdOrLabel);
        if (candidate == null) {
            return SelectionResult.notFound("CANDIDATE_NOT_FOUND", "Candidate could not be resolved.");
        }
        return persistCandidate(state, candidate);
    }

    private SelectionResult persistSelectedSchedule(Long scheduleId) {
        TransientComparisonState state = transientComparisonState.get();
        if (state != null) {
            CandidateData candidate = state.resolveCandidate(scheduleId);
            if (candidate != null) {
                return persistCandidate(state, candidate);
            }
        }
        Schedule schedule = scheduleDAO.findById(scheduleId).orElse(null);
        if (schedule == null) {
            return SelectionResult.scheduleNotFound("SCHEDULE_NOT_FOUND", "Schedule could not be resolved.");
        }
        transientComparisonState.set(null);
        return SelectionResult.persisted(schedule.getId());
    }

    private SelectionResult persistCandidate(TransientComparisonState state, CandidateData candidate) {
        if (schedulerController.alreadyExistsAnotherSchedule(state.startDate, state.endDate)) {
            return SelectionResult.duplicateRange("DUPLICATE_RANGE", "Schedule already exists for this date range.");
        }
        if (!candidate.validation.valid) {
            return SelectionResult.invalid(
                    INVALID_CANDIDATE_SELECTION_ERROR_CODE,
                    INVALID_CANDIDATE_SELECTION_ERROR_MESSAGE
            );
        }
        Schedule schedule = buildScheduleForCandidate(state, candidate);
        if (schedule == null) {
            return SelectionResult.invalid("INVALID_CANDIDATE", "Unable to build the selected schedule.");
        }
        Schedule persisted = schedulerController.persistSchedule(schedule);
        if (persisted == null || persisted.getId() == null) {
            return SelectionResult.invalid("PERSIST_FAILED", "Unable to persist selected schedule.");
        }
        transientComparisonState.set(null);
        return SelectionResult.persisted(persisted.getId());
    }

    private Schedule buildScheduleForCandidate(TransientComparisonState state, CandidateData candidate) {
        if (candidate.schedule != null) {
            return candidate.schedule;
        }
        try {
            List<ConcreteShift> concreteShifts = aiScheduleConverterService.convert(candidate.rawScheduleJson);
            return new Schedule(state.startDate.toEpochDay(), state.endDate.toEpochDay(), concreteShifts);
        } catch (AiProtocolException ex) {
            return null;
        }
    }

    private void cacheTransientComparison(LocalDate startDate,
                                          LocalDate endDate,
                                          List<CandidateData> candidates,
                                          AiScheduleComparisonResponseDto response) {
        Map<String, CandidateData> mappedCandidates = new LinkedHashMap<>();
        for (CandidateData candidate : candidates) {
            mappedCandidates.put(candidate.candidateId, candidate);
        }
        transientComparisonState.set(new TransientComparisonState(startDate, endDate, mappedCandidates, response));
    }

    private ToonPayloadContext buildToonPayload(LocalDate startDate,
                                                LocalDate endDate,
                                                List<ConcreteShift> concreteShifts,
                                                String correlationId) {
        List<ConcreteShift> scopedShifts = filterShiftsInTargetPeriod(startDate, endDate, concreteShifts);
        List<Doctor> doctors = resolveEligibleDoctors(scopedShifts);
        List<Long> eligibleDoctorIds = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor != null && doctor.getId() != null) {
                eligibleDoctorIds.add(doctor.getId());
            }
        }
        List<DoctorUffaPriority> priorities = eligibleDoctorIds.isEmpty()
                ? List.of()
                : doctorUffaPriorityDAO.findByDoctor_IdIn(eligibleDoctorIds);
        List<DoctorHolidays> doctorHolidays = eligibleDoctorIds.isEmpty()
                ? List.of()
                : doctorHolidaysDAO.findByDoctor_IdIn(eligibleDoctorIds);
        AiActiveConstraintResolver.ResolveResult constraintResolveResult = aiReschedulingOrchestrationService
                .resolveActiveConstraintsWithReport(doctors, scopedShifts, false);
        List<ToonActiveConstraint> resolvedActiveConstraints = constraintResolveResult.getResolvedConstraints();
        int hardConstraintsCount = constraintResolveResult.getHardConstraintsCount();
        int softConstraintsCount = constraintResolveResult.getSoftConstraintsCount();
        int skippedConstraintsCount = constraintResolveResult.getSkippedConstraints();
        List<ToonFeedback> feedbacks = new ArrayList<>();
        logger.info("event=toon_payload_build_requested start_date={} end_date={} shifts_count={} doctors_count={} priorities_count={} holidays_count={} constraints_count={} constraints_resolved_count={} constraints_skipped_count={} constraints_hard_count={} constraints_soft_count={} feedbacks_count={}",
                startDate,
                endDate,
                scopedShifts.size(),
                doctors.size(),
                priorities.size(),
                doctorHolidays.size(),
                resolvedActiveConstraints.size() + skippedConstraintsCount,
                resolvedActiveConstraints.size(),
                skippedConstraintsCount,
                hardConstraintsCount,
                softConstraintsCount,
                feedbacks.size());

        AiReschedulingToonRequest request = aiReschedulingOrchestrationService.buildToonRequestContext(
                startDate,
                endDate,
                MODE_GENERATE,
                scopedShifts,
                doctors,
                priorities,
                doctorHolidays,
                resolvedActiveConstraints,
                feedbacks
        );

        ToonRequestContext context = request.getToonRequestContext();
        ToonBuilder builder = new ToonBuilder();
        String toonPayload = builder.build(context, ToonBuilder.SerializationMode.COMPACT);
        int feedbackCount = context.getFeedbacks() == null ? 0 : context.getFeedbacks().size();
        boolean feedbackSectionIncluded = feedbackCount > 0 && toonPayload != null && toonPayload.contains("\nfb[");
        logger.info("event=toon_payload_feedback_section correlation_id={} feedbacks_count={} feedback_section_included={} toon_payload_checksum={}",
                correlationId,
                feedbackCount,
                feedbackSectionIncluded,
                toonPayload == null ? "0" : Integer.toHexString(toonPayload.hashCode()));
        String hardCoverageBlock = hardCoveragePromptBlockBuilder.buildHardCoverageRequirementsBlock(scopedShifts);
        String roleValidationScratchpadBlock = roleValidationScratchpadPromptBlockBuilder
                .buildRoleValidationScratchpadBlock(scopedShifts, doctors);
        int generatedHardCoverageRows = countHardCoverageRows(hardCoverageBlock);
        int generatedRoleValidationScratchpadRows = countBlockRows(roleValidationScratchpadBlock);
        String roleValidationScratchpadSampleRow = extractFirstDataRow(roleValidationScratchpadBlock);
        logger.info("event=hard_coverage_block_built correlation_id={} scoped_shifts_count={} hard_coverage_rows_count={} hard_coverage_block_length={} hard_coverage_block_checksum={}",
                correlationId,
                scopedShifts.size(),
                generatedHardCoverageRows,
                hardCoverageBlock == null ? 0 : hardCoverageBlock.length(),
                hardCoverageBlock == null ? "0" : Integer.toHexString(hardCoverageBlock.hashCode()));
        logger.info("event=role_validation_scratchpad_block_built correlation_id={} scoped_shifts_count={} role_validation_scratchpad_rows_count={} role_validation_scratchpad_block_length={} role_validation_scratchpad_block_checksum={} role_validation_scratchpad_sample_row={}",
                correlationId,
                scopedShifts.size(),
                generatedRoleValidationScratchpadRows,
                roleValidationScratchpadBlock == null ? 0 : roleValidationScratchpadBlock.length(),
                roleValidationScratchpadBlock == null ? "0" : Integer.toHexString(roleValidationScratchpadBlock.hashCode()),
                roleValidationScratchpadSampleRow == null ? "none" : roleValidationScratchpadSampleRow);
        if (!scopedShifts.isEmpty() && generatedHardCoverageRows == 0) {
            logger.error("event=hard_coverage_block_empty_with_scoped_shifts correlation_id={} scoped_shifts_count={} hard_coverage_rows_count={}",
                    correlationId,
                    scopedShifts.size(),
                    generatedHardCoverageRows);
        }
        String safeToonPayload = toonPayload == null ? "" : toonPayload;
        String safeHardCoverageBlock = hardCoverageBlock == null ? "" : hardCoverageBlock;
        String safeRoleValidationScratchpadBlock = roleValidationScratchpadBlock == null ? "" : roleValidationScratchpadBlock;
        String stablePayloadWithoutScratchpad = safeToonPayload + "\n" + safeHardCoverageBlock;
        return new ToonPayloadContext(
                stablePayloadWithoutScratchpad + safeRoleValidationScratchpadBlock,
                stablePayloadWithoutScratchpad,
                safeRoleValidationScratchpadBlock,
                Collections.unmodifiableList(new ArrayList<>(scopedShifts)),
                Collections.unmodifiableList(new ArrayList<>(doctors))
        );
    }

    private int countHardCoverageRows(String hardCoverageBlock) {
        return countBlockRows(hardCoverageBlock);
    }

    private int countBlockRows(String block) {
        if (block == null || block.isBlank()) {
            return 0;
        }
        String[] lines = block.trim().split("\\R");
        return lines.length <= 1 ? 0 : lines.length - 1;
    }

    private String extractFirstDataRow(String block) {
        if (block == null || block.isBlank()) {
            return null;
        }
        String[] lines = block.trim().split("\\R");
        return lines.length <= 1 ? null : lines[1];
    }


    private List<ConcreteShift> filterShiftsInTargetPeriod(LocalDate startDate,
                                                           LocalDate endDate,
                                                           List<ConcreteShift> concreteShifts) {
        if (concreteShifts == null || concreteShifts.isEmpty()) {
            return List.of();
        }
        long startEpoch = startDate == null ? Long.MIN_VALUE : startDate.toEpochDay();
        long endEpoch = endDate == null ? Long.MAX_VALUE : endDate.toEpochDay();
        List<ConcreteShift> scopedShifts = new ArrayList<>();
        for (ConcreteShift concreteShift : concreteShifts) {
            if (concreteShift == null) {
                continue;
            }
            long shiftDate = concreteShift.getDate();
            if (shiftDate >= startEpoch && shiftDate <= endEpoch) {
                scopedShifts.add(concreteShift);
            }
        }
        return scopedShifts;
    }

    private List<Doctor> resolveEligibleDoctors(List<ConcreteShift> scopedShifts) {
        if (scopedShifts == null || scopedShifts.isEmpty()) {
            return List.of();
        }
        Map<Long, Doctor> doctorsById = new LinkedHashMap<>();
        List<Doctor> assignedDoctors = extractAssignedDoctors(scopedShifts);
        for (Doctor doctor : assignedDoctors) {
            if (doctor != null && doctor.getId() != null) {
                doctorsById.put(doctor.getId(), doctor);
            }
        }
        List<Seniority> requiredSeniorities = new ArrayList<>();
        for (ConcreteShift concreteShift : scopedShifts) {
            if (concreteShift == null || concreteShift.getShift() == null || concreteShift.getShift().getQuantityShiftSeniority() == null) {
                continue;
            }
            for (QuantityShiftSeniority quantityShiftSeniority : concreteShift.getShift().getQuantityShiftSeniority()) {
                if (quantityShiftSeniority == null || quantityShiftSeniority.getSeniorityMap() == null) {
                    continue;
                }
                for (Map.Entry<Seniority, Integer> entry : quantityShiftSeniority.getSeniorityMap().entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0 && !requiredSeniorities.contains(entry.getKey())) {
                        requiredSeniorities.add(entry.getKey());
                    }
                }
            }
        }
        if (!requiredSeniorities.isEmpty()) {
            List<Doctor> doctorsBySeniority = doctorDAO.findBySeniorities(requiredSeniorities);
            for (Doctor doctor : doctorsBySeniority) {
                if (doctor != null && doctor.getId() != null) {
                    doctorsById.put(doctor.getId(), doctor);
                }
            }
        }
        return new ArrayList<>(doctorsById.values());
    }

    private List<Doctor> extractAssignedDoctors(List<ConcreteShift> scopedShifts) {
        List<Doctor> doctors = new ArrayList<>();
        for (ConcreteShift concreteShift : scopedShifts) {
            if (concreteShift == null || concreteShift.getDoctorAssignmentList() == null) {
                continue;
            }
            for (DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
                if (assignment == null || assignment.getDoctor() == null || assignment.getDoctor().getId() == null) {
                    continue;
                }
                if (assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY
                        || assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL) {
                    doctors.add(assignment.getDoctor());
                }
            }
        }
        return doctors;
    }

    private CandidateData buildStandardCandidate(Schedule schedule, DecisionMetricValues metrics) {
        AiScheduleResponseDto responseDto = buildStandardResponseDto(schedule, metrics);
        String rawJson = serializeResponse(responseDto);
        return new CandidateData(
                "standard",
                null,
                ScheduleCandidateType.STANDARD,
                rawJson,
                metrics,
                schedule,
                CandidateValidationData.valid()
        );
    }

    private CandidateBatch requestAiCandidates(ToonPayloadContext toonPayloadContext,
                                               String batchCorrelationId,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               List<ConcreteShift> referenceShifts) {
        Map<String, AiScheduleResponse> aggregatedVariants = new LinkedHashMap<>();
        Map<String, CandidateValidationData> validationByLabel = new LinkedHashMap<>();
        Map<String, Integer> shiftRequirements = buildShiftRequirementsByShiftId(referenceShifts);
        Map<String, Map<Seniority, Integer>> shiftRoleMinima = buildShiftRoleMinimaByShiftId(referenceShifts);

        int variantMaxAttempts = aiBrokerProperties.getScheduleValidationMaxRetries() + 1;

        for (VariantDefinition definition : VARIANT_DEFINITIONS) {
            String baseInstructions = BASE_VARIANT_INSTRUCTION + " " + definition.variantInstruction;
            String attemptInstructions = baseInstructions;
            String attemptToonPayload = toonPayloadContext.fullPayload;
            String attemptScratchpadBlock = toonPayloadContext.initialScratchpadBlock;
            AiScheduleResponse selectedVariant = null;
            CandidateValidationData lastValidation = CandidateValidationData.valid();

            for (int attempt = 1; attempt <= variantMaxAttempts; attempt++) {
                if (attempt > 1 && !lastValidation.valid) {
                    attemptScratchpadBlock = roleValidationScratchpadPromptBlockBuilder
                            .buildRoleValidationScratchpadBlock(toonPayloadContext.scopedShifts, toonPayloadContext.eligibleDoctors);
                    attemptToonPayload = toonPayloadContext.payloadWithoutScratchpad
                            + (attemptScratchpadBlock == null ? "" : attemptScratchpadBlock);
                    logger.info("event=ai_variant_retry_scratchpad_refreshed correlation_id={} label={} attempt={} previous_validation_code={} scoped_shifts_count={} role_validation_scratchpad_rows_count={} role_validation_scratchpad_block_checksum={} role_validation_scratchpad_sample_row={}",
                            batchCorrelationId,
                            definition.label,
                            attempt,
                            lastValidation.code,
                            toonPayloadContext.scopedShifts.size(),
                            countBlockRows(attemptScratchpadBlock),
                            attemptScratchpadBlock == null ? "0" : Integer.toHexString(attemptScratchpadBlock.hashCode()),
                            extractFirstDataRow(attemptScratchpadBlock) == null ? "none" : extractFirstDataRow(attemptScratchpadBlock));
                }
                String correlationId = UUID.randomUUID().toString();
                AiBrokerRequest request = new AiBrokerRequest(attemptToonPayload, attemptInstructions, correlationId);
                logger.info("event=ai_broker_request_prepared correlation_id={} label={} attempt={} payload_length={} instructions_length={}",
                        correlationId,
                        definition.label,
                        attempt,
                        attemptToonPayload == null ? 0 : attemptToonPayload.length(),
                        attemptInstructions.length());

                AiTokenBudgetGuardResult budgetGuard = agentBroker.previewTokenBudget(request);
                if (!budgetGuard.isAllowed()) {
                    logger.warn("event=ai_variant_deferred correlation_id={} label={} attempt={} projected_tpm={} budget_limit={}",
                            correlationId,
                            definition.label,
                            attempt,
                            budgetGuard.getProjectedTpm(),
                            budgetGuard.getBudgetLimit());
                    selectedVariant = buildDeferredResponse(definition.label, budgetGuard);
                    lastValidation = CandidateValidationData.invalid(
                            "TOKEN_BUDGET_GUARD_DEFERRED",
                            "Variant deferred by token budget guard.",
                            "La variante AI è stata rimandata dal controllo budget token. Ridurre il payload o riprovare più tardi.",
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyMap(),
                            Collections.emptyMap(),
                            Collections.emptyList()
                    );
                    break;
                }

                AiScheduleVariantsResponse singleResponse = agentBroker.requestSchedule(request);
                AiScheduleResponse variant = singleResponse == null ? null : singleResponse.getVariant(definition.label);
                if (variant == null) {
                    throw AiProtocolException.schemaMismatch(
                            "AI response missing variant " + definition.label,
                            null
                    );
                }
                selectedVariant = variant;

                CandidateValidationData partialSuccessWarning = null;
                if (variant.getStatus() == AiStatus.PARTIAL_SUCCESS) {
                    partialSuccessWarning = CandidateValidationData.validWithWarning(
                            "PARTIAL_SUCCESS",
                            "AI returned a PARTIAL_SUCCESS variant; review uncovered shifts before final selection."
                    );
                }

                String rawJson = serializeResponse(buildAiResponseDto(variant));
                CandidateValidationData validation = validateAiCandidate(rawJson,
                        variant,
                        shiftRoleMinima,
                        definition.candidateId,
                        batchCorrelationId,
                        startDate,
                        endDate);
                if (partialSuccessWarning != null && validation.valid) {
                    validation = validation.withWarning(partialSuccessWarning.code, partialSuccessWarning.message);
                }
                lastValidation = validation;
                logDualLayerCoverageDiagnostics(batchCorrelationId, definition.label, definition.candidateId, validation);
                if (validation.valid) {
                    break;
                }

                logRoleMismatchDiagnostics(batchCorrelationId, definition.label, definition.candidateId, validation);

                if (attempt < variantMaxAttempts) {
                    attemptInstructions = buildCorrectiveVariantInstruction(baseInstructions, attempt, validation);
                    logger.warn("event=ai_variant_retry_scheduled correlation_id={} label={} attempt={} next_attempt={} reason={} message={} role_validation_scratchpad_block_checksum={} role_validation_scratchpad_sample_row={}",
                            correlationId,
                            definition.label,
                            attempt,
                            attempt + 1,
                            validation.code,
                            validation.message,
                            attemptScratchpadBlock == null || attemptScratchpadBlock.isBlank() ? "0" : Integer.toHexString(attemptScratchpadBlock.hashCode()),
                            extractFirstDataRow(attemptScratchpadBlock) == null ? "none" : extractFirstDataRow(attemptScratchpadBlock));
                }
            }

            if (!lastValidation.valid && variantMaxAttempts > 0) {
                lastValidation = lastValidation.withMaxRetriesReached(true);
            }

            if (selectedVariant == null) {
                throw AiProtocolException.schemaMismatch(
                        "AI response missing variant " + definition.label,
                        null
                );
            }
            aggregatedVariants.put(definition.label, selectedVariant);
            validationByLabel.put(definition.label, lastValidation);
        }

        logger.info("event=ai_broker_response_received correlation_id={} variants_count={}",
                batchCorrelationId,
                aggregatedVariants.size());

        List<CandidateData> candidates = new ArrayList<>();
        MetricsErrorMetadata errorMetadata = null;
        for (VariantDefinition definition : VARIANT_DEFINITIONS) {
            AiScheduleResponse variant = aggregatedVariants.get(definition.label);
            if (variant == null) {
                throw AiProtocolException.schemaMismatch(
                        "AI response missing variant " + definition.label,
                        null
                );
            }
            AiScheduleResponseDto responseDto = buildAiResponseDto(variant);
            String rawJson = serializeResponse(responseDto);
            CandidateValidationData validation = validationByLabel.get(definition.label);
            if (validation == null) {
                validation = validateAiCandidate(rawJson,
                        variant,
                        shiftRoleMinima,
                        definition.candidateId,
                        batchCorrelationId,
                        startDate,
                        endDate);
            }
            logDualLayerCoverageDiagnostics(batchCorrelationId, definition.label, definition.candidateId, validation);
            if (!validation.valid) {
                errorMetadata = buildMetricsError(errorMetadata,
                        batchCorrelationId,
                        ERROR_AI_CANDIDATE_VALIDATION,
                        new IllegalArgumentException(validation.code + ": " + validation.message));
            }
            DecisionMetricValues metrics;
            try {
                metrics = buildAiMetrics(variant, rawJson, definition.candidateId, batchCorrelationId, shiftRequirements);
            } catch (AiProtocolException | IllegalArgumentException | PriorityScaleValidationException ex) {
                errorMetadata = buildMetricsError(errorMetadata,
                        batchCorrelationId,
                        ERROR_AI_METRICS,
                        ex);
                metrics = fallbackMetrics();
            }
            candidates.add(new CandidateData(definition.candidateId,
                    null,
                    definition.type,
                    rawJson,
                    metrics,
                    null,
                    validation));
        }
        return new CandidateBatch(Collections.unmodifiableList(candidates), errorMetadata);
    }

    private String buildCorrectiveVariantInstruction(String baseInstructions,
                                                     int failedAttempt,
                                                     CandidateValidationData validation) {
        return baseInstructions
                + " Previous attempt " + failedAttempt
                + " was invalid (" + validation.code + ": " + validation.message + ")."
                + " Treat role_validation_scratchpad semantics as a backend-authoritative contract: each candidate_doctor_ids entry must reference an in-context doctor whose seniority exactly matches role_required."
                + " Strict dual-layer quota: for each (shift_id, required_role), count(ON_DUTY)=count(ON_CALL)=required_count (or hard_minimum_required_count when required_count is absent)."
                + buildValidationFailurePromptBlock(validation)
                + " Regenerate the same variant label only, fix all domain/syntax issues, and return strict JSON.";
    }

    private String buildValidationFailurePromptBlock(CandidateValidationData validation) {
        StringBuilder builder = new StringBuilder();
        builder.append(" Validation failures (prompt-safe):");
        builder.append(" [");
        builder.append("{code='").append(promptSafeText(validation.code)).append("'");
        builder.append(", expected='Produce a schedule that satisfies conversion and domain constraints'");
        builder.append(", actual='").append(promptSafeText(validation.message)).append("'");
        builder.append("}");

        appendConstraintViolationPromptDetails(builder, validation.violatedConstraints);
        appendProtocolValidationPromptDetails(builder, validation.protocolValidationDetails);
        appendRoleCoveragePromptDetails(builder, validation.roleCoverageViolations);

        builder.append("]");
        return truncateValidationFailurePrompt(builder.toString());
    }

    private void appendConstraintViolationPromptDetails(StringBuilder builder,
                                                        List<ConstraintViolationDetail> violations) {
        if (violations == null || violations.isEmpty()) {
            return;
        }
        int limit = Math.min(VALIDATION_FAILURE_PROMPT_MAX_DETAILS, violations.size());
        for (int i = 0; i < limit; i++) {
            ConstraintViolationDetail violation = violations.get(i);
            builder.append(", {constraint_id='")
                    .append(promptSafeText(violation.constraintId))
                    .append("', constraint_type='")
                    .append(promptSafeText(violation.constraintType))
                    .append("', severity='")
                    .append(promptSafeText(violation.severity.name()))
                    .append("', shift_id='")
                    .append(promptSafeText(violation.shiftId))
                    .append("', date='")
                    .append(promptSafeText(violation.date))
                    .append("', doctor_id='")
                    .append(promptSafeText(violation.doctorId))
                    .append("', expected='")
                    .append(promptSafeText(violation.expectedCondition))
                    .append("', actual='")
                    .append(promptSafeText(violation.actualCondition))
                    .append("'}");
        }
        int omitted = violations.size() - limit;
        if (omitted > 0) {
            builder.append(", {omitted_constraint_violations='")
                    .append(omitted)
                    .append("'}");
        }
    }

    private void appendProtocolValidationPromptDetails(StringBuilder builder,
                                                       List<ProtocolValidationDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        int limit = Math.min(VALIDATION_FAILURE_PROMPT_MAX_DETAILS, details.size());
        for (int i = 0; i < limit; i++) {
            ProtocolValidationDetail detail = details.get(i);
            builder.append(", {path='")
                    .append(promptSafeText(detail.path))
                    .append("', message='")
                    .append(promptSafeText(detail.message))
                    .append("'}");
        }
        int omitted = details.size() - limit;
        if (omitted > 0) {
            builder.append(", {omitted_protocol_details='")
                    .append(omitted)
                    .append("'}");
        }
    }

    private void appendRoleCoveragePromptDetails(StringBuilder builder,
                                                 List<RoleCoverageViolationDetail> violations) {
        if (violations == null || violations.isEmpty()) {
            return;
        }
        int limit = Math.min(VALIDATION_FAILURE_PROMPT_MAX_DETAILS, violations.size());
        for (int i = 0; i < limit; i++) {
            RoleCoverageViolationDetail violation = violations.get(i);
            builder.append(", {shift_id='")
                    .append(promptSafeText(violation.shiftId))
                    .append("', assignment_status='")
                    .append(promptSafeText(violation.assignmentStatus))
                    .append("', role_covered='")
                    .append(promptSafeText(violation.roleCovered))
                    .append("', required_min='")
                    .append(violation.requiredMin)
                    .append("', actual='")
                    .append(violation.actualCount)
                    .append("'}");
        }
        int omitted = violations.size() - limit;
        if (omitted > 0) {
            builder.append(", {omitted_role_coverage_violations='")
                    .append(omitted)
                    .append("'}");
        }
    }

    private String truncateValidationFailurePrompt(String promptBlock) {
        if (promptBlock == null || promptBlock.length() <= VALIDATION_FAILURE_PROMPT_MAX_CHARS) {
            return promptBlock;
        }
        String truncated = promptBlock.substring(0, VALIDATION_FAILURE_PROMPT_MAX_CHARS);
        return truncated + " ... [truncated_for_token_budget]";
    }

    private String promptSafeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "n/a";
        }
        return value.replace("\\", "\\\\")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("'", "\\'")
                .trim();
    }

    private void logRoleMismatchDiagnostics(String correlationId,
                                            String variantId,
                                            String candidateId,
                                            CandidateValidationData validation) {
        List<RoleMismatchDetail> mismatches = extractRoleMismatchDetails(validation);
        if (mismatches.isEmpty()) {
            return;
        }
        List<RoleMismatchDetail> sampled = mismatches.subList(0, Math.min(ROLE_MISMATCH_LOG_LIMIT, mismatches.size()));
        logger.warn("event=ai_candidate_role_mismatch_validation_failed correlation_id={} variant_id={} candidate_id={} role_mismatch_count={} mismatch_sample={}",
                correlationId,
                variantId,
                candidateId,
                mismatches.size(),
                sampled);
    }

    private List<RoleMismatchDetail> extractRoleMismatchDetails(CandidateValidationData validation) {
        if (validation == null || validation.protocolValidationDetails.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoleMismatchDetail> mismatches = new ArrayList<>();
        for (ProtocolValidationDetail detail : validation.protocolValidationDetails) {
            if (detail == null || detail.path == null || detail.message == null) {
                continue;
            }
            String expected = null;
            String actual = null;

            Matcher scratchpadMatcher = SCRATCHPAD_ROLE_MISMATCH_PATTERN.matcher(detail.message);
            if (scratchpadMatcher.matches()) {
                actual = scratchpadMatcher.group(1);
                expected = scratchpadMatcher.group(2);
            } else if (detail.path.contains("$.assignments[") && detail.path.endsWith(".role_covered")) {
                Matcher assignmentMatcher = ASSIGNMENT_ROLE_MISMATCH_PATTERN.matcher(detail.message);
                if (assignmentMatcher.matches()) {
                    expected = assignmentMatcher.group(1);
                    actual = "assigned role mismatch";
                }
            }

            if (expected != null || actual != null) {
                mismatches.add(new RoleMismatchDetail(detail.path, safeText(expected), safeText(actual)));
            }
        }
        return mismatches;
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "n/a" : value;
    }

    private CandidateValidationData validateAiCandidate(String rawJson,
                                                        AiScheduleResponse variant,
                                                        Map<String, Map<Seniority, Integer>> shiftRoleMinima,
                                                        String candidateId,
                                                        String correlationId,
                                                        LocalDate startDate,
                                                        LocalDate endDate) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            logger.warn("event=ai_candidate_validation_failed correlation_id={} candidate_id={} reason=EMPTY_RAW_JSON",
                    correlationId,
                    candidateId);
            return CandidateValidationData.invalid("EMPTY_RAW_JSON", "Candidate JSON payload is empty.");
        }
        try {
            List<ConcreteShift> concreteShifts = aiScheduleConverterService.convert(rawJson);
            Schedule candidateSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), concreteShifts);
            List<ConstraintViolationDetail> violatedConstraints = collectViolatedConstraints(candidateSchedule);
            if (hasHardConstraintViolation(violatedConstraints)) {
                String violationMessage = String.join(" | ", formatViolationMessages(violatedConstraints));
                logger.warn("event=ai_candidate_validation_failed correlation_id={} candidate_id={} reason={} violations_count={} message={}",
                        correlationId,
                        candidateId,
                        "DOMAIN_CONSTRAINTS_VIOLATED",
                        violatedConstraints.size(),
                        violationMessage);
                return CandidateValidationData.invalid("DOMAIN_CONSTRAINTS_VIOLATED",
                        violationMessage,
                        "Copertura non completa entro i vincoli HARD attivi: medici insufficienti per coprire tutti i turni rispettando ore periodo, riposi, turni contigui e ferie.",
                        violatedConstraints);
            }
            if (!violatedConstraints.isEmpty()) {
                String softViolationMessage = String.join(" | ", formatViolationMessages(violatedConstraints));
                logger.info("event=ai_candidate_validation_soft_constraints correlation_id={} candidate_id={} violations_count={} message={}",
                        correlationId,
                        candidateId,
                        violatedConstraints.size(),
                        softViolationMessage);
                return CandidateValidationData.validWithWarning(
                        "SOFT_DOMAIN_CONSTRAINTS_VIOLATED",
                        "Sono state rilevate violazioni di vincoli violabili; il candidato resta valido.",
                        violatedConstraints);
            }
            CandidateValidationData roleLayerValidation = validateRoleLayerMinima(variant, shiftRoleMinima);
            if (!roleLayerValidation.valid) {
                logger.warn("event=ai_candidate_validation_failed correlation_id={} candidate_id={} reason={} violations_count={} message={}",
                        correlationId,
                        candidateId,
                        ERROR_ROLE_LAYER_MINIMA,
                        roleLayerValidation.roleCoverageViolations.size(),
                        roleLayerValidation.message);
                return roleLayerValidation;
            }
            return CandidateValidationData.valid();
        } catch (AiProtocolException ex) {
            List<ProtocolValidationDetail> protocolValidationDetails = extractProtocolValidationDetails(ex.getDetails());
            List<RoleMismatchDetail> mismatchExamples = extractRoleMismatchDetailsFromProtocolValidation(protocolValidationDetails)
                    .stream()
                    .limit(VALIDATION_MISMATCH_SAMPLE_LIMIT)
                    .collect(Collectors.toList());
            logger.warn("event=ai_candidate_validation_failed correlation_id={} candidate_id={} reason={} message={}",
                    correlationId,
                    candidateId,
                    "CONVERSION_FAILED",
                    ex.getMessage());
            return CandidateValidationData.invalid("CONVERSION_FAILED",
                    ex.getMessage(),
                    "Errore tecnico durante la validazione del candidato AI. Riprovare la generazione.",
                    Collections.emptyList(),
                    protocolValidationDetails,
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    mismatchExamples);
        }
    }

    private List<RoleMismatchDetail> extractRoleMismatchDetailsFromProtocolValidation(List<ProtocolValidationDetail> protocolValidationDetails) {
        if (protocolValidationDetails == null || protocolValidationDetails.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoleMismatchDetail> mismatches = new ArrayList<>();
        for (ProtocolValidationDetail detail : protocolValidationDetails) {
            if (detail == null) {
                continue;
            }
            if (detail.path != null && detail.path.contains("role_covered")
                    || detail.message != null && detail.message.toLowerCase().contains("seniority")) {
                mismatches.add(new RoleMismatchDetail(
                        safeText(detail.path),
                        "role_covered must match required seniority",
                        safeText(detail.message)
                ));
            }
        }
        return mismatches;
    }

    private CandidateValidationData validateRoleLayerMinima(AiScheduleResponse variant,
                                                            Map<String, Map<Seniority, Integer>> shiftRoleMinima) {
        if (variant == null || shiftRoleMinima == null || shiftRoleMinima.isEmpty()) {
            return CandidateValidationData.valid();
        }

        Map<String, Integer> assignmentCounts = new HashMap<>();
        Map<String, Integer> requiredSlotsByKey = new LinkedHashMap<>();
        List<AiAssignment> assignments = variant.getAssignments() == null ? Collections.emptyList() : variant.getAssignments();
        for (AiAssignment assignment : assignments) {
            if (assignment == null
                    || assignment.getShiftId() == null
                    || assignment.getShiftId().trim().isEmpty()
                    || assignment.getAssignmentStatus() == null
                    || assignment.getRoleCovered() == null) {
                continue;
            }
            if (assignment.getAssignmentStatus() != ConcreteShiftDoctorStatus.ON_DUTY
                    && assignment.getAssignmentStatus() != ConcreteShiftDoctorStatus.ON_CALL) {
                continue;
            }
            String key = toRoleStatusCountKey(assignment.getShiftId().trim(), assignment.getAssignmentStatus(), assignment.getRoleCovered());
            assignmentCounts.merge(key, 1, Integer::sum);
        }

        List<RoleCoverageViolationDetail> violations = new ArrayList<>();
        for (Map.Entry<String, Map<Seniority, Integer>> shiftEntry : shiftRoleMinima.entrySet()) {
            String shiftId = shiftEntry.getKey();
            Map<Seniority, Integer> minimaByRole = shiftEntry.getValue();
            if (minimaByRole == null) {
                continue;
            }
            for (Map.Entry<Seniority, Integer> roleEntry : minimaByRole.entrySet()) {
                Seniority role = roleEntry.getKey();
                int requiredMin = Math.max(roleEntry.getValue() == null ? 0 : roleEntry.getValue(), 0);
                if (requiredMin == 0) {
                    continue;
                }
                for (ConcreteShiftDoctorStatus status : List.of(ConcreteShiftDoctorStatus.ON_DUTY, ConcreteShiftDoctorStatus.ON_CALL)) {
                    String key = toRoleStatusCountKey(shiftId, status, role);
                    requiredSlotsByKey.put(key, requiredMin);
                    int assigned = assignmentCounts.getOrDefault(key, 0);
                    if (assigned < requiredMin) {
                        violations.add(new RoleCoverageViolationDetail(shiftId, status.name(), role.name(), requiredMin, assigned));
                    }
                }
            }
        }

        List<RoleMismatchDetail> mismatchSamples = extractRoleMismatchDetailsFromCoverageViolations(violations)
                .stream()
                .limit(VALIDATION_MISMATCH_SAMPLE_LIMIT)
                .collect(Collectors.toList());

        if (violations.isEmpty()) {
            return CandidateValidationData.valid(requiredSlotsByKey,
                    buildAssignedCoverageByKey(requiredSlotsByKey.keySet(), assignmentCounts),
                    Collections.emptyList(),
                    mismatchSamples);
        }

        String message = violations.stream()
                .map(RoleCoverageViolationDetail::toCompactMessage)
                .collect(Collectors.joining(" | "));
        return CandidateValidationData.invalid(ERROR_ROLE_LAYER_MINIMA,
                message,
                "Copertura non completa sui requisiti ruolo/stato richiesti: medici insufficienti sui livelli minimi richiesti.",
                Collections.emptyList(),
                Collections.emptyList(),
                violations,
                requiredSlotsByKey,
                buildAssignedCoverageByKey(requiredSlotsByKey.keySet(), assignmentCounts),
                mismatchSamples);
    }

    private Map<String, Integer> buildAssignedCoverageByKey(Set<String> keys, Map<String, Integer> assignmentCounts) {
        Map<String, Integer> assignedByKey = new LinkedHashMap<>();
        if (keys == null || keys.isEmpty()) {
            return assignedByKey;
        }
        for (String key : keys) {
            assignedByKey.put(key, assignmentCounts.getOrDefault(key, 0));
        }
        return assignedByKey;
    }

    private List<RoleMismatchDetail> extractRoleMismatchDetailsFromCoverageViolations(List<RoleCoverageViolationDetail> violations) {
        if (violations == null || violations.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoleMismatchDetail> mismatchDetails = new ArrayList<>();
        for (RoleCoverageViolationDetail violation : violations) {
            mismatchDetails.add(new RoleMismatchDetail(
                    "$.assignments",
                    "required>= " + violation.requiredMin + " for " + violation.assignmentStatus + "/" + violation.roleCovered,
                    "assigned=" + violation.actualCount + " for shift_id=" + violation.shiftId
            ));
        }
        return mismatchDetails;
    }

    private void logDualLayerCoverageDiagnostics(String correlationId,
                                                 String variantId,
                                                 String candidateId,
                                                 CandidateValidationData validation) {
        if (validation == null) {
            return;
        }
        logger.info("event=ai_candidate_dual_layer_coverage_validation correlation_id={} variant_id={} candidate_id={} dual_layer_coverage_valid={} required_slots_count={} assigned_slots_count={} missing_slots_count={} metadata={}",
                correlationId,
                variantId,
                candidateId,
                validation.dualLayerCoverageValid,
                validation.requiredSlotsByShiftRoleLayer.size(),
                validation.assignedSlotsByShiftRoleLayer.size(),
                validation.roleCoverageViolations.size(),
                serializeValidationMetadata(validation));
    }

    private String serializeValidationMetadata(CandidateValidationData validation) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("dual_layer_coverage_valid", validation.dualLayerCoverageValid);
        metadata.put("required_slots_per_shift_role_layer", validation.requiredSlotsByShiftRoleLayer);
        metadata.put("assigned_slots_per_shift_role_layer", validation.assignedSlotsByShiftRoleLayer);
        metadata.put("missing_slots_summary", validation.roleCoverageViolations.stream()
                .map(RoleCoverageViolationDetail::toCompactMessage)
                .collect(Collectors.toList()));
        metadata.put("mismatch_examples", validation.mismatchExamples.stream()
                .map(RoleMismatchDetail::toString)
                .collect(Collectors.toList()));
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return metadata.toString();
        }
    }

    private String toRoleStatusCountKey(String shiftId, ConcreteShiftDoctorStatus status, Seniority roleCovered) {
        return shiftId + "|" + status.name() + "|" + roleCovered.name();
    }

    private List<ProtocolValidationDetail> extractProtocolValidationDetails(List<ValidationError> details) {
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProtocolValidationDetail> protocolValidationDetails = new ArrayList<>();
        for (ValidationError detail : details) {
            if (detail == null) {
                continue;
            }
            protocolValidationDetails.add(new ProtocolValidationDetail(detail.getPath(), detail.getMessage()));
        }
        return protocolValidationDetails;
    }

    private List<String> formatViolationMessages(List<ConstraintViolationDetail> violations) {
        List<String> messages = new ArrayList<>();
        for (ConstraintViolationDetail violation : violations) {
            messages.add(violation.severity + " " + violation.constraintType + "[" + violation.constraintId + "]: " + violation.actualCondition);
        }
        return messages;
    }

    private boolean hasHardConstraintViolation(List<ConstraintViolationDetail> violations) {
        if (violations == null || violations.isEmpty()) {
            return false;
        }
        for (ConstraintViolationDetail violation : violations) {
            if (violation != null && violation.severity == ConstraintViolationSeverity.HARD) {
                return true;
            }
        }
        return false;
    }

    private List<ConstraintViolationDetail> collectViolatedConstraints(Schedule candidateSchedule) {
        if (candidateSchedule == null || candidateSchedule.getConcreteShifts() == null) {
            return Collections.emptyList();
        }
        List<Constraint> constraints = constraintDAO.findAll();

        List<ConstraintViolationDetail> violations = new ArrayList<>();
        List<Holiday> holidays = holidayDAO.findAll();
        Map<Long, DoctorHolidays> doctorHolidaysByDoctorId = new HashMap<>();
        for (DoctorHolidays doctorHolidays : doctorHolidaysDAO.findAll()) {
            if (doctorHolidays == null || doctorHolidays.getDoctor() == null || doctorHolidays.getDoctor().getId() == null) {
                continue;
            }
            doctorHolidaysByDoctorId.put(doctorHolidays.getDoctor().getId(), doctorHolidays);
        }

        Map<Long, DoctorUffaPriority> prioritiesByDoctorId = new HashMap<>();
        for (DoctorUffaPriority priority : doctorUffaPriorityDAO.findAll()) {
            if (priority == null || priority.getDoctor() == null || priority.getDoctor().getId() == null) {
                continue;
            }
            // Use an empty per-doctor assignment cache for validation and fill it incrementally
            // while iterating assignments. Binding this object to candidateSchedule would preload
            // every assignment for that doctor and make turni-turno constraints (e.g. ubiquity)
            // compare each assignment against itself, producing false positives.
            DoctorUffaPriority validationPriority = new DoctorUffaPriority(priority.getDoctor());
            validationPriority.setSchedule(candidateSchedule);
            validationPriority.setAssegnazioniTurnoCache(new ArrayList<>());
            validationPriority.setGeneralPriority(priority.getGeneralPriority());
            validationPriority.setPartialGeneralPriority(priority.getPartialGeneralPriority());
            validationPriority.setLongShiftPriority(priority.getLongShiftPriority());
            validationPriority.setPartialLongShiftPriority(priority.getPartialLongShiftPriority());
            validationPriority.setNightPriority(priority.getNightPriority());
            validationPriority.setPartialNightPriority(priority.getPartialNightPriority());
            prioritiesByDoctorId.put(priority.getDoctor().getId(), validationPriority);
        }

        for (ConcreteShift concreteShift : candidateSchedule.getConcreteShifts()) {
            if (concreteShift == null || concreteShift.getDoctorAssignmentList() == null) {
                continue;
            }
            if (concreteShift.getShift() == null) {
                violations.add(ConstraintViolationDetail.malformedShift(
                        concreteShift,
                        "shift",
                        "Missing required field: shift"
                ));
                continue;
            }
            if (concreteShift.getShift().getDuration() == null) {
                violations.add(ConstraintViolationDetail.malformedShift(
                        concreteShift,
                        "duration",
                        "Missing required field: shift.duration"
                ));
                continue;
            }
            for (DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
                if (assignment == null || assignment.getDoctor() == null || assignment.getDoctor().getId() == null) {
                    continue;
                }
                if (assignment.getConcreteShiftDoctorStatus() != ConcreteShiftDoctorStatus.ON_DUTY
                        && assignment.getConcreteShiftDoctorStatus() != ConcreteShiftDoctorStatus.ON_CALL) {
                    continue;
                }

                Long doctorId = assignment.getDoctor().getId();
                DoctorUffaPriority doctorPriority = prioritiesByDoctorId.get(doctorId);
                if (doctorPriority == null) {
                    doctorPriority = new DoctorUffaPriority(assignment.getDoctor());
                    doctorPriority.setSchedule(candidateSchedule);
                    doctorPriority.setAssegnazioniTurnoCache(new ArrayList<>());
                    prioritiesByDoctorId.put(doctorId, doctorPriority);
                }

                ContextConstraint context = new ContextConstraint(
                        doctorPriority,
                        concreteShift,
                        doctorHolidaysByDoctorId.get(doctorId),
                        holidays
                );
                for (Constraint constraint : constraints) {
                    try {
                        constraint.verifyConstraint(context);
                    } catch (ViolatedConstraintException ex) {
                        String message = ex.getMessage() == null ? "violated" : ex.getMessage();
                        violations.add(ConstraintViolationDetail.of(
                                constraint,
                                concreteShift,
                                assignment.getDoctor(),
                                "Constraint validation failed for the current assignment",
                                message
                        ));
                    } catch (RuntimeException ex) {
                        String exceptionClass = sanitizeExceptionClass(ex);
                        String exceptionMessage = sanitizeExceptionMessage(ex);
                        logger.warn("event=ai_candidate_validation_failed reason=CONSTRAINT_EXECUTION_ERROR "
                                        + "constraint_type={} constraint_id={} doctor_id={} shift_id={} exception_class={} "
                                        + "exception_message={}",
                                constraint == null ? "n/a" : constraint.getClass().getSimpleName(),
                                constraint == null || constraint.getId() == null ? "n/a" : constraint.getId(),
                                assignment.getDoctor() == null || assignment.getDoctor().getId() == null
                                        ? "n/a"
                                        : assignment.getDoctor().getId(),
                                concreteShift.getShift() == null || concreteShift.getShift().getId() == null
                                        ? "n/a"
                                        : concreteShift.getShift().getId(),
                                exceptionClass,
                                exceptionMessage);
                        violations.add(ConstraintViolationDetail.constraintExecutionFailure(
                                constraint,
                                concreteShift,
                                assignment.getDoctor(),
                                exceptionClass,
                                exceptionMessage
                        ));
                    }
                }

                doctorPriority.addConcreteShift(concreteShift);
            }
        }
        return violations;
    }

    private String sanitizeExceptionClass(RuntimeException ex) {
        if (ex == null || ex.getClass() == null || ex.getClass().getSimpleName() == null
                || ex.getClass().getSimpleName().trim().isEmpty()) {
            return "RuntimeException";
        }
        return ex.getClass().getSimpleName().trim();
    }

    private String sanitizeExceptionMessage(RuntimeException ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().trim().isEmpty()) {
            return "n/a";
        }
        String sanitized = ex.getMessage()
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .trim();
        return sanitized.length() > 256 ? sanitized.substring(0, 256) : sanitized;
    }

    private AiScheduleResponse buildDeferredResponse(String label, AiTokenBudgetGuardResult budgetGuard) {
        String reasoning = "Deferred due to token budget guard for label " + label
                + ". projected_tpm=" + budgetGuard.getProjectedTpm()
                + ", budget_limit=" + budgetGuard.getBudgetLimit();
        AiMetrics metrics = new AiMetrics(0.0, new AiUffaBalance(new AiStdDev(0.0, 0.0)), 0);
        AiMetadata metadata = new AiMetadata(reasoning, "token-budget-guard", 0.0, metrics);
        return new AiScheduleResponse(AiStatus.PARTIAL_SUCCESS,
                metadata,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    private DecisionMetricValues buildStandardMetrics(Schedule schedule) {
        double coverage = computeCoverage(schedule.getConcreteShifts());
        PriorityDeltaStats deltaStats = computePriorityDeltaStats(
                schedule.getDoctorUffaPrioritiesSnapshot(),
                schedule.getDoctorUffaPriorityList()
        );
        double uffaBalance = computeUffaBalanceImprovement(
                schedule.getDoctorUffaPrioritiesSnapshot(),
                schedule.getDoctorUffaPriorityList()
        );
        logger.info("event=metrics_standard_calculated coverage={} uffa_balance={} delta_mean={} delta_variance={}",
                coverage,
                uffaBalance,
                deltaStats.mean,
                deltaStats.variance);
        return new DecisionMetricValues(
                coverage,
                uffaBalance,
                deltaStats.mean,
                deltaStats.variance
        );
    }

    private DecisionMetricValues buildAiMetrics(AiScheduleResponse response,
                                                String rawJson,
                                                String candidateId,
                                                String correlationId,
                                                Map<String, Integer> shiftRequirements) {
        DecisionMetricValues aiProvided = buildAiProvidedMetrics(response);
        DecisionMetricValues serverComputed = buildAiMetricsFromAssignments(response, rawJson, shiftRequirements);
        compareAiMetrics(aiProvided, serverComputed, candidateId, correlationId);
        logger.info("event=metrics_ai_calculated candidate_id={} coverage={} uffa_balance={} delta_mean={} delta_variance={}",
                candidateId,
                serverComputed.getCoverage(),
                serverComputed.getUffaBalance(),
                serverComputed.getUpDelta(),
                serverComputed.getVarianceDelta());
        return serverComputed;
    }

    private DecisionMetricValues buildAiMetricsFromAssignments(AiScheduleResponse response,
                                                               String rawJson,
                                                               Map<String, Integer> shiftRequirements) {
        List<ConcreteShift> concreteShifts = aiScheduleConverterService.convert(rawJson);
        double coverage = computeCoverageFromAiResponse(response, shiftRequirements);
        double uffaBalance = computeUffaBalanceFromConcreteShifts(concreteShifts);
        PriorityDeltaStats deltaStats = computeUffaDeltaStats(response == null ? null : response.getUffaDelta());
        if (response == null || response.getUffaDelta() == null || response.getUffaDelta().isEmpty()) {
            deltaStats = computeLoadDeltaStatsFromConcreteShifts(concreteShifts);
        }
        return new DecisionMetricValues(
                coverage,
                uffaBalance,
                deltaStats.mean,
                deltaStats.variance
        );
    }

    private DecisionMetricValues buildAiProvidedMetrics(AiScheduleResponse response) {
        if (response == null) {
            return null;
        }
        AiMetadata metadata = response.getMetadata();
        AiMetrics metrics = metadata != null ? metadata.getMetrics() : null;
        if (metrics == null) {
            return null;
        }
        Double coverage = metrics.getCoveragePercent() != null ? normalizeCoverageMetric(metrics.getCoveragePercent()) : 0.0;
        Double uffaBalance = hasUffaBalanceMetric(metrics) ? resolveUffaBalanceImprovement(metrics) : 0.0;
        PriorityDeltaStats deltaStats = computeUffaDeltaStats(response.getUffaDelta());
        if (response.getUffaDelta() == null || response.getUffaDelta().isEmpty()) {
            deltaStats = new PriorityDeltaStats(0.0, 0.0);
        }
        return new DecisionMetricValues(
                coverage,
                uffaBalance,
                deltaStats.mean,
                deltaStats.variance
        );
    }

    private boolean hasUffaBalanceMetric(AiMetrics metrics) {
        if (metrics == null || metrics.getUffaBalance() == null || metrics.getUffaBalance().getNightShiftStdDev() == null) {
            return false;
        }
        AiStdDev stdDev = metrics.getUffaBalance().getNightShiftStdDev();
        return stdDev.getInitial() != null && stdDev.getFinalValue() != null;
    }

    private void compareAiMetrics(DecisionMetricValues aiProvided,
                                  DecisionMetricValues serverComputed,
                                  String candidateId,
                                  String correlationId) {
        if (aiProvided == null) {
            logger.warn("event=metrics_ai_missing candidate_id={} correlation_id={}",
                    candidateId,
                    correlationId);
            return;
        }
        boolean coverageMismatch = !approximatelyEqual(aiProvided.getCoverage(), serverComputed.getCoverage());
        boolean uffaBalanceMismatch = !approximatelyEqual(aiProvided.getUffaBalance(), serverComputed.getUffaBalance());
        if (coverageMismatch || uffaBalanceMismatch) {
            logger.warn("event=metrics_ai_mismatch candidate_id={} correlation_id={} coverage_ai={} coverage_server={} uffa_balance_ai={} uffa_balance_server={}",
                    candidateId,
                    correlationId,
                    aiProvided.getCoverage(),
                    serverComputed.getCoverage(),
                    aiProvided.getUffaBalance(),
                    serverComputed.getUffaBalance());
        }
    }

    private boolean approximatelyEqual(Double left, Double right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return Math.abs(left - right) <= METRICS_COMPARISON_TOLERANCE;
    }

    private double resolveUffaBalanceImprovement(AiMetrics metrics) {
        if (metrics == null || metrics.getUffaBalance() == null) {
            return 0.0;
        }
        AiUffaBalance balance = metrics.getUffaBalance();
        AiStdDev stdDev = balance.getNightShiftStdDev();
        if (stdDev == null || stdDev.getInitial() == null || stdDev.getFinalValue() == null) {
            return 0.0;
        }
        return stdDev.getInitial() - stdDev.getFinalValue();
    }

    private PriorityDeltaStats computePriorityDeltaStats(List<DoctorUffaPrioritySnapshot> snapshots,
                                                         List<DoctorUffaPriority> current) {
        if (snapshots == null || current == null) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        Map<Long, Integer> previous = new HashMap<>();
        for (DoctorUffaPrioritySnapshot snapshot : snapshots) {
            if (snapshot.getDoctor() != null && snapshot.getDoctor().getId() != null) {
                previous.put(snapshot.getDoctor().getId(), snapshot.getGeneralPriority());
            }
        }
        List<Double> deltas = new ArrayList<>();
        for (DoctorUffaPriority priority : current) {
            if (priority.getDoctor() == null || priority.getDoctor().getId() == null) {
                continue;
            }
            Integer previousValue = previous.get(priority.getDoctor().getId());
            if (previousValue == null) {
                continue;
            }
            deltas.add((double) priority.getGeneralPriority() - previousValue);
        }
        if (deltas.isEmpty()) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        return new PriorityDeltaStats(MetricAggregationUtils.mean(deltas), MetricAggregationUtils.variance(deltas));
    }

    private double computeUffaBalanceImprovement(List<DoctorUffaPrioritySnapshot> snapshots,
                                                 List<DoctorUffaPriority> current) {
        if (snapshots == null || current == null || snapshots.isEmpty() || current.isEmpty()) {
            return 0.0;
        }
        List<Integer> previousNight = new ArrayList<>();
        for (DoctorUffaPrioritySnapshot snapshot : snapshots) {
            previousNight.add(snapshot.getNightPriority());
        }
        List<Integer> currentNight = new ArrayList<>();
        for (DoctorUffaPriority priority : current) {
            currentNight.add(priority.getNightPriority());
        }
        double initialStdDev = computeStdDev(previousNight);
        double finalStdDev = computeStdDev(currentNight);
        return initialStdDev - finalStdDev;
    }

    private PriorityDeltaStats computeUffaDeltaStats(List<AiUffaDelta> deltas) {
        if (deltas == null || deltas.isEmpty()) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        List<Double> values = new ArrayList<>();
        for (AiUffaDelta delta : deltas) {
            if (delta != null && delta.getPoints() != null) {
                values.add(delta.getPoints().doubleValue());
            }
        }
        if (values.isEmpty()) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        return new PriorityDeltaStats(MetricAggregationUtils.mean(values), MetricAggregationUtils.variance(values));
    }

    private double computeCoverageFromAiResponse(AiScheduleResponse response, Map<String, Integer> shiftRequirements) {
        if (response == null || shiftRequirements == null || shiftRequirements.isEmpty()) {
            return 0.0;
        }

        Map<String, Integer> assignedByShift = new HashMap<>();
        if (response.getAssignments() != null) {
            for (AiAssignment assignment : response.getAssignments()) {
                if (assignment == null || assignment.getShiftId() == null || assignment.getShiftId().trim().isEmpty()) {
                    continue;
                }
                String shiftId = assignment.getShiftId().trim();
                assignedByShift.put(shiftId, assignedByShift.getOrDefault(shiftId, 0) + 1);
            }
        }

        int totalRequired = 0;
        int totalAssigned = 0;
        for (Map.Entry<String, Integer> entry : shiftRequirements.entrySet()) {
            int required = entry.getValue() == null ? 0 : entry.getValue();
            totalRequired += required;
            int assigned = assignedByShift.getOrDefault(entry.getKey(), 0);
            totalAssigned += Math.min(required, assigned);
        }

        if (totalRequired <= 0) {
            return 0.0;
        }

        return (double) totalAssigned / totalRequired;
    }

    private Map<String, Integer> buildShiftRequirementsByShiftId(List<ConcreteShift> referenceShifts) {
        Map<String, Integer> requirements = new HashMap<>();
        if (referenceShifts == null) {
            return requirements;
        }
        for (ConcreteShift shift : referenceShifts) {
            if (shift == null) {
                continue;
            }
            String shiftId = ToonBuilder.shiftIdFor(shift);
            int required = computeShiftRequirement(shift.getShift());
            if (required <= 0) {
                required = countAssignedDoctors(shift);
            }
            requirements.merge(shiftId, required, Math::max);
        }
        return requirements;
    }

    private Map<String, Map<Seniority, Integer>> buildShiftRoleMinimaByShiftId(List<ConcreteShift> referenceShifts) {
        Map<String, Map<Seniority, Integer>> minimaByShift = new HashMap<>();
        if (referenceShifts == null) {
            return minimaByShift;
        }
        for (ConcreteShift concreteShift : referenceShifts) {
            if (concreteShift == null) {
                continue;
            }
            String shiftId = ToonBuilder.shiftIdFor(concreteShift);
            Map<Seniority, Integer> minima = minimaByShift.computeIfAbsent(shiftId, ignored -> new HashMap<>());
            Shift shift = concreteShift.getShift();
            if (shift == null || shift.getQuantityShiftSeniority() == null) {
                continue;
            }
            for (QuantityShiftSeniority qss : shift.getQuantityShiftSeniority()) {
                if (qss == null || qss.getSeniorityMap() == null) {
                    continue;
                }
                for (Map.Entry<Seniority, Integer> entry : qss.getSeniorityMap().entrySet()) {
                    if (entry.getKey() == null) {
                        continue;
                    }
                    int value = entry.getValue() == null ? 0 : Math.max(entry.getValue(), 0);
                    minima.merge(entry.getKey(), value, Integer::sum);
                }
            }
        }
        return minimaByShift;
    }

    private PriorityDeltaStats computeLoadDeltaStatsFromConcreteShifts(List<ConcreteShift> concreteShifts) {
        if (concreteShifts == null || concreteShifts.isEmpty()) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        Map<Long, Integer> loadsByDoctor = new HashMap<>();
        for (ConcreteShift concreteShift : concreteShifts) {
            if (concreteShift == null || concreteShift.getDoctorAssignmentList() == null) {
                continue;
            }
            for (DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
                if (assignment == null || assignment.getDoctor() == null || assignment.getDoctor().getId() == null) {
                    continue;
                }
                ConcreteShiftDoctorStatus status = assignment.getConcreteShiftDoctorStatus();
                if (status != ConcreteShiftDoctorStatus.ON_CALL && status != ConcreteShiftDoctorStatus.ON_DUTY) {
                    continue;
                }
                Long doctorId = assignment.getDoctor().getId();
                loadsByDoctor.put(doctorId, loadsByDoctor.getOrDefault(doctorId, 0) + 1);
            }
        }
        if (loadsByDoctor.isEmpty()) {
            return new PriorityDeltaStats(0.0, 0.0);
        }
        List<Double> values = new ArrayList<>();
        for (Integer value : loadsByDoctor.values()) {
            values.add(value.doubleValue());
        }
        return new PriorityDeltaStats(MetricAggregationUtils.mean(values), MetricAggregationUtils.variance(values));
    }

    private double computeUffaBalanceFromConcreteShifts(List<ConcreteShift> concreteShifts) {
        if (concreteShifts == null || concreteShifts.isEmpty()) {
            return 0.0;
        }
        Map<Long, Integer> allAssignmentsByDoctor = new HashMap<>();
        Map<Long, Integer> nightAssignmentsByDoctor = new HashMap<>();
        for (ConcreteShift concreteShift : concreteShifts) {
            if (concreteShift == null || concreteShift.getDoctorAssignmentList() == null) {
                continue;
            }
            boolean isNightShift = concreteShift.getShift() != null
                    && concreteShift.getShift().getTimeSlot() != null
                    && "NIGHT".equals(concreteShift.getShift().getTimeSlot().name());
            for (DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
                if (assignment == null || assignment.getDoctor() == null || assignment.getDoctor().getId() == null) {
                    continue;
                }
                ConcreteShiftDoctorStatus status = assignment.getConcreteShiftDoctorStatus();
                if (status != ConcreteShiftDoctorStatus.ON_CALL && status != ConcreteShiftDoctorStatus.ON_DUTY) {
                    continue;
                }
                Long doctorId = assignment.getDoctor().getId();
                allAssignmentsByDoctor.put(doctorId, allAssignmentsByDoctor.getOrDefault(doctorId, 0) + 1);
                if (isNightShift) {
                    nightAssignmentsByDoctor.put(doctorId, nightAssignmentsByDoctor.getOrDefault(doctorId, 0) + 1);
                }
            }
        }
        if (allAssignmentsByDoctor.isEmpty()) {
            return 0.0;
        }
        List<Integer> nightLoads = new ArrayList<>();
        for (Long doctorId : allAssignmentsByDoctor.keySet()) {
            nightLoads.add(nightAssignmentsByDoctor.getOrDefault(doctorId, 0));
        }
        return -computeStdDev(nightLoads);
    }

    private double normalizeCoverageMetric(Double coveragePercent) {
        if (coveragePercent == null || coveragePercent.isNaN() || coveragePercent.isInfinite()) {
            return 0.0;
        }
        double value = coveragePercent;
        if (value > 1.0) {
            value = value / 100.0;
        }
        return clampUnit(value);
    }

    private double computeCoverage(List<ConcreteShift> concreteShifts) {
        if (concreteShifts == null || concreteShifts.isEmpty()) {
            logger.info("event=coverage_computed total_required=0 total_assigned=0 coverage=0.0");
            return 0.0;
        }
        int totalRequired = 0;
        int totalAssigned = 0;
        for (ConcreteShift shift : concreteShifts) {
            int requiredPerRoleMinima = computeShiftRequirementPerRoleMinima(shift.getShift());
            int requiredOnDuty = requiredPerRoleMinima;
            int requiredOnCall = requiredPerRoleMinima;
            int required = requiredOnDuty + requiredOnCall;

            int assignedOnDuty = countAssignedDoctors(shift, ConcreteShiftDoctorStatus.ON_DUTY);
            int assignedOnCall = countAssignedDoctors(shift, ConcreteShiftDoctorStatus.ON_CALL);

            int coveredOnDuty = Math.min(requiredOnDuty, assignedOnDuty);
            int coveredOnCall = Math.min(requiredOnCall, assignedOnCall);

            totalRequired += required;
            totalAssigned += coveredOnDuty + coveredOnCall;
        }
        if (totalRequired == 0) {
            logger.info("event=coverage_computed total_required=0 total_assigned={} coverage=1.0", totalAssigned);
            return 1.0;
        }
        double coverage = (double) totalAssigned / totalRequired;
        logger.info("event=coverage_computed total_required={} total_assigned={} coverage={}",
                totalRequired,
                totalAssigned,
                coverage);
        return coverage;
    }

    private int computeShiftRequirement(Shift shift) {
        int requiredPerRoleMinima = computeShiftRequirementPerRoleMinima(shift);
        int requiredOnDuty = requiredPerRoleMinima;
        int requiredOnCall = requiredPerRoleMinima;
        return requiredOnDuty + requiredOnCall;
    }

    private int computeShiftRequirementPerRoleMinima(Shift shift) {
        if (shift == null || shift.getQuantityShiftSeniority() == null) {
            return 0;
        }
        int required = 0;
        for (QuantityShiftSeniority qss : shift.getQuantityShiftSeniority()) {
            if (qss.getSeniorityMap() == null) {
                continue;
            }
            for (Integer value : qss.getSeniorityMap().values()) {
                if (value != null) {
                    required += value;
                }
            }
        }
        return required;
    }

    private int countAssignedDoctors(ConcreteShift shift) {
        return countAssignedDoctors(shift, ConcreteShiftDoctorStatus.ON_DUTY)
                + countAssignedDoctors(shift, ConcreteShiftDoctorStatus.ON_CALL);
    }

    private int countAssignedDoctors(ConcreteShift shift, ConcreteShiftDoctorStatus statusToCount) {
        if (shift == null || shift.getDoctorAssignmentList() == null) {
            return 0;
        }
        int count = 0;
        for (DoctorAssignment assignment : shift.getDoctorAssignmentList()) {
            if (assignment == null) {
                continue;
            }
            ConcreteShiftDoctorStatus status = assignment.getConcreteShiftDoctorStatus();
            if (status == statusToCount) {
                count++;
            }
        }
        return count;
    }

    private double computeStdDev(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        double mean = 0.0;
        for (Integer value : values) {
            mean += value;
        }
        mean /= values.size();
        double variance = 0.0;
        for (Integer value : values) {
            double delta = value - mean;
            variance += delta * delta;
        }
        variance /= values.size();
        return Math.sqrt(variance);
    }

    private AiScheduleResponseDto buildStandardResponseDto(Schedule schedule, DecisionMetricValues metrics) {
        AiScheduleResponseDto dto = new AiScheduleResponseDto();
        dto.status = AiStatus.SUCCESS;
        dto.metadata = new AiMetadataDto();
        dto.metadata.reasoning = "STANDARD_SCHEDULER";
        dto.metadata.optimalityScore = null;
        dto.metadata.metrics = new AiMetricsDto();
        dto.metadata.metrics.coveragePercent = metrics.getCoverage();
        dto.metadata.metrics.softViolationsCount = schedule.getViolatedConstraints() != null
                ? schedule.getViolatedConstraints().size()
                : 0;
        dto.metadata.metrics.uffaBalance = buildStandardUffaBalance(schedule);
        dto.assignments = buildAssignmentsFromSchedule(schedule);
        dto.uncoveredShifts = buildUncoveredShifts(schedule);
        dto.uffaDelta = buildUffaDelta(schedule);
        return dto;
    }

    private AiUffaBalanceDto buildStandardUffaBalance(Schedule schedule) {
        AiUffaBalanceDto balance = new AiUffaBalanceDto();
        AiStdDevDto stdDev = new AiStdDevDto();
        stdDev.initial = computeStdDevFromSnapshot(schedule.getDoctorUffaPrioritiesSnapshot());
        stdDev.finalValue = computeStdDevFromCurrent(schedule.getDoctorUffaPriorityList());
        balance.nightShiftStdDev = stdDev;
        return balance;
    }

    private double computeStdDevFromSnapshot(List<DoctorUffaPrioritySnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return 0.0;
        }
        List<Integer> values = new ArrayList<>();
        for (DoctorUffaPrioritySnapshot snapshot : snapshots) {
            values.add(snapshot.getNightPriority());
        }
        return computeStdDev(values);
    }

    private double computeStdDevFromCurrent(List<DoctorUffaPriority> current) {
        if (current == null || current.isEmpty()) {
            return 0.0;
        }
        List<Integer> values = new ArrayList<>();
        for (DoctorUffaPriority priority : current) {
            values.add(priority.getNightPriority());
        }
        return computeStdDev(values);
    }

    private List<AiAssignmentDto> buildAssignmentsFromSchedule(Schedule schedule) {
        List<AiAssignmentDto> assignments = new ArrayList<>();
        if (schedule.getConcreteShifts() == null) {
            return assignments;
        }
        for (ConcreteShift shift : schedule.getConcreteShifts()) {
            if (shift.getDoctorAssignmentList() == null) {
                continue;
            }
            for (DoctorAssignment assignment : shift.getDoctorAssignmentList()) {
                if (assignment == null || assignment.getDoctor() == null) {
                    continue;
                }
                ConcreteShiftDoctorStatus status = assignment.getConcreteShiftDoctorStatus();
                if (status != ConcreteShiftDoctorStatus.ON_CALL && status != ConcreteShiftDoctorStatus.ON_DUTY) {
                    continue;
                }
                AiAssignmentDto dto = new AiAssignmentDto();
                dto.shiftId = ToonBuilder.shiftIdFor(shift);
                dto.doctorId = assignment.getDoctor().getId().intValue();
                dto.roleCovered = assignment.getDoctor().getSeniority();
                dto.assignmentStatus = status;
                dto.isForced = Boolean.FALSE;
                dto.violationNote = null;
                assignments.add(dto);
            }
        }
        return assignments;
    }

    private List<AiUncoveredShiftDto> buildUncoveredShifts(Schedule schedule) {
        List<AiUncoveredShiftDto> uncovered = new ArrayList<>();
        if (schedule.getConcreteShifts() == null) {
            return uncovered;
        }
        for (ConcreteShift shift : schedule.getConcreteShifts()) {
            int required = computeShiftRequirement(shift.getShift());
            int assigned = countAssignedDoctors(shift);
            if (assigned < required) {
                AiUncoveredShiftDto dto = new AiUncoveredShiftDto();
                dto.shiftId = ToonBuilder.shiftIdFor(shift);
                dto.reason = "UNDERSTAFFED";
                uncovered.add(dto);
            }
        }
        return uncovered;
    }

    private List<AiUffaDeltaDto> buildUffaDelta(Schedule schedule) {
        List<AiUffaDeltaDto> deltas = new ArrayList<>();
        if (schedule.getDoctorUffaPrioritiesSnapshot() == null || schedule.getDoctorUffaPriorityList() == null) {
            return deltas;
        }
        Map<Long, Integer> previous = new HashMap<>();
        for (DoctorUffaPrioritySnapshot snapshot : schedule.getDoctorUffaPrioritiesSnapshot()) {
            if (snapshot.getDoctor() != null && snapshot.getDoctor().getId() != null) {
                previous.put(snapshot.getDoctor().getId(), snapshot.getGeneralPriority());
            }
        }
        for (DoctorUffaPriority priority : schedule.getDoctorUffaPriorityList()) {
            if (priority.getDoctor() == null || priority.getDoctor().getId() == null) {
                continue;
            }
            Integer previousValue = previous.get(priority.getDoctor().getId());
            if (previousValue == null) {
                continue;
            }
            AiUffaDeltaDto dto = new AiUffaDeltaDto();
            dto.doctorId = priority.getDoctor().getId().intValue();
            dto.queue = AiUffaQueue.GEN;
            dto.points = priority.getGeneralPriority() - previousValue;
            deltas.add(dto);
        }
        return deltas;
    }

    private AiScheduleResponseDto buildAiResponseDto(AiScheduleResponse response) {
        AiScheduleResponseDto dto = new AiScheduleResponseDto();
        dto.status = response.getStatus();
        dto.metadata = mapMetadata(response.getMetadata());
        dto.assignments = mapAssignments(response.getAssignments());
        dto.uncoveredShifts = mapUncoveredShifts(response.getUncoveredShifts());
        dto.uffaDelta = mapUffaDeltas(response.getUffaDelta());
        return dto;
    }

    private AiMetadataDto mapMetadata(AiMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        AiMetadataDto dto = new AiMetadataDto();
        dto.reasoning = metadata.getReasoning();
        dto.optimalityScore = metadata.getOptimalityScore();
        dto.metrics = mapMetrics(metadata.getMetrics());
        return dto;
    }

    private AiMetricsDto mapMetrics(AiMetrics metrics) {
        if (metrics == null) {
            return null;
        }
        AiMetricsDto dto = new AiMetricsDto();
        dto.coveragePercent = metrics.getCoveragePercent();
        dto.softViolationsCount = metrics.getSoftViolationsCount();
        dto.uffaBalance = mapUffaBalance(metrics.getUffaBalance());
        return dto;
    }

    private AiUffaBalanceDto mapUffaBalance(AiUffaBalance balance) {
        if (balance == null) {
            return null;
        }
        AiUffaBalanceDto dto = new AiUffaBalanceDto();
        dto.nightShiftStdDev = mapStdDev(balance.getNightShiftStdDev());
        return dto;
    }

    private AiStdDevDto mapStdDev(AiStdDev stdDev) {
        if (stdDev == null) {
            return null;
        }
        AiStdDevDto dto = new AiStdDevDto();
        dto.initial = stdDev.getInitial();
        dto.finalValue = stdDev.getFinalValue();
        return dto;
    }

    private List<AiAssignmentDto> mapAssignments(List<AiAssignment> assignments) {
        List<AiAssignmentDto> result = new ArrayList<>();
        if (assignments == null) {
            return result;
        }
        for (AiAssignment assignment : assignments) {
            AiAssignmentDto dto = new AiAssignmentDto();
            dto.shiftId = assignment.getShiftId();
            dto.doctorId = assignment.getDoctorId();
            dto.roleCovered = assignment.getRoleCovered();
            dto.assignmentStatus = assignment.getAssignmentStatus();
            dto.isForced = assignment.isForced();
            dto.violationNote = assignment.getViolationNote();
            result.add(dto);
        }
        return result;
    }

    private List<AiUncoveredShiftDto> mapUncoveredShifts(List<AiUncoveredShift> shifts) {
        List<AiUncoveredShiftDto> result = new ArrayList<>();
        if (shifts == null) {
            return result;
        }
        for (AiUncoveredShift shift : shifts) {
            AiUncoveredShiftDto dto = new AiUncoveredShiftDto();
            dto.shiftId = shift.getShiftId();
            dto.reason = shift.getReason();
            result.add(dto);
        }
        return result;
    }

    private List<AiUffaDeltaDto> mapUffaDeltas(List<AiUffaDelta> deltas) {
        List<AiUffaDeltaDto> result = new ArrayList<>();
        if (deltas == null) {
            return result;
        }
        for (AiUffaDelta delta : deltas) {
            AiUffaDeltaDto dto = new AiUffaDeltaDto();
            dto.doctorId = delta.getDoctorId();
            dto.queue = delta.getQueue();
            dto.points = delta.getPoints();
            result.add(dto);
        }
        return result;
    }

    private String serializeResponse(AiScheduleResponseDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize AI schedule response", e);
        }
    }

    private AiScheduleDecisionOutcome selectDecisionOutcome(List<CandidateData> candidates,
                                                           Map<String, AiScheduleCandidateMetrics> normalizedMetrics) {
        List<AiScheduleCandidateMetrics> metrics = new ArrayList<>();
        for (CandidateData candidate : candidates) {
            if (!candidate.validation.valid) {
                continue;
            }
            AiScheduleCandidateMetrics candidateMetrics = normalizedMetrics.get(candidate.candidateId);
            if (candidateMetrics != null) {
                metrics.add(candidateMetrics);
            }
        }
        if (metrics.isEmpty()) {
            return null;
        }
        org.cswteams.ms3.audit.selection.AuditedSelectionResult auditedResult =
                decisionAlgorithmService.selectPreferredWithAudit(metrics);
        AiScheduleCandidateMetrics selected = null;
        for (AiScheduleCandidateMetrics candidate : metrics) {
            if (candidate.getCandidateId().equals(auditedResult.getSelectedCandidateId())) {
                selected = candidate;
                break;
            }
        }
        if (selected == null) {
            return null;
        }
        CandidateData selectedCandidate = null;
        for (CandidateData candidate : candidates) {
            if (candidate.candidateId.equals(selected.getCandidateId())) {
                selectedCandidate = candidate;
                break;
            }
        }
        if (selectedCandidate == null) {
            return null;
        }
        return new AiScheduleDecisionOutcome(
                selectedCandidate.candidateId,
                selectedCandidate.scheduleId,
                selectedCandidate.type
        );
    }

    private Map<String, AiScheduleCandidateMetrics> normalizeMetrics(List<CandidateData> candidates) {
        Map<String, AiScheduleCandidateMetrics> normalized = new HashMap<>();
        if (candidates == null || candidates.isEmpty()) {
            logger.info("event=metrics_normalization_empty candidates_count=0");
            return normalized;
        }
        double minCoverage = Double.POSITIVE_INFINITY;
        double maxCoverage = Double.NEGATIVE_INFINITY;
        double minUffaBalance = Double.POSITIVE_INFINITY;
        double maxUffaBalance = Double.NEGATIVE_INFINITY;
        double minUpDelta = Double.POSITIVE_INFINITY;
        double maxUpDelta = Double.NEGATIVE_INFINITY;
        double minVariance = Double.POSITIVE_INFINITY;
        double maxVariance = Double.NEGATIVE_INFINITY;

        for (CandidateData candidate : candidates) {
            DecisionMetricValues metrics = candidate.rawMetrics;
            minCoverage = Math.min(minCoverage, metrics.getCoverage());
            maxCoverage = Math.max(maxCoverage, metrics.getCoverage());
            minUffaBalance = Math.min(minUffaBalance, metrics.getUffaBalance());
            maxUffaBalance = Math.max(maxUffaBalance, metrics.getUffaBalance());
            minUpDelta = Math.min(minUpDelta, metrics.getUpDelta());
            maxUpDelta = Math.max(maxUpDelta, metrics.getUpDelta());
            minVariance = Math.min(minVariance, metrics.getVarianceDelta());
            maxVariance = Math.max(maxVariance, metrics.getVarianceDelta());
        }

        for (CandidateData candidate : candidates) {
            DecisionMetricValues metrics = candidate.rawMetrics;
            // Coverage is already bounded in [0,1], preserve its absolute meaning across candidates.
            double coverage = clampUnit(metrics.getCoverage());
            double uffaBalance = MetricNormalizationUtils.normalizeRange(metrics.getUffaBalance(), minUffaBalance,
                    maxUffaBalance, true);
            double upDelta = MetricNormalizationUtils.normalizeRange(metrics.getUpDelta(), minUpDelta, maxUpDelta,
                    true);
            double variance = MetricNormalizationUtils.normalizeRange(metrics.getVarianceDelta(), minVariance,
                    maxVariance, true);
            AiScheduleCandidateMetrics normalizedCandidate = new AiScheduleCandidateMetrics(
                    candidate.candidateId,
                    coverage,
                    uffaBalance,
                    upDelta,
                    variance
            );
            normalized.put(candidate.candidateId, normalizedCandidate);
        }
        logger.info("event=metrics_normalization_completed candidates_count={} coverage_min={} coverage_max={} uffa_min={} uffa_max={} up_delta_min={} up_delta_max={} variance_min={} variance_max={}",
                candidates.size(),
                minCoverage,
                maxCoverage,
                minUffaBalance,
                maxUffaBalance,
                minUpDelta,
                maxUpDelta,
                minVariance,
                maxVariance);
        return normalized;
    }

    private double clampUnit(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return 0.0;
        }
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }

    private MetricsErrorMetadata buildMetricsError(MetricsErrorMetadata existing,
                                                   String correlationId,
                                                   String errorCode,
                                                   Exception ex) {
        logger.error("event=metrics_computation_failed correlation_id={} error_code={} stage={} message={}",
                correlationId,
                errorCode,
                METRICS_COMPUTE_STAGE,
                ex.getMessage(),
                ex);
        if (existing != null) {
            return existing;
        }
        return new MetricsErrorMetadata(correlationId, errorCode, METRICS_COMPUTE_STAGE, true);
    }

    private MetricsErrorMetadata mergeError(MetricsErrorMetadata existing, MetricsErrorMetadata incoming) {
        if (existing != null) {
            return existing;
        }
        return incoming;
    }

    private DecisionMetricValues fallbackMetrics() {
        return new DecisionMetricValues(0.0, 0.0, 0.0, 0.0);
    }

    private static class VariantDefinition {
        private final String label;
        private final String candidateId;
        private final ScheduleCandidateType type;
        private final String variantInstruction;

        private VariantDefinition(String label,
                                  String candidateId,
                                  ScheduleCandidateType type,
                                  String variantInstruction) {
            this.label = Objects.requireNonNull(label, "label");
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.type = Objects.requireNonNull(type, "type");
            this.variantInstruction = Objects.requireNonNull(variantInstruction, "variantInstruction");
        }
    }

    private static class ToonPayloadContext {
        private final String fullPayload;
        private final String payloadWithoutScratchpad;
        private final String initialScratchpadBlock;
        private final List<ConcreteShift> scopedShifts;
        private final List<Doctor> eligibleDoctors;

        private ToonPayloadContext(String fullPayload,
                                   String payloadWithoutScratchpad,
                                   String initialScratchpadBlock,
                                   List<ConcreteShift> scopedShifts,
                                   List<Doctor> eligibleDoctors) {
            this.fullPayload = fullPayload;
            this.payloadWithoutScratchpad = payloadWithoutScratchpad;
            this.initialScratchpadBlock = initialScratchpadBlock;
            this.scopedShifts = scopedShifts;
            this.eligibleDoctors = eligibleDoctors;
        }
    }

    private static class CandidateBatch {
        private final List<CandidateData> candidates;
        private final MetricsErrorMetadata errorMetadata;

        private CandidateBatch(List<CandidateData> candidates, MetricsErrorMetadata errorMetadata) {
            this.candidates = candidates;
            this.errorMetadata = errorMetadata;
        }
    }

    private static class MetricsErrorMetadata {
        private final String correlationId;
        private final String errorCode;
        private final String stage;
        private final boolean retryable;

        private MetricsErrorMetadata(String correlationId, String errorCode, String stage, boolean retryable) {
            this.correlationId = correlationId;
            this.errorCode = errorCode;
            this.stage = stage;
            this.retryable = retryable;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getStage() {
            return stage;
        }

        public boolean isRetryable() {
            return retryable;
        }
    }

    private static class CandidateData {
        private final String candidateId;
        private final Long scheduleId;
        private final ScheduleCandidateType type;
        private final String rawScheduleJson;
        private final DecisionMetricValues rawMetrics;
        private final Schedule schedule;
        private final CandidateValidationData validation;

        private CandidateData(String candidateId,
                              Long scheduleId,
                              ScheduleCandidateType type,
                              String rawScheduleJson,
                              DecisionMetricValues rawMetrics,
                              Schedule schedule,
                              CandidateValidationData validation) {
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.scheduleId = scheduleId;
            this.type = Objects.requireNonNull(type, "type");
            this.rawScheduleJson = rawScheduleJson;
            this.rawMetrics = Objects.requireNonNull(rawMetrics, "rawMetrics");
            this.schedule = schedule;
            this.validation = Objects.requireNonNull(validation, "validation");
        }
    }

    private static class CandidateValidationData {
        private final boolean valid;
        private final boolean maxRetriesReached;
        private final String code;
        private final String message;
        private final String apiMessage;
        private final List<ConstraintViolationDetail> violatedConstraints;
        private final List<ProtocolValidationDetail> protocolValidationDetails;
        private final List<RoleCoverageViolationDetail> roleCoverageViolations;
        private final Map<String, Integer> requiredSlotsByShiftRoleLayer;
        private final Map<String, Integer> assignedSlotsByShiftRoleLayer;
        private final boolean dualLayerCoverageValid;
        private final List<RoleMismatchDetail> mismatchExamples;

        private CandidateValidationData(boolean valid,
                                        boolean maxRetriesReached,
                                        String code,
                                        String message,
                                        String apiMessage,
                                        List<ConstraintViolationDetail> violatedConstraints,
                                        List<ProtocolValidationDetail> protocolValidationDetails,
                                        List<RoleCoverageViolationDetail> roleCoverageViolations,
                                        Map<String, Integer> requiredSlotsByShiftRoleLayer,
                                        Map<String, Integer> assignedSlotsByShiftRoleLayer,
                                        List<RoleMismatchDetail> mismatchExamples) {
            this.valid = valid;
            this.maxRetriesReached = maxRetriesReached;
            this.code = code;
            this.message = message;
            this.apiMessage = apiMessage;
            this.violatedConstraints = violatedConstraints == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(violatedConstraints));
            this.protocolValidationDetails = protocolValidationDetails == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(protocolValidationDetails));
            this.roleCoverageViolations = roleCoverageViolations == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(roleCoverageViolations));
            this.requiredSlotsByShiftRoleLayer = requiredSlotsByShiftRoleLayer == null
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(new LinkedHashMap<>(requiredSlotsByShiftRoleLayer));
            this.assignedSlotsByShiftRoleLayer = assignedSlotsByShiftRoleLayer == null
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(new LinkedHashMap<>(assignedSlotsByShiftRoleLayer));
            this.dualLayerCoverageValid = this.roleCoverageViolations.isEmpty();
            this.mismatchExamples = mismatchExamples == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(mismatchExamples));
        }

        private static CandidateValidationData valid() {
            return new CandidateValidationData(true,
                    false,
                    null,
                    null,
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyList());
        }

        private static CandidateValidationData valid(Map<String, Integer> requiredSlotsByShiftRoleLayer,
                                                     Map<String, Integer> assignedSlotsByShiftRoleLayer,
                                                     List<RoleCoverageViolationDetail> roleCoverageViolations,
                                                     List<RoleMismatchDetail> mismatchExamples) {
            return new CandidateValidationData(true,
                    false,
                    null,
                    null,
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    roleCoverageViolations,
                    requiredSlotsByShiftRoleLayer,
                    assignedSlotsByShiftRoleLayer,
                    mismatchExamples);
        }

        private static CandidateValidationData validWithWarning(String code, String message) {
            return validWithWarning(code, message, Collections.emptyList());
        }

        private static CandidateValidationData validWithWarning(String code,
                                                                String message,
                                                                List<ConstraintViolationDetail> violatedConstraints) {
            return new CandidateValidationData(true,
                    false,
                    code,
                    message,
                    message,
                    violatedConstraints,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code, String message) {
            return invalid(code, message, message, Collections.emptyList(), Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code, String message, List<ConstraintViolationDetail> violatedConstraints) {
            return invalid(code, message, message, violatedConstraints, Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code,
                                                       String message,
                                                       String apiMessage,
                                                       List<ConstraintViolationDetail> violatedConstraints) {
            return invalid(code, message, apiMessage, violatedConstraints, Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code,
                                                       String message,
                                                       String apiMessage,
                                                       List<ConstraintViolationDetail> violatedConstraints,
                                                       List<ProtocolValidationDetail> protocolValidationDetails) {
            return invalid(code, message, apiMessage, violatedConstraints, protocolValidationDetails, Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code,
                                                       String message,
                                                       String apiMessage,
                                                       List<ConstraintViolationDetail> violatedConstraints,
                                                       List<ProtocolValidationDetail> protocolValidationDetails,
                                                       List<RoleCoverageViolationDetail> roleCoverageViolations) {
            return invalid(code,
                    message,
                    apiMessage,
                    violatedConstraints,
                    protocolValidationDetails,
                    roleCoverageViolations,
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyList());
        }

        private static CandidateValidationData invalid(String code,
                                                       String message,
                                                       String apiMessage,
                                                       List<ConstraintViolationDetail> violatedConstraints,
                                                       List<ProtocolValidationDetail> protocolValidationDetails,
                                                       List<RoleCoverageViolationDetail> roleCoverageViolations,
                                                       Map<String, Integer> requiredSlotsByShiftRoleLayer,
                                                       Map<String, Integer> assignedSlotsByShiftRoleLayer,
                                                       List<RoleMismatchDetail> mismatchExamples) {
            return new CandidateValidationData(false,
                    false,
                    code,
                    message,
                    apiMessage,
                    violatedConstraints,
                    protocolValidationDetails,
                    roleCoverageViolations,
                    requiredSlotsByShiftRoleLayer,
                    assignedSlotsByShiftRoleLayer,
                    mismatchExamples);
        }

        private CandidateValidationData withMaxRetriesReached(boolean reached) {
            return new CandidateValidationData(valid,
                    reached,
                    code,
                    message,
                    apiMessage,
                    violatedConstraints,
                    protocolValidationDetails,
                    roleCoverageViolations,
                    requiredSlotsByShiftRoleLayer,
                    assignedSlotsByShiftRoleLayer,
                    mismatchExamples);
        }

        private CandidateValidationData withWarning(String warningCode, String warningMessage) {
            return new CandidateValidationData(valid,
                    maxRetriesReached,
                    warningCode,
                    warningMessage,
                    warningMessage,
                    violatedConstraints,
                    protocolValidationDetails,
                    roleCoverageViolations,
                    requiredSlotsByShiftRoleLayer,
                    assignedSlotsByShiftRoleLayer,
                    mismatchExamples);
        }

        private List<String> violationMessages() {
            List<String> messages = violatedConstraints.stream()
                    .map(violation -> violation.severity + " " + violation.constraintType + "[" + violation.constraintId + "]: " + violation.actualCondition)
                    .collect(Collectors.toList());
            messages.addAll(protocolValidationDetails.stream()
                    .map(detail -> detail.path + ": " + detail.message)
                    .collect(Collectors.toList()));
            messages.addAll(roleCoverageViolations.stream()
                    .map(RoleCoverageViolationDetail::toCompactMessage)
                    .collect(Collectors.toList()));
            return messages;
        }
    }

    private static class RoleCoverageViolationDetail {
        private final String shiftId;
        private final String assignmentStatus;
        private final String roleCovered;
        private final int requiredMin;
        private final int actualCount;

        private RoleCoverageViolationDetail(String shiftId,
                                            String assignmentStatus,
                                            String roleCovered,
                                            int requiredMin,
                                            int actualCount) {
            this.shiftId = shiftId;
            this.assignmentStatus = assignmentStatus;
            this.roleCovered = roleCovered;
            this.requiredMin = requiredMin;
            this.actualCount = actualCount;
        }

        private String toCompactMessage() {
            return "role_layer_minima[shift_id=" + shiftId
                    + ",assignment_status=" + assignmentStatus
                    + ",role_covered=" + roleCovered
                    + "]: actual=" + actualCount + " < required=" + requiredMin;
        }
    }

    private static class RoleMismatchDetail {
        private final String path;
        private final String expectedSeniority;
        private final String actualSeniorityOrAssignedRole;

        private RoleMismatchDetail(String path, String expectedSeniority, String actualSeniorityOrAssignedRole) {
            this.path = path;
            this.expectedSeniority = expectedSeniority;
            this.actualSeniorityOrAssignedRole = actualSeniorityOrAssignedRole;
        }

        @Override
        public String toString() {
            return "{path='" + path + "', expected_seniority='" + expectedSeniority
                    + "', actual='" + actualSeniorityOrAssignedRole + "'}";
        }
    }

    private static class ProtocolValidationDetail {
        private final String path;
        private final String message;

        private ProtocolValidationDetail(String path, String message) {
            this.path = path;
            this.message = message;
        }
    }

    private static class ConstraintViolationDetail {
        private final String constraintId;
        private final String constraintType;
        private final String shiftId;
        private final String date;
        private final String doctorId;
        private final ConstraintViolationSeverity severity;
        private final String expectedCondition;
        private final String actualCondition;

        private ConstraintViolationDetail(String constraintId,
                                          String constraintType,
                                          String shiftId,
                                          String date,
                                          String doctorId,
                                          ConstraintViolationSeverity severity,
                                          String expectedCondition,
                                          String actualCondition) {
            this.constraintId = constraintId;
            this.constraintType = constraintType;
            this.shiftId = shiftId;
            this.date = date;
            this.doctorId = doctorId;
            this.severity = severity == null ? ConstraintViolationSeverity.HARD : severity;
            this.expectedCondition = expectedCondition;
            this.actualCondition = actualCondition;
        }

        private static ConstraintViolationDetail of(Constraint constraint,
                                                    ConcreteShift concreteShift,
                                                    Doctor doctor,
                                                    String expectedCondition,
                                                    String actualCondition) {
            String date = concreteShift == null ? null : String.valueOf(LocalDate.ofEpochDay(concreteShift.getDate()));
            String shiftId = concreteShift == null || concreteShift.getShift() == null || concreteShift.getShift().getId() == null
                    ? null
                    : String.valueOf(concreteShift.getShift().getId());
            String doctorId = doctor == null || doctor.getId() == null ? null : String.valueOf(doctor.getId());
            String constraintId = constraint == null || constraint.getId() == null ? null : String.valueOf(constraint.getId());
            String constraintType = constraint == null
                    ? null
                    : constraint.getClass().getSimpleName();
            return new ConstraintViolationDetail(
                    constraintId,
                    constraintType,
                    shiftId,
                    date,
                    doctorId,
                    fromConstraint(constraint),
                    expectedCondition,
                    actualCondition
            );
        }

        private static ConstraintViolationSeverity fromConstraint(Constraint constraint) {
            if (constraint != null && constraint.isViolable()) {
                return ConstraintViolationSeverity.SOFT;
            }
            return ConstraintViolationSeverity.HARD;
        }

        private static ConstraintViolationDetail malformedShift(ConcreteShift concreteShift,
                                                                String missingField,
                                                                String actualCondition) {
            String date = concreteShift == null ? null : String.valueOf(LocalDate.ofEpochDay(concreteShift.getDate()));
            String shiftId = concreteShift == null || concreteShift.getShift() == null || concreteShift.getShift().getId() == null
                    ? null
                    : String.valueOf(concreteShift.getShift().getId());
            return new ConstraintViolationDetail(
                    "STRUCTURAL_VALIDATION",
                    "MALFORMED_CANDIDATE_SHIFT",
                    shiftId,
                    date,
                    null,
                    ConstraintViolationSeverity.HARD,
                    "ConcreteShift must include non-null " + missingField,
                    actualCondition
            );
        }

        private static ConstraintViolationDetail constraintExecutionFailure(Constraint constraint,
                                                                            ConcreteShift concreteShift,
                                                                            Doctor doctor,
                                                                            String exceptionClass,
                                                                            String exceptionMessage) {
            String sanitizedExceptionClass = exceptionClass == null || exceptionClass.trim().isEmpty()
                    ? "RuntimeException"
                    : exceptionClass.trim();
            String sanitizedExceptionMessage = exceptionMessage == null || exceptionMessage.trim().isEmpty()
                    ? "n/a"
                    : exceptionMessage.trim();
            return of(constraint,
                    concreteShift,
                    doctor,
                    "Constraint execution must complete without runtime errors",
                    "Constraint execution failed: " + sanitizedExceptionClass + ": " + sanitizedExceptionMessage);
        }
    }

    private enum ConstraintViolationSeverity {
        HARD,
        SOFT
    }

    private static class PriorityDeltaStats {
        private final double mean;
        private final double variance;

        private PriorityDeltaStats(double mean, double variance) {
            this.mean = mean;
            this.variance = variance;
        }
    }

    private static class TransientComparisonState {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Map<String, CandidateData> candidates;
        private final AiScheduleComparisonResponseDto response;

        private TransientComparisonState(LocalDate startDate,
                                         LocalDate endDate,
                                         Map<String, CandidateData> candidates,
                                         AiScheduleComparisonResponseDto response) {
            this.startDate = Objects.requireNonNull(startDate, "startDate");
            this.endDate = Objects.requireNonNull(endDate, "endDate");
            this.candidates = Objects.requireNonNull(candidates, "candidates");
            this.response = response;
        }

        private CandidateData resolveCandidate(String candidateIdOrLabel) {
            if (candidateIdOrLabel == null) {
                return null;
            }
            String trimmed = candidateIdOrLabel.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            for (CandidateData candidate : candidates.values()) {
                if (candidate.candidateId.equalsIgnoreCase(trimmed)) {
                    return candidate;
                }
                if (candidate.type.getLabel().equalsIgnoreCase(trimmed)) {
                    return candidate;
                }
                if (candidate.type.name().equalsIgnoreCase(trimmed)) {
                    return candidate;
                }
            }
            return null;
        }

        private CandidateData resolveCandidate(Long scheduleId) {
            if (scheduleId == null) {
                return null;
            }
            for (CandidateData candidate : candidates.values()) {
                if (scheduleId.equals(candidate.scheduleId)) {
                    return candidate;
                }
            }
            return null;
        }
    }

    public static class SelectionResult {
        public enum Status {
            PERSISTED,
            INVALID_SELECTION,
            CANDIDATE_NOT_FOUND,
            NO_ACTIVE_COMPARISON,
            DUPLICATE_RANGE,
            SCHEDULE_NOT_FOUND
        }

        private final Status status;
        private final Long scheduleId;
        private final String errorCode;
        private final String errorMessage;

        private SelectionResult(Status status, Long scheduleId, String errorCode, String errorMessage) {
            this.status = status;
            this.scheduleId = scheduleId;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public static SelectionResult persisted(Long scheduleId) {
            return new SelectionResult(Status.PERSISTED, scheduleId, null, null);
        }

        public static SelectionResult invalid(String errorCode, String errorMessage) {
            return new SelectionResult(Status.INVALID_SELECTION, null, errorCode, errorMessage);
        }

        public static SelectionResult notFound(String errorCode, String errorMessage) {
            return new SelectionResult(Status.CANDIDATE_NOT_FOUND, null, errorCode, errorMessage);
        }

        public static SelectionResult noActiveComparison(String errorCode, String errorMessage) {
            return new SelectionResult(Status.NO_ACTIVE_COMPARISON, null, errorCode, errorMessage);
        }

        public static SelectionResult duplicateRange(String errorCode, String errorMessage) {
            return new SelectionResult(Status.DUPLICATE_RANGE, null, errorCode, errorMessage);
        }

        public static SelectionResult scheduleNotFound(String errorCode, String errorMessage) {
            return new SelectionResult(Status.SCHEDULE_NOT_FOUND, null, errorCode, errorMessage);
        }

        public Status getStatus() {
            return status;
        }

        public Long getScheduleId() {
            return scheduleId;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
