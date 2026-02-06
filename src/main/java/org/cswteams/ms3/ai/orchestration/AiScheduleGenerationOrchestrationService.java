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
import org.cswteams.ms3.ai.comparison.mapper.AiScheduleComparisonMapper;
import org.cswteams.ms3.ai.decision.AiScheduleCandidateMetrics;
import org.cswteams.ms3.ai.decision.DecisionAlgorithmService;
import org.cswteams.ms3.ai.metrics.MetricAggregationUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AiScheduleGenerationOrchestrationService {
    private static final String MODE_GENERATE = "generate";
    private static final String EMPATHETIC_LABEL = "EMPATHETIC";
    private static final String EFFICIENT_LABEL = "EFFICIENT";
    private static final String BALANCED_LABEL = "BALANCED";
    private static final List<VariantDefinition> VARIANT_DEFINITIONS = List.of(
            new VariantDefinition(EMPATHETIC_LABEL,
                    "ai-empathetic",
                    ScheduleCandidateType.EMPATHETIC,
                    "Maximize doctor well-being and respect expressed preferences as strict constraints."),
            new VariantDefinition(EFFICIENT_LABEL,
                    "ai-efficient",
                    ScheduleCandidateType.EFFICIENT,
                    "Optimize coverage and fairness metrics, minimizing uncovered shifts and priority variance."),
            new VariantDefinition(BALANCED_LABEL,
                    "ai-balanced",
                    ScheduleCandidateType.BALANCED,
                    "Balance well-being and operational efficiency, allowing soft constraint tradeoffs.")
    );

    private final ISchedulerController schedulerController;
    private final DoctorDAO doctorDAO;
    private final DoctorUffaPriorityDAO doctorUffaPriorityDAO;
    private final DoctorHolidaysDAO doctorHolidaysDAO;
    private final AgentBroker agentBroker;
    private final AiReschedulingOrchestrationService aiReschedulingOrchestrationService;
    private final DecisionAlgorithmService decisionAlgorithmService;
    private final ObjectMapper objectMapper;
    private final AiScheduleComparisonMapper comparisonMapper = new AiScheduleComparisonMapper();

    @Autowired
    public AiScheduleGenerationOrchestrationService(ISchedulerController schedulerController,
                                                    DoctorDAO doctorDAO,
                                                    DoctorUffaPriorityDAO doctorUffaPriorityDAO,
                                                    DoctorHolidaysDAO doctorHolidaysDAO,
                                                    AgentBroker agentBroker,
                                                    AiReschedulingOrchestrationService aiReschedulingOrchestrationService,
                                                    DecisionAlgorithmService decisionAlgorithmService,
                                                    ObjectMapper objectMapper) {
        this.schedulerController = schedulerController;
        this.doctorDAO = doctorDAO;
        this.doctorUffaPriorityDAO = doctorUffaPriorityDAO;
        this.doctorHolidaysDAO = doctorHolidaysDAO;
        this.agentBroker = agentBroker;
        this.aiReschedulingOrchestrationService = aiReschedulingOrchestrationService;
        this.decisionAlgorithmService = decisionAlgorithmService;
        this.objectMapper = objectMapper;
    }

    public AiScheduleComparisonResponseDto generateScheduleComparison(LocalDate startDate, LocalDate endDate) {
        Schedule standardSchedule = schedulerController.createSchedule(startDate, endDate);
        if (standardSchedule == null) {
            return null;
        }

        String toonPayload = buildToonPayload(startDate, endDate, standardSchedule.getConcreteShifts());

        CandidateData standardCandidate = buildStandardCandidate(standardSchedule);
        List<CandidateData> aiCandidates = requestAiCandidates(toonPayload);
        List<CandidateData> candidates = new ArrayList<>();
        candidates.add(standardCandidate);
        candidates.addAll(aiCandidates);
        Map<String, AiScheduleCandidateMetrics> normalizedMetrics = normalizeMetrics(candidates);

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

        AiScheduleDecisionOutcome outcome = selectDecisionOutcome(candidates, normalizedMetrics);
        return comparisonMapper.toDto(comparisonCandidates, outcome);
    }

    private String buildToonPayload(LocalDate startDate, LocalDate endDate, List<ConcreteShift> concreteShifts) {
        List<Doctor> doctors = doctorDAO.findAll();
        List<DoctorUffaPriority> priorities = doctorUffaPriorityDAO.findAll();
        List<DoctorHolidays> doctorHolidays = doctorHolidaysDAO.findAll();
        List<ToonActiveConstraint> activeConstraints = new ArrayList<>();
        List<ToonFeedback> feedbacks = new ArrayList<>();

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

    private CandidateData buildStandardCandidate(Schedule schedule) {
        DecisionMetricValues metrics = buildStandardMetrics(schedule);
        AiScheduleResponseDto responseDto = buildStandardResponseDto(schedule, metrics);
        String rawJson = serializeResponse(responseDto);
        return new CandidateData(
                "standard",
                schedule.getId(),
                ScheduleCandidateType.STANDARD,
                rawJson,
                metrics
        );
    }

    private List<CandidateData> requestAiCandidates(String toonPayload) {
        String instructions = buildMultiVariantInstructions();
        AiBrokerRequest request = new AiBrokerRequest(toonPayload, instructions, UUID.randomUUID().toString());
        AiScheduleVariantsResponse response = agentBroker.requestSchedule(request);
        List<CandidateData> candidates = new ArrayList<>();
        for (VariantDefinition definition : VARIANT_DEFINITIONS) {
            AiScheduleResponse variant = response.getVariant(definition.label);
            if (variant == null) {
                throw AiProtocolException.schemaMismatch(
                        "AI response missing variant " + definition.label,
                        null
                );
            }
            DecisionMetricValues metrics = buildAiMetrics(variant);
            AiScheduleResponseDto responseDto = buildAiResponseDto(variant);
            String rawJson = serializeResponse(responseDto);
            candidates.add(new CandidateData(definition.candidateId, null, definition.type, rawJson, metrics));
        }
        return Collections.unmodifiableList(candidates);
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
            return 1.0;
        }
        return (double) totalAssigned / totalRequired;
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
        AiScheduleCandidateMetrics selected = decisionAlgorithmService.selectPreferred(metrics);
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
            double coverage = normalize(metrics.getCoverage(), minCoverage, maxCoverage, false);
            double uffaBalance = normalize(metrics.getUffaBalance(), minUffaBalance, maxUffaBalance, false);
            double sentiment = normalize(metrics.getSentimentTransitions(), minSentiment, maxSentiment, false);
            double upDelta = normalize(metrics.getUpDelta(), minUpDelta, maxUpDelta, true);
            double variance = normalize(metrics.getVarianceDelta(), minVariance, maxVariance, true);
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
        return normalized;
    }

    private double normalize(double value, double min, double max, boolean lowerIsBetter) {
        if (Double.compare(min, max) == 0) {
            return 1.0;
        }
        double normalized = (value - min) / (max - min);
        if (lowerIsBetter) {
            normalized = 1.0 - normalized;
        }
        if (normalized < 0.0) {
            return 0.0;
        }
        if (normalized > 1.0) {
            return 1.0;
        }
        return normalized;
    }

    private String buildMultiVariantInstructions() {
        StringBuilder builder = new StringBuilder("Generate three schedule variants in a single JSON response.\n");
        builder.append("Use the labels EMPATHETIC, EFFICIENT, BALANCED under the \"variants\" object.\n");
        for (VariantDefinition definition : VARIANT_DEFINITIONS) {
            builder.append("- ").append(definition.label).append(": ").append(definition.intent).append("\n");
        }
        builder.append("Return only the JSON object, no extra text.");
        return builder.toString();
    }

    private static class VariantDefinition {
        private final String label;
        private final String candidateId;
        private final ScheduleCandidateType type;
        private final String intent;

        private VariantDefinition(String label,
                                  String candidateId,
                                  ScheduleCandidateType type,
                                  String intent) {
            this.label = Objects.requireNonNull(label, "label");
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.type = Objects.requireNonNull(type, "type");
            this.intent = Objects.requireNonNull(intent, "intent");
        }
    }

    private static class CandidateData {
        private final String candidateId;
        private final Long scheduleId;
        private final ScheduleCandidateType type;
        private final String rawScheduleJson;
        private final DecisionMetricValues rawMetrics;

        private CandidateData(String candidateId,
                              Long scheduleId,
                              ScheduleCandidateType type,
                              String rawScheduleJson,
                              DecisionMetricValues rawMetrics) {
            this.candidateId = Objects.requireNonNull(candidateId, "candidateId");
            this.scheduleId = scheduleId;
            this.type = Objects.requireNonNull(type, "type");
            this.rawScheduleJson = rawScheduleJson;
            this.rawMetrics = Objects.requireNonNull(rawMetrics, "rawMetrics");
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
}
