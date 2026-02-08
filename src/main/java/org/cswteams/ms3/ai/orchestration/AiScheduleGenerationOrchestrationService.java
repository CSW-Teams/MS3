package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerRequest;
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
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.ai.protocol.utils.AiUffaQueue;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonRequestContext;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.DoctorHolidaysDAO;
import org.cswteams.ms3.dao.DoctorUffaPriorityDAO;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.DoctorUffaPrioritySnapshot;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AiScheduleGenerationOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(AiScheduleGenerationOrchestrationService.class);
    private static final String MODE_GENERATE = "generate";
    private static final String METRICS_COMPUTE_STAGE = "METRICS_COMPUTE";
    private static final String ERROR_STANDARD_METRICS = "STANDARD_METRICS_FAILED";
    private static final String ERROR_AI_METRICS = "AI_METRICS_FAILED";
    private static final String ERROR_NORMALIZATION = "METRICS_NORMALIZATION_FAILED";
    private static final String ERROR_DECISION = "METRICS_DECISION_FAILED";
    private static final String EMPATHETIC_LABEL = "EMPATHETIC";
    private static final String EFFICIENT_LABEL = "EFFICIENT";
    private static final String BALANCED_LABEL = "BALANCED";
    private static final List<VariantDefinition> VARIANT_DEFINITIONS = List.of(
            new VariantDefinition(EMPATHETIC_LABEL,
                    "ai-empathetic",
                    ScheduleCandidateType.EMPATHETIC),
            new VariantDefinition(EFFICIENT_LABEL,
                    "ai-efficient",
                    ScheduleCandidateType.EFFICIENT),
            new VariantDefinition(BALANCED_LABEL,
                    "ai-balanced",
                    ScheduleCandidateType.BALANCED)
    );

    private final ISchedulerController schedulerController;
    private final DoctorDAO doctorDAO;
    private final DoctorUffaPriorityDAO doctorUffaPriorityDAO;
    private final DoctorHolidaysDAO doctorHolidaysDAO;
    private final ScheduleDAO scheduleDAO;
    private final AgentBroker agentBroker;
    private final AiReschedulingOrchestrationService aiReschedulingOrchestrationService;
    private final DecisionAlgorithmService decisionAlgorithmService;
    private final AiScheduleConverterService aiScheduleConverterService;
    private final ObjectMapper objectMapper;
    private final AiScheduleComparisonMapper comparisonMapper = new AiScheduleComparisonMapper();
    private final AtomicReference<TransientComparisonState> transientComparisonState = new AtomicReference<>();

    @Autowired
    public AiScheduleGenerationOrchestrationService(ISchedulerController schedulerController,
                                                    DoctorDAO doctorDAO,
                                                    DoctorUffaPriorityDAO doctorUffaPriorityDAO,
                                                    DoctorHolidaysDAO doctorHolidaysDAO,
                                                    ScheduleDAO scheduleDAO,
                                                    AgentBroker agentBroker,
                                                    AiReschedulingOrchestrationService aiReschedulingOrchestrationService,
                                                    DecisionAlgorithmService decisionAlgorithmService,
                                                    AiScheduleConverterService aiScheduleConverterService,
                                                    ObjectMapper objectMapper) {
        this.schedulerController = schedulerController;
        this.doctorDAO = doctorDAO;
        this.doctorUffaPriorityDAO = doctorUffaPriorityDAO;
        this.doctorHolidaysDAO = doctorHolidaysDAO;
        this.scheduleDAO = scheduleDAO;
        this.agentBroker = agentBroker;
        this.aiReschedulingOrchestrationService = aiReschedulingOrchestrationService;
        this.decisionAlgorithmService = decisionAlgorithmService;
        this.aiScheduleConverterService = aiScheduleConverterService;
        this.objectMapper = objectMapper;
    }

    public AiScheduleComparisonResponseDto generateScheduleComparison(LocalDate startDate, LocalDate endDate) {
        logger.info("event=ai_standard_generation_start start_date={} end_date={}", startDate, endDate);
        Schedule standardSchedule = schedulerController.createScheduleTransient(startDate, endDate);
        if (standardSchedule == null) {
            logger.warn("event=ai_standard_generation_empty start_date={} end_date={}", startDate, endDate);
            return null;
        }
        standardSchedule = scheduleDAO.save(standardSchedule);
        String metricsCorrelationId = UUID.randomUUID().toString();
        MetricsErrorMetadata errorMetadata = null;
        int standardShiftCount = standardSchedule.getConcreteShifts() == null
                ? 0
                : standardSchedule.getConcreteShifts().size();
        logger.info("event=ai_standard_generation_completed start_date={} end_date={} shifts_count={}",
                startDate, endDate, standardShiftCount);

        String toonPayload = buildToonPayload(startDate, endDate, standardSchedule.getConcreteShifts());

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
        CandidateBatch aiBatch = requestAiCandidates(toonPayload);
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
                    metrics
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
        if (!schedulerController.alreadyExistsAnotherSchedule(state.startDate, state.endDate)) {
            return SelectionResult.duplicateRange("DUPLICATE_RANGE", "Schedule already exists for this date range.");
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

    private String buildToonPayload(LocalDate startDate, LocalDate endDate, List<ConcreteShift> concreteShifts) {
        List<Doctor> doctors = doctorDAO.findAll();
        List<DoctorUffaPriority> priorities = doctorUffaPriorityDAO.findAll();
        List<DoctorHolidays> doctorHolidays = doctorHolidaysDAO.findAll();
        List<ToonActiveConstraint> activeConstraints = new ArrayList<>();
        List<ToonFeedback> feedbacks = new ArrayList<>();
        int shiftCount = concreteShifts == null ? 0 : concreteShifts.size();
        logger.info("event=toon_payload_build_requested start_date={} end_date={} shifts_count={} doctors_count={} priorities_count={} holidays_count={} constraints_count={} feedbacks_count={}",
                startDate,
                endDate,
                shiftCount,
                doctors.size(),
                priorities.size(),
                doctorHolidays.size(),
                activeConstraints.size(),
                feedbacks.size());

        AiReschedulingToonRequest request = aiReschedulingOrchestrationService.buildToonRequestContext(
                startDate,
                endDate,
                MODE_GENERATE,
                concreteShifts,
                doctors,
                priorities,
                doctorHolidays,
                activeConstraints,
                feedbacks
        );

        ToonRequestContext context = request.getToonRequestContext();
        ToonBuilder builder = new ToonBuilder();
        return builder.build(context);
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
                schedule
        );
    }

    private CandidateBatch requestAiCandidates(String toonPayload) {
        String instructions = buildMultiVariantInstructions();
        String correlationId = UUID.randomUUID().toString();
        AiBrokerRequest request = new AiBrokerRequest(toonPayload, instructions, correlationId);
        logger.info("event=ai_broker_request_prepared correlation_id={} payload_length={} instructions_length={}",
                correlationId,
                toonPayload == null ? 0 : toonPayload.length(),
                instructions == null ? 0 : instructions.length());
        // Transport-level failures (timeouts/network/rate limits) are handled exclusively by AgentBrokerImpl.
        // Orchestration focuses on schema validation, metrics evaluation, and system-level decision logic.
        AiScheduleVariantsResponse response = agentBroker.requestSchedule(request);
        logger.info("event=ai_broker_response_received correlation_id={} variants_count={}",
                correlationId,
                response == null || response.getVariants() == null ? 0 : response.getVariants().size());
        List<CandidateData> candidates = new ArrayList<>();
        MetricsErrorMetadata errorMetadata = null;
        for (VariantDefinition definition : VARIANT_DEFINITIONS) {
            AiScheduleResponse variant = response.getVariant(definition.label);
            if (variant == null) {
                throw AiProtocolException.schemaMismatch(
                        "AI response missing variant " + definition.label,
                        null
                );
            }
            DecisionMetricValues metrics;
            try {
                metrics = buildAiMetrics(variant);
            } catch (IllegalArgumentException | PriorityScaleValidationException ex) {
                errorMetadata = buildMetricsError(errorMetadata,
                        correlationId,
                        ERROR_AI_METRICS,
                        ex);
                metrics = fallbackMetrics();
            }
            AiScheduleResponseDto responseDto = buildAiResponseDto(variant);
            String rawJson = serializeResponse(responseDto);
            candidates.add(new CandidateData(definition.candidateId, null, definition.type, rawJson, metrics, null));
        }
        return new CandidateBatch(Collections.unmodifiableList(candidates), errorMetadata);
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
                0.0,
                deltaStats.mean,
                deltaStats.variance
        );
    }

    private DecisionMetricValues buildAiMetrics(AiScheduleResponse response) {
        AiMetadata metadata = response.getMetadata();
        AiMetrics metrics = metadata != null ? metadata.getMetrics() : null;
        Double coverage = metrics != null ? metrics.getCoveragePercent() : 0.0;
        Double uffaBalance = resolveUffaBalanceImprovement(metrics);
        PriorityDeltaStats deltaStats = computeUffaDeltaStats(response.getUffaDelta());
        logger.info("event=metrics_ai_calculated coverage={} uffa_balance={} delta_mean={} delta_variance={}",
                coverage,
                uffaBalance,
                deltaStats.mean,
                deltaStats.variance);
        return new DecisionMetricValues(
                coverage,
                uffaBalance,
                0.0,
                deltaStats.mean,
                deltaStats.variance
        );
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

    private double computeCoverage(List<ConcreteShift> concreteShifts) {
        if (concreteShifts == null || concreteShifts.isEmpty()) {
            logger.info("event=coverage_computed total_required=0 total_assigned=0 coverage=0.0");
            return 0.0;
        }
        int totalRequired = 0;
        int totalAssigned = 0;
        for (ConcreteShift shift : concreteShifts) {
            int required = computeShiftRequirement(shift.getShift());
            int assigned = countAssignedDoctors(shift);
            totalRequired += required;
            totalAssigned += Math.min(required, assigned);
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
        if (shift == null || shift.getDoctorAssignmentList() == null) {
            return 0;
        }
        int count = 0;
        for (DoctorAssignment assignment : shift.getDoctorAssignmentList()) {
            if (assignment == null) {
                continue;
            }
            ConcreteShiftDoctorStatus status = assignment.getConcreteShiftDoctorStatus();
            if (status == ConcreteShiftDoctorStatus.ON_CALL || status == ConcreteShiftDoctorStatus.ON_DUTY) {
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
        double minSentiment = Double.POSITIVE_INFINITY;
        double maxSentiment = Double.NEGATIVE_INFINITY;
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
            minSentiment = Math.min(minSentiment, metrics.getSentimentTransitions());
            maxSentiment = Math.max(maxSentiment, metrics.getSentimentTransitions());
            minUpDelta = Math.min(minUpDelta, metrics.getUpDelta());
            maxUpDelta = Math.max(maxUpDelta, metrics.getUpDelta());
            minVariance = Math.min(minVariance, metrics.getVarianceDelta());
            maxVariance = Math.max(maxVariance, metrics.getVarianceDelta());
        }

        for (CandidateData candidate : candidates) {
            DecisionMetricValues metrics = candidate.rawMetrics;
            double coverage = MetricNormalizationUtils.normalizeRange(metrics.getCoverage(), minCoverage, maxCoverage,
                    false);
            double uffaBalance = MetricNormalizationUtils.normalizeRange(metrics.getUffaBalance(), minUffaBalance,
                    maxUffaBalance, false);
            double sentiment = MetricNormalizationUtils.normalizeRange(metrics.getSentimentTransitions(), minSentiment,
                    maxSentiment, false);
            double upDelta = MetricNormalizationUtils.normalizeRange(metrics.getUpDelta(), minUpDelta, maxUpDelta,
                    true);
            double variance = MetricNormalizationUtils.normalizeRange(metrics.getVarianceDelta(), minVariance,
                    maxVariance, true);
            AiScheduleCandidateMetrics normalizedCandidate = new AiScheduleCandidateMetrics(
                    candidate.candidateId,
                    coverage,
                    uffaBalance,
                    sentiment,
                    upDelta,
                    variance
            );
            normalized.put(candidate.candidateId, normalizedCandidate);
        }
        logger.info("event=metrics_normalization_completed candidates_count={} coverage_min={} coverage_max={} uffa_min={} uffa_max={} sentiment_min={} sentiment_max={} up_delta_min={} up_delta_max={} variance_min={} variance_max={}",
                candidates.size(),
                minCoverage,
                maxCoverage,
                minUffaBalance,
                maxUffaBalance,
                minSentiment,
                maxSentiment,
                minUpDelta,
                maxUpDelta,
                minVariance,
                maxVariance);
        return normalized;
    }

    private String buildMultiVariantInstructions() {
        StringBuilder builder = new StringBuilder("Generate three schedule variants in a single JSON response.\n");
        builder.append("Use the labels ")
                .append(AiPromptTemplate.variantLabels())
                .append(" under the \"variants\" object.\n");
        builder.append(AiPromptTemplate.buildVariantIntentInstructions());
        builder.append("Return only the JSON object, no extra text.");
        return builder.toString();
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
        return new DecisionMetricValues(0.0, 0.0, 0.0, 0.0, 0.0);
    }

    private static class VariantDefinition {
        private final String label;
        private final String candidateId;
        private final ScheduleCandidateType type;

        private VariantDefinition(String label,
                                  String candidateId,
                                  ScheduleCandidateType type) {
            this.label = Objects.requireNonNull(label, "label");
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.type = Objects.requireNonNull(type, "type");
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

        private CandidateData(String candidateId,
                              Long scheduleId,
                              ScheduleCandidateType type,
                              String rawScheduleJson,
                              DecisionMetricValues rawMetrics,
                              Schedule schedule) {
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.scheduleId = scheduleId;
            this.type = Objects.requireNonNull(type, "type");
            this.rawScheduleJson = rawScheduleJson;
            this.rawMetrics = Objects.requireNonNull(rawMetrics, "rawMetrics");
            this.schedule = schedule;
        }
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
