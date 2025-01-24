package org.cswteams.ms3.control.shift;

import org.cswteams.ms3.dao.MedicalServiceDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dao.TaskDAO;
import org.cswteams.ms3.dto.shift.*;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.entity.constraint.AdditionalConstraint;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.jpa_constraints.validant.Validant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Service
public class ShiftController implements IShiftController {

    @Autowired
    private ShiftDAO shiftDAO;

    @Autowired
    private MedicalServiceDAO medicalServiceDAO;
    @Autowired
    private TaskDAO taskDAO;

    private ShiftDTOOut convertShiftToDTO(Shift shift) {
        Set<String> daysOfWeek = new HashSet<>();
        for (DayOfWeek dayOfWeek : shift.getDaysOfWeek()) {
            daysOfWeek.add(dayOfWeek.name());
        }
        MedicalServiceShiftDTO serviceShiftDTO = new MedicalServiceShiftDTO(
                shift.getMedicalService().getId(), shift.getMedicalService().getLabel()
        );

        List<QuantityShiftSeniorityDTO> quantityShiftSeniorities = new ArrayList<>();
        for (QuantityShiftSeniority entry : shift.getQuantityShiftSeniority()) {
            for (Map.Entry<Seniority, Integer> entry1 : entry.getSeniorityMap().entrySet()) {
                quantityShiftSeniorities.add(new QuantityShiftSeniorityDTO(entry.getTask().getId(), entry1.getKey().name(), entry1.getValue()));
            }
        }
        return new ShiftDTOOut(
                shift.getId(), shift.getTimeSlot().name(), shift.getStartTime().getHour(),
                shift.getStartTime().getMinute(), shift.getDuration().toMinutesPart(),
                daysOfWeek, serviceShiftDTO, quantityShiftSeniorities
        );
    }

    private Shift convertDTOToShift(ShiftDTOIn shiftDTOIn) {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        for (String dayOfWeek : shiftDTOIn.getDaysOfWeek())
            daysOfWeek.add(DayOfWeek.valueOf(dayOfWeek));

        MedicalService service;
        if (shiftDTOIn.getMedicalService().getId() != null)
            service = new MedicalService(
                    shiftDTOIn.getMedicalService().getId(),
                    List.of(),
                    shiftDTOIn.getMedicalService().getLabel().toUpperCase()
            );
        else
            service = new MedicalService(
                    List.of(),
                    shiftDTOIn.getMedicalService().getLabel().toUpperCase()
            );

        Map<Long, Map<Seniority, Integer>> quantityShiftSenitiorityMap = new HashMap<>();
        for (QuantityShiftSeniorityDTO entry : shiftDTOIn.getQuantityShiftSeniority()) {
            long task = entry.getTask();

            if (quantityShiftSenitiorityMap.containsKey(task)) {
                if (quantityShiftSenitiorityMap.get(task).containsKey(Seniority.valueOf(entry.getSeniority()))) {
                    int app = quantityShiftSenitiorityMap.get(task).get(Seniority.valueOf(entry.getSeniority()));
                    app = app + entry.getQuantity();
                    quantityShiftSenitiorityMap.get(task).put(Seniority.valueOf(entry.getSeniority()), app);
                } else {
                    quantityShiftSenitiorityMap.get(task).put(Seniority.valueOf(entry.getSeniority()), entry.getQuantity());
                }
            } else {
                quantityShiftSenitiorityMap.put(task, new HashMap<>());
                quantityShiftSenitiorityMap.get(task).put(Seniority.valueOf(entry.getSeniority()), entry.getQuantity());
            }
        }

        List<QuantityShiftSeniority> quantityShiftSeniorities = new ArrayList<>();
        for (Map.Entry<Long, Map<Seniority, Integer>> entry : quantityShiftSenitiorityMap.entrySet()) {
            Optional<Task> t = taskDAO.findById(entry.getKey());
            if (t.isPresent()) {
                QuantityShiftSeniority q = new QuantityShiftSeniority(entry.getValue(), t.get());
                quantityShiftSeniorities.add(q);
            }
        }

        ArrayList<AdditionalConstraint> constraints = new ArrayList<>();
        for (AdditionalConstraintShiftDTO dto : shiftDTOIn.getAdditionalConstraints()) {
            AdditionalConstraint constraint = new AdditionalConstraint();
            if (dto.getId() != null) constraint.setId(dto.getId());
            constraint.setDescription(dto.getDescription());
            constraint.setViolable(dto.isViolable());
        }

        if (shiftDTOIn.getId() != null)
            return new Shift(shiftDTOIn.getId(), TimeSlot.valueOf(shiftDTOIn.getTimeSlot()),
                    LocalTime.of(shiftDTOIn.getStartHour(), shiftDTOIn.getStartMinute()),
                    Duration.ofMinutes(shiftDTOIn.getDurationMinutes()),
                    daysOfWeek, service,
                    quantityShiftSeniorities, constraints
            );
        else
            return new Shift(
                    LocalTime.of(shiftDTOIn.getStartHour(), shiftDTOIn.getStartMinute()),
                    Duration.ofMinutes(shiftDTOIn.getDurationMinutes()),
                    service, TimeSlot.valueOf(shiftDTOIn.getTimeSlot()),
                    quantityShiftSeniorities, daysOfWeek, constraints
            );
    }

    @Override
    public List<ShiftDTOOut> getAllShifts() {
        List<Shift> shifts = shiftDAO.findAll();
        ArrayList<ShiftDTOOut> retVal = new ArrayList<>();

        for (Shift shift : shifts) {
            retVal.add(convertShiftToDTO(shift));
        }

        return retVal;
    }

    @Override
    @Validant
    public List<ShiftDTOOut> getShiftsOfService(@Valid ShiftServiceNameDTOIn serviceName) {

        List<Shift> shifts = shiftDAO.findAllByMedicalServiceLabel(serviceName.getServiceLabel());
        ArrayList<ShiftDTOOut> retVal = new ArrayList<>();

        for (Shift shift : shifts) {
            retVal.add(convertShiftToDTO(shift));
        }

        return retVal;
    }

    @Override
    @Validant
    public ShiftDTOOut createShift(@Valid ShiftDTOIn shift) {
        Shift shiftEntity = convertDTOToShift(shift);

        if (shiftEntity.getMedicalService().getId() == null) medicalServiceDAO.save(shiftEntity.getMedicalService());

        return convertShiftToDTO(shiftDAO.save(shiftEntity));
    }

    @Override
    public void softDeleteShift(Long id) {
        // Recupera lo shift per ID
        Optional<Shift> optionalShift = shiftDAO.findById(id);

        if (optionalShift.isPresent()) {
            Shift shift = optionalShift.get();
            // Imposta il flag "deleted" a true
            shift.setDeleted(true);
            shiftDAO.save(shift); // Persistenza nel database
        } else {
            throw new IllegalArgumentException("Shift not found with id: " + id);
        }
    }
}
