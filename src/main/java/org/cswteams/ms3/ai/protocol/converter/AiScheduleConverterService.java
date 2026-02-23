package org.cswteams.ms3.ai.protocol.converter;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.AiScheduleSemanticValidator;
import org.cswteams.ms3.ai.protocol.ValidationError;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiRoleValidationScratchpadItemDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiScheduleConverterService {

    private final AiScheduleJsonParser jsonParser;
    private final AiScheduleSemanticValidator semanticValidator;
    private final DoctorDAO doctorDAO;
    private final ShiftDAO shiftDAO;

    // Pattern to extract shift ID and date from AI's shift_id format (S_<id>_<yyyyMMdd>)
    private static final Pattern SHIFT_ID_PATTERN = Pattern.compile("^S_([A-Za-z0-9]+)_(\\d{8})$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    @Autowired
    public AiScheduleConverterService(AiScheduleJsonParser jsonParser,
                                      AiScheduleSemanticValidator semanticValidator,
                                      DoctorDAO doctorDAO,
                                      ShiftDAO shiftDAO) {
        this.jsonParser = jsonParser;
        this.semanticValidator = semanticValidator;
        this.doctorDAO = doctorDAO;
        this.shiftDAO = shiftDAO;
    }

    /**
     * Converts a raw JSON response from the AI into a list of internal ConcreteShift entities
     * with associated DoctorAssignments.
     *
     * @param jsonResponse The raw JSON string from the AI.
     * @return A list of ConcreteShift entities representing the AI-generated schedule.
     * @throws AiProtocolException if parsing, validation, or conversion fails.
     */
    public List<ConcreteShift> convert(String jsonResponse) {
        // 1. Parse the JSON response into AiScheduleResponseDto
        AiScheduleResponseDto aiResponseDto = jsonParser.parse(jsonResponse);

        // 2. Perform semantic validation on the DTO
        semanticValidator.validate(aiResponseDto);

        // 3. Validate role scratchpad against known doctors from run context
        validateRoleValidationScratchpadCandidates(aiResponseDto);

        // 4. Map AiAssignmentDto to ConcreteShift entities with DoctorAssignments
        return mapAssignmentsToConcreteShifts(aiResponseDto.assignments);
    }

    private void validateRoleValidationScratchpadCandidates(AiScheduleResponseDto aiResponseDto) {
        if (aiResponseDto == null
                || aiResponseDto.metadata == null
                || aiResponseDto.metadata.roleValidationScratchpad == null
                || aiResponseDto.metadata.roleValidationScratchpad.isEmpty()) {
            return;
        }

        Set<Long> candidateIds = aiResponseDto.metadata.roleValidationScratchpad.stream()
                .filter(Objects::nonNull)
                .map(item -> item.candidateDoctorIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .collect(Collectors.toSet());

        if (candidateIds.isEmpty()) {
            return;
        }

        Map<Long, Seniority> knownDoctorSeniorityById = doctorDAO.findAllById(candidateIds).stream()
                .collect(Collectors.toMap(Doctor::getId, Doctor::getSeniority));

        for (int scratchpadIndex = 0; scratchpadIndex < aiResponseDto.metadata.roleValidationScratchpad.size(); scratchpadIndex++) {
            AiRoleValidationScratchpadItemDto scratchpadItem = aiResponseDto.metadata.roleValidationScratchpad.get(scratchpadIndex);
            if (scratchpadItem == null || scratchpadItem.candidateDoctorIds == null) {
                continue;
            }
            Seniority roleRequired = Seniority.valueOf(scratchpadItem.roleRequired);
            for (int candidateIndex = 0; candidateIndex < scratchpadItem.candidateDoctorIds.size(); candidateIndex++) {
                Integer candidateId = scratchpadItem.candidateDoctorIds.get(candidateIndex);
                if (candidateId == null) {
                    continue;
                }
                Seniority knownSeniority = knownDoctorSeniorityById.get(candidateId.longValue());
                String path = "$.metadata.role_validation_scratchpad[" + scratchpadIndex + "].candidate_doctor_ids[" + candidateIndex + "]";
                if (knownSeniority == null) {
                    throw AiProtocolException.schemaMismatch(
                            "AI response violates the backend-provided role_validation_scratchpad contract: candidate_doctor_ids contain unknown doctor IDs",
                            List.of(new ValidationError(path, "doctor_id=" + candidateId + " is not available in current run context")),
                            null
                    );
                }
                if (knownSeniority != roleRequired) {
                    throw AiProtocolException.schemaMismatch(
                            "AI response violates the backend-provided role_validation_scratchpad contract: candidate_doctor_ids do not match role_required",
                            List.of(new ValidationError(path,
                                    "doctor_id=" + candidateId + " has seniority=" + knownSeniority + " but role_required=" + roleRequired)),
                            null
                    );
                }
            }
        }
    }

    /**
     * Maps a list of AI assignment DTOs to internal ConcreteShift entities.
     *
     * @param aiAssignments A list of AiAssignmentDto from the AI response.
     * @return A list of ConcreteShift entities.
     * @throws AiProtocolException if an entity is not found or a task cannot be resolved.
     */
    private List<ConcreteShift> mapAssignmentsToConcreteShifts(List<AiAssignmentDto> aiAssignments) {
        // Map to hold ConcreteShifts, keyed by a combination of Shift ID and date (long epoch format)
        // This allows grouping assignments for the same concrete shift
        Map<String, ConcreteShift> concreteShiftsMap = new HashMap<>();
        Map<String, ShiftCoverageAccumulator> coverageByConcreteShift = new HashMap<>();
        List<ValidationError> validationErrors = new ArrayList<>();

        for (int index = 0; index < aiAssignments.size(); index++) {
            AiAssignmentDto aiAssignment = aiAssignments.get(index);
            // a. Parse shiftId
            ParsedShiftId parsedShiftId = parseAiShiftId(aiAssignment.shiftId);
            String shiftTemplateIdStr = parsedShiftId.shiftTemplateId;
            LocalDate assignmentDate = parsedShiftId.assignmentDate;

            // b. Resolve Doctor
            Doctor doctor = doctorDAO.findById(aiAssignment.doctorId.longValue());
            if (doctor == null) {
                throw AiProtocolException.entityNotFound("Doctor with ID " + aiAssignment.doctorId + " not found.");
            }
            if (!doctor.getSeniority().equals(aiAssignment.roleCovered)) {
                validationErrors.add(new ValidationError(
                        "$.assignments[" + index + "].role_covered",
                        "expected seniority=" + doctor.getSeniority() + " but assigned role=" + aiAssignment.roleCovered
                ));
            }

            // c. Resolve Shift (template)
            Optional<Shift> shiftTemplateOptional = shiftDAO.findById(Long.parseLong(shiftTemplateIdStr));
            if (shiftTemplateOptional.isEmpty()) {
                throw AiProtocolException.entityNotFound("Shift template with ID " + shiftTemplateIdStr + " not found.");
            }
            Shift shiftTemplate = shiftTemplateOptional.get();
            
            // d. Resolve Task (Crucial step based on assumption)
            Task task = resolveTaskForAssignment(shiftTemplate, aiAssignment.roleCovered);

            // e. Find/Create ConcreteShift
            String concreteShiftKey = shiftTemplateIdStr + "_" + assignmentDate.format(DATE_FORMATTER);
            ConcreteShift concreteShift = concreteShiftsMap.computeIfAbsent(concreteShiftKey, k -> {
                // ConcreteShift constructor expects date in long epoch format
                return new ConcreteShift(assignmentDate.toEpochDay(), shiftTemplate);
            });
            ShiftCoverageAccumulator coverageAccumulator = coverageByConcreteShift.computeIfAbsent(
                    concreteShiftKey,
                    k -> ShiftCoverageAccumulator.fromShiftTemplate(shiftTemplate, aiAssignment.shiftId)
            );

            // f. Create DoctorAssignment
            AssignmentStatusResolution statusResolution = resolveAssignmentStatus(aiAssignment);

            DoctorAssignment doctorAssignment = new DoctorAssignment(doctor, statusResolution.persistenceStatus, concreteShift, task);
            concreteShift.getDoctorAssignmentList().add(doctorAssignment);
            coverageAccumulator.registerAssignment(statusResolution.coverageStatuses, doctor.getSeniority());
        }

        for (ShiftCoverageAccumulator coverageAccumulator : coverageByConcreteShift.values()) {
            coverageAccumulator.addCoverageErrors(validationErrors);
        }

        if (!validationErrors.isEmpty()) {
            throw AiProtocolException.schemaMismatch(
                    "AI response violates minimum doctors/seniority shift constraints",
                    validationErrors,
                    null
            );
        }

        return new ArrayList<>(concreteShiftsMap.values());
    }

    /**
     * Extracts the shift template ID and assignment date from the AI's shift_id string.
     * Expected format: S_<id>_<yyyyMMdd>
     *
     * @param aiShiftId The shift_id string from AiAssignmentDto.
     * @return A ParsedShiftId object containing the extracted ID and date.
     * @throws AiProtocolException if the shift_id format is invalid.
     */
    private ParsedShiftId parseAiShiftId(String aiShiftId) {
        Matcher matcher = SHIFT_ID_PATTERN.matcher(aiShiftId);
        if (!matcher.matches()) {
            throw AiProtocolException.invalidFormat("AI shift_id format is invalid: " + aiShiftId + ". Expected S_<id>_<yyyyMMdd>.");
        }
        String shiftTemplateId = matcher.group(1);
        String datePart = matcher.group(2);
        
        try {
            LocalDate assignmentDate = LocalDate.parse(datePart, DATE_FORMATTER);
            return new ParsedShiftId(shiftTemplateId, assignmentDate);
        } catch (DateTimeParseException e) {
            throw AiProtocolException.invalidFormat("Invalid date format in AI shift_id: " + aiShiftId + ". Expected yyyyMMdd.", e);
        }
    }

    /**
     * Resolves the specific Task for a DoctorAssignment based on the Shift template
     * and the doctor's Seniority (role covered).
     *
     * @param shiftTemplate The Shift template entity.
     * @param doctorSeniority The seniority of the doctor being assigned.
     * @return The resolved Task entity.
     * @throws AiProtocolException if a unique task cannot be resolved for the given shift and seniority.
     */
    private Task resolveTaskForAssignment(Shift shiftTemplate, Seniority doctorSeniority) {
        List<Task> candidateTasks = new ArrayList<>();

        for (QuantityShiftSeniority qss : shiftTemplate.getQuantityShiftSeniority()) {
            if (qss.getSeniorityMap() != null && qss.getSeniorityMap().containsKey(doctorSeniority)) {
                // If the shift template requires a doctor of this seniority for this QSS entry's task
                candidateTasks.add(qss.getTask());
            }
        }

        if (candidateTasks.isEmpty()) {
            throw AiProtocolException.taskResolutionError(
                    "No task found for doctor seniority " + doctorSeniority + " in shift template " + shiftTemplate.getId()
            );
        }

        // Return the unique task
        return candidateTasks.get(0);
    }

    // Helper record for parsing AI's composite shift ID
    private static class ParsedShiftId {
        private final String shiftTemplateId;
        private final LocalDate assignmentDate;

        public ParsedShiftId(String shiftTemplateId, LocalDate assignmentDate) {
            this.shiftTemplateId = shiftTemplateId;
            this.assignmentDate = assignmentDate;
        }

    }

    private AssignmentStatusResolution resolveAssignmentStatus(AiAssignmentDto aiAssignment) {
        if (aiAssignment.assignmentStatus == null) {
            throw AiProtocolException.invalidFormat(
                    "Missing assignment_status in AI response assignment. Expected ON_DUTY or ON_CALL."
            );
        }
        if (aiAssignment.assignmentStatus == ConcreteShiftDoctorStatus.ON_DUTY
                || aiAssignment.assignmentStatus == ConcreteShiftDoctorStatus.ON_CALL) {
            return AssignmentStatusResolution.explicit(aiAssignment.assignmentStatus);
        }
        throw AiProtocolException.invalidFormat(
                "Unsupported assignment_status in AI response: " + aiAssignment.assignmentStatus
                        + ". Expected ON_DUTY or ON_CALL."
        );
    }

    private static class AssignmentStatusResolution {
        private final ConcreteShiftDoctorStatus persistenceStatus;
        private final EnumSet<ConcreteShiftDoctorStatus> coverageStatuses;

        private AssignmentStatusResolution(ConcreteShiftDoctorStatus persistenceStatus,
                                           EnumSet<ConcreteShiftDoctorStatus> coverageStatuses) {
            this.persistenceStatus = persistenceStatus;
            this.coverageStatuses = coverageStatuses;
        }

        private static AssignmentStatusResolution explicit(ConcreteShiftDoctorStatus assignmentStatus) {
            return new AssignmentStatusResolution(assignmentStatus, EnumSet.of(assignmentStatus));
        }

    }

    private static class ShiftCoverageAccumulator {
        private final String shiftId;
        private final Map<Seniority, Integer> requiredBySeniority;
        private final Map<ConcreteShiftDoctorStatus, Map<Seniority, Integer>> assignedByStatusAndSeniority;

        private ShiftCoverageAccumulator(String shiftId,
                                         Map<Seniority, Integer> requiredBySeniority,
                                         Map<ConcreteShiftDoctorStatus, Map<Seniority, Integer>> assignedByStatusAndSeniority) {
            this.shiftId = shiftId;
            this.requiredBySeniority = requiredBySeniority;
            this.assignedByStatusAndSeniority = assignedByStatusAndSeniority;
        }

        private static ShiftCoverageAccumulator fromShiftTemplate(Shift shiftTemplate, String shiftId) {
            Map<Seniority, Integer> requiredBySeniority = new EnumMap<>(Seniority.class);
            if (shiftTemplate.getQuantityShiftSeniority() != null) {
                for (QuantityShiftSeniority qss : shiftTemplate.getQuantityShiftSeniority()) {
                    if (qss == null || qss.getSeniorityMap() == null) {
                        continue;
                    }
                    for (Map.Entry<Seniority, Integer> entry : qss.getSeniorityMap().entrySet()) {
                        if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                            continue;
                        }
                        requiredBySeniority.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }
                }
            }
            Map<ConcreteShiftDoctorStatus, Map<Seniority, Integer>> assignedByStatusAndSeniority = new EnumMap<>(ConcreteShiftDoctorStatus.class);
            assignedByStatusAndSeniority.put(ConcreteShiftDoctorStatus.ON_DUTY, new EnumMap<>(Seniority.class));
            assignedByStatusAndSeniority.put(ConcreteShiftDoctorStatus.ON_CALL, new EnumMap<>(Seniority.class));
            return new ShiftCoverageAccumulator(shiftId, requiredBySeniority, assignedByStatusAndSeniority);
        }

        private void registerAssignment(EnumSet<ConcreteShiftDoctorStatus> statuses, Seniority seniority) {
            if (seniority == null || statuses == null || statuses.isEmpty()) {
                return;
            }
            for (ConcreteShiftDoctorStatus status : statuses) {
                Map<Seniority, Integer> assignedBySeniority = assignedByStatusAndSeniority.get(status);
                if (assignedBySeniority == null) {
                    continue;
                }
                assignedBySeniority.merge(seniority, 1, Integer::sum);
            }
        }

        private void addCoverageErrors(List<ValidationError> errors) {
            addCoverageErrorsForStatus(errors, ConcreteShiftDoctorStatus.ON_DUTY);
            addCoverageErrorsForStatus(errors, ConcreteShiftDoctorStatus.ON_CALL);
        }

        private void addCoverageErrorsForStatus(List<ValidationError> errors, ConcreteShiftDoctorStatus status) {
            Map<Seniority, Integer> assignedBySeniority = assignedByStatusAndSeniority.getOrDefault(status, Collections.emptyMap());
            for (Map.Entry<Seniority, Integer> requiredEntry : requiredBySeniority.entrySet()) {
                int assigned = assignedBySeniority.getOrDefault(requiredEntry.getKey(), 0);
                if (assigned < requiredEntry.getValue()) {
                    errors.add(new ValidationError(
                            "$.assignments",
                            "shift_id=" + shiftId + " assignment_status=" + status
                                    + " requires at least " + requiredEntry.getValue()
                                    + " doctors with seniority=" + requiredEntry.getKey()
                                    + " but got " + assigned
                    ));
                }
            }
        }
    }
}
