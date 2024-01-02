package org.cswteams.ms3.dto.concreteshift;

import lombok.Getter;
import org.cswteams.ms3.dto.user.UserDTO;

import java.util.HashSet;
import java.util.Set;

/**
 * Passed from server to client
 */
@Getter
public class GetAllConcreteShiftDTO {

    private final Long id;
    private final Long shiftID;
    private final long startDateTime;
    private final long endDateTime;
    private final Set<UserDTO> doctorsOnDuty;
    private final Set<UserDTO> doctorsOnCall;
    private final String medicalServiceLabel;
    private final String medicalServiceTask;
    private final String timeSlot;
    private final boolean reperibilitaAttiva;
    private final Set<UserDTO> deletedDoctors;

    /**
     * Constructor of the DTO that should be sent to te client so that
     * the view of the schedule may have all the information needed to display
     * @param id ID of a shift
     * @param shiftID ID of a concrete shift
     * @param startDateTime Long that represents the starting time of the concrete shift
     * @param endDateTime Long that represents the ending time of the concrete shift
     * @param doctorsOnDuty Set of doctors which are on duty for that shift
     * @param doctorsOnCall Set of doctors which are on call for that shift
     * @param medicalServiceLabel Name of the medical service offered in the specifica concrete shift
     * @param medicalServiceTask Name of the task service offered in the specifica concrete shift
     * @param timeSlot Moment of the day in which the concrete shift takes place, Morning/Afternoon/Night
     */
    public GetAllConcreteShiftDTO(Long id,
                                  Long shiftID,
                                  long startDateTime,
                                  long endDateTime,
                                  Set<UserDTO> doctorsOnDuty,
                                  Set<UserDTO> doctorsOnCall,
                                  String medicalServiceLabel,
                                  String medicalServiceTask,
                                  String timeSlot,
                                  boolean reperibilitaAttiva) {
        this.id = id;
        this.shiftID = shiftID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.doctorsOnDuty = doctorsOnDuty;
        this.doctorsOnCall = doctorsOnCall;
        this.medicalServiceLabel = medicalServiceLabel;
        this.medicalServiceTask = medicalServiceTask;
        this.timeSlot = timeSlot;
        this.reperibilitaAttiva = reperibilitaAttiva;

        this.deletedDoctors = new HashSet<>();
    }

    /**
     * Constructor of the DTO that should be sent to te client so that
     * the view of the schedule may have all the information needed to display
     * @param id ID of a shift
     * @param shiftID ID of a concrete shift
     * @param startDateTime Long that represents the starting time of the concrete shift
     * @param endDateTime Long that represents the ending time of the concrete shift
     * @param medicalServiceLabel Name of the medical service offered in the specifica concrete shift
     * @param medicalServiceTask Name of the task service offered in the specifica concrete shift
     * @param timeSlot Moment of the day in which the concrete shift takes place, Morning/Afternoon/Night
     */
    public GetAllConcreteShiftDTO(Long id,
                                  Long shiftID,
                                  long startDateTime,
                                  long endDateTime,
                                  String medicalServiceLabel,
                                  String medicalServiceTask,
                                  String timeSlot,
                                  boolean reperibilitaAttiva) {
        this.id = id;
        this.shiftID = shiftID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.medicalServiceLabel = medicalServiceLabel;
        this.medicalServiceTask = medicalServiceTask;
        this.timeSlot = timeSlot;
        this.reperibilitaAttiva = reperibilitaAttiva;

        this.doctorsOnDuty = new HashSet<>();
        this.doctorsOnCall = new HashSet<>();
        this.deletedDoctors = new HashSet<>();
    }
}
