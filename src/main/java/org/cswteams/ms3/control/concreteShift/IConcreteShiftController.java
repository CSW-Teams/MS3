package org.cswteams.ms3.control.concreteShift;

import org.cswteams.ms3.dto.concreteshift.GetAllConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.exception.ConcreteShiftException;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Set;

public interface IConcreteShiftController {

    Set<GetAllConcreteShiftDTO> getAllConcreteShifts() throws ParseException;

    ConcreteShift createNewConcreteShift(RegisterConcreteShiftDTO c) throws ConcreteShiftException;

    Set<GetAllConcreteShiftDTO> getSingleDoctorConcreteShifts(Long idUtente) throws ParseException;

    ConcreteShift getConcreteShiftById(long idAssegnazione);

    /**
     * Substitute a doctor assigned to a concrete shift (i.e. is either <i>on duty</i> or <i>on call</i> for it)
     * with a doctor that is <i>on call</i> for it.
     *
     * @param concreteShift    the concrete shift the substitution is referring to
     * @param requestingDoctor doctor requesting the substitution
     * @param substituteDoctor <i>on-call</i>-doctor that will substitute <code>requestingDoctor</code>
     * @return object related to the concrete shift
     * @throws ConcreteShiftException if the requesting doctor is neither <i>on duty</i> nor <i>on call</i>,
     *                                    or if the substitute doctor is not <i>on call</i> for the concrete shift.
     */
    ConcreteShift substituteAssignedDoctor(@NotNull ConcreteShift concreteShift, @NotNull Doctor requestingDoctor, @NotNull Doctor substituteDoctor) throws ConcreteShiftException;
}
