package org.cswteams.ms3.ai.protocol.converter;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.AiScheduleSemanticValidator;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleVariantsResponseDto;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dao.TaskDAO;
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
    private final TaskDAO taskDAO;

    // Pattern to extract shift ID and date from AI's shift_id format (S_<id>_<yyyyMMdd>)
    private static final Pattern SHIFT_ID_PATTERN = Pattern.compile("^S_([A-Za-z0-9]+)_(\\d{8})$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    @Autowired
    public AiScheduleConverterService(AiScheduleJsonParser jsonParser,
                                      AiScheduleSemanticValidator semanticValidator,
                                      DoctorDAO doctorDAO,
                                      ShiftDAO shiftDAO,
                                      TaskDAO taskDAO) {
        this.jsonParser = jsonParser;
        this.semanticValidator = semanticValidator;
        this.doctorDAO = doctorDAO;
        this.shiftDAO = shiftDAO;
        this.taskDAO = taskDAO;
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

        // 2. Perform conversion-oriented semantic validation on the DTO
        // (metadata can be missing in comparison payloads; assignments are what we need to persist)
        semanticValidator.validateForConversion(aiResponseDto);

        // 3. Map AiAssignmentDto to ConcreteShift entities with DoctorAssignments
        return mapAssignmentsToConcreteShifts(aiResponseDto.assignments);
    }

    /**
     * Converts a multi-variant JSON response into internal ConcreteShift lists for each variant.
     *
     * @param jsonResponse The raw JSON string from the AI containing variants.
     * @return A map keyed by variant label with the corresponding ConcreteShift entities.
     * @throws AiProtocolException if parsing, validation, or conversion fails.
     */
    public Map<String, List<ConcreteShift>> convertVariants(String jsonResponse) {
        AiScheduleVariantsResponseDto variantsDto = jsonParser.parseVariants(jsonResponse);
        Map<String, List<ConcreteShift>> converted = new LinkedHashMap<>();
        for (String label : AiScheduleJsonParser.requiredVariantLabels()) {
            AiScheduleResponseDto variant = variantsDto.variants.get(label);
            if (variant == null) {
                throw AiProtocolException.schemaMismatch("AI response missing variant " + label, null);
            }
            semanticValidator.validate(variant);
            converted.put(label, mapAssignmentsToConcreteShifts(variant.assignments));
        }
        return converted;
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

        for (AiAssignmentDto aiAssignment : aiAssignments) {
            // a. Parse shiftId
            ParsedShiftId parsedShiftId = parseAiShiftId(aiAssignment.shiftId);
            String shiftTemplateIdStr = parsedShiftId.shiftTemplateId;
            LocalDate assignmentDate = parsedShiftId.assignmentDate;

            // b. Resolve Doctor
            Doctor doctor = doctorDAO.findById(aiAssignment.doctorId.longValue());
            if (doctor == null) {
                throw AiProtocolException.entityNotFound("Doctor with ID " + aiAssignment.doctorId + " not found.");
            }

            // c. Resolve Shift (template)
            Optional<Shift> shiftTemplateOptional = shiftDAO.findById(Long.parseLong(shiftTemplateIdStr));
            if (shiftTemplateOptional.isEmpty()) {
                throw AiProtocolException.entityNotFound("Shift template with ID " + shiftTemplateIdStr + " not found.");
            }
            Shift shiftTemplate = shiftTemplateOptional.get();
            
            // d. Find/Create ConcreteShift
            String concreteShiftKey = shiftTemplateIdStr + "_" + assignmentDate.format(DATE_FORMATTER);
            ConcreteShift concreteShift = concreteShiftsMap.computeIfAbsent(concreteShiftKey, k -> {
                // ConcreteShift constructor expects date in long epoch format
                return new ConcreteShift(assignmentDate.toEpochDay(), shiftTemplate);
            });

            // e. Resolve Task using shift capacities and already assigned slots in this concrete shift
            Seniority roleCovered = aiAssignment.roleCovered != null ? aiAssignment.roleCovered : doctor.getSeniority();
            Task task = resolveTaskForAssignment(shiftTemplate, roleCovered, concreteShift);

            // f. Create DoctorAssignment
            ConcreteShiftDoctorStatus status = ConcreteShiftDoctorStatus.ON_DUTY; // Default status
            // The AI provides isForced and violationNote. If needed, this could influence the status
            // or be stored in a separate log/entity. For now, we assume ON_DUTY.

            DoctorAssignment doctorAssignment = new DoctorAssignment(doctor, status, concreteShift, task);
            concreteShift.getDoctorAssignmentList().add(doctorAssignment);
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
    private Task resolveTaskForAssignment(Shift shiftTemplate,
                                          Seniority doctorSeniority,
                                          ConcreteShift concreteShift) {
        Seniority lookupSeniority = doctorSeniority;
        List<Task> candidateTasks = collectTasksForSeniority(shiftTemplate, lookupSeniority);
        if (candidateTasks.isEmpty() && doctorSeniority == Seniority.SPECIALIST_SENIOR) {
            // In scheduling, senior doctors can often satisfy junior slots.
            lookupSeniority = Seniority.SPECIALIST_JUNIOR;
            candidateTasks = collectTasksForSeniority(shiftTemplate, lookupSeniority);
        }

        if (candidateTasks.isEmpty()) {
            throw AiProtocolException.taskResolutionError(
                    "No task found for doctor seniority " + doctorSeniority + " in shift template " + shiftTemplate.getId()
            );
        }

        Set<Task> distinctTasks = new HashSet<>(candidateTasks);
        if (distinctTasks.size() == 1) {
            return candidateTasks.get(0);
        }

        // Ambiguous case: allocate deterministically using remaining capacity per task for the given seniority.
        Map<Long, Integer> requiredByTaskId = new HashMap<>();
        for (QuantityShiftSeniority qss : shiftTemplate.getQuantityShiftSeniority()) {
            if (qss.getTask() == null || qss.getTask().getId() == null || qss.getSeniorityMap() == null) {
                continue;
            }
            Integer required = qss.getSeniorityMap().get(lookupSeniority);
            if (required != null && required > 0) {
                requiredByTaskId.merge(qss.getTask().getId(), required, Integer::sum);
            }
        }

        Map<Long, Integer> assignedByTaskId = new HashMap<>();
        if (concreteShift.getDoctorAssignmentList() != null) {
            for (DoctorAssignment current : concreteShift.getDoctorAssignmentList()) {
                if (current == null || current.getTask() == null || current.getTask().getId() == null) {
                    continue;
                }
                assignedByTaskId.merge(current.getTask().getId(), 1, Integer::sum);
            }
        }

        Task selected = null;
        int bestRemaining = Integer.MIN_VALUE;
        int bestAssigned = Integer.MAX_VALUE;
        for (Task task : distinctTasks) {
            Long taskId = task.getId();
            int required = requiredByTaskId.getOrDefault(taskId, 0);
            int assigned = assignedByTaskId.getOrDefault(taskId, 0);
            int remaining = required - assigned;

            if (remaining > bestRemaining || (remaining == bestRemaining && assigned < bestAssigned)) {
                bestRemaining = remaining;
                bestAssigned = assigned;
                selected = task;
            }
        }

        if (selected != null) {
            return selected;
        }

        String tasks = distinctTasks.stream()
                .map(t -> t.getTaskType() == null ? String.valueOf(t.getId()) : t.getTaskType().name())
                .collect(Collectors.joining(", "));
        throw AiProtocolException.taskResolutionError(
                "Ambiguous task resolution for doctor seniority " + doctorSeniority +
                        " in shift template " + shiftTemplate.getId() + ". Candidate tasks: " + tasks
        );
    }

    private List<Task> collectTasksForSeniority(Shift shiftTemplate, Seniority seniority) {
        List<Task> candidateTasks = new ArrayList<>();
        for (QuantityShiftSeniority qss : shiftTemplate.getQuantityShiftSeniority()) {
            if (qss.getSeniorityMap() != null && qss.getSeniorityMap().containsKey(seniority)) {
                candidateTasks.add(qss.getTask());
            }
        }
        return candidateTasks;
    }


    // Helper record for parsing AI's composite shift ID
    private static class ParsedShiftId {
        private final String shiftTemplateId;
        private final LocalDate assignmentDate;

        public ParsedShiftId(String shiftTemplateId, LocalDate assignmentDate) {
            this.shiftTemplateId = shiftTemplateId;
            this.assignmentDate = assignmentDate;
        }

        public String shiftTemplateId() {
            return shiftTemplateId;
        }

        public LocalDate assignmentDate() {
            return assignmentDate;
        }
    }
}
