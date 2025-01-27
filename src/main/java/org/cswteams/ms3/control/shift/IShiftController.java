package org.cswteams.ms3.control.shift;

import org.cswteams.ms3.dto.shift.ShiftDTOIn;
import org.cswteams.ms3.dto.shift.ShiftDTOOut;
import org.cswteams.ms3.dto.shift.ShiftServiceNameDTOIn;
import org.cswteams.ms3.exception.DatabaseException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This interface is responsible for managing at business level the shift definitions created by a configurator
 */
public interface IShiftController {

    /**
     * Retrieves all the shifts
     * @return A list of DTOs representing the shifts
     */
    List<ShiftDTOOut> getAllShifts() ;

    /**
     * Retrieves all the shifts relative to the service label
     * @return A list of DTOs representing the shifts searched
     */
    List<ShiftDTOOut> getShiftsOfService(ShiftServiceNameDTOIn serviceName) ;

    /**
     * Creates a new shift
     * @param shift A DTO with all the necessary information for creating the shift
     * @return A DTO representing the shift
     */
    ShiftDTOOut createShift(ShiftDTOIn shift) ;

    /**
     * Soft deletes a shift by marking it as deleted.
     * @param id The ID of the shift to soft delete.
     */
    boolean deleteShift(@NotNull Long id) throws DatabaseException;
}
