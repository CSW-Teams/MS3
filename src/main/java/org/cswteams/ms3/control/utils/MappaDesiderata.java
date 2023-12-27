package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.preferences.PreferenceDTOOut;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.Doctor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MappaDesiderata {

    public static Preference desiderataDtoToEntity(PreferenceDTOOut dto, List<Doctor> doctors){
        return new Preference(LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay()), dto.getTurnKinds(), doctors);
    }

    public static List<Preference> desiderataDtoToEntity(List<PreferenceDTOOut> dtos, List<Doctor> doctors){
        List<Preference> desiderata = new ArrayList<>();
        for(PreferenceDTOOut dto: dtos){
            desiderata.add(desiderataDtoToEntity(dto, doctors));
        }
        return desiderata;
    }

    public static PreferenceDTOOut desiderataToDto(Preference entity){
        return new PreferenceDTOOut(entity.getId(), entity.getDate().getDayOfMonth(), entity.getDate().getMonthValue(),entity.getDate().getYear(), entity.getTimeSlots());
    }

    public static List<PreferenceDTOOut> desiderataToDto(List<Preference> entities){
        List<PreferenceDTOOut> preferenceDTOOut = new ArrayList<>();

        for(Preference preference : entities){
            preferenceDTOOut.add(desiderataToDto(preference));
        }
        return preferenceDTOOut;
    }
}
