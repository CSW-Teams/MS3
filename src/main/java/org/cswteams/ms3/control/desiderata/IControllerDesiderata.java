package org.cswteams.ms3.control.desiderata;

import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.exception.DatabaseException;

import java.util.List;

public interface IControllerDesiderata {

    Preference aggiungiDesiderata(DesiderataDTO dto, long utenteId) throws DatabaseException;

    List<Preference> aggiungiDesiderate(List<DesiderataDTO> dtos, long utenteId) throws DatabaseException;

    void cancellaDesiderata(Long desiderata, long utenteId) throws DatabaseException;

    List<DesiderataDTO> getDesiderateDtoUtente(long utenteId);

    List<Preference> getDesiderateUtente(long utenteId);
}
