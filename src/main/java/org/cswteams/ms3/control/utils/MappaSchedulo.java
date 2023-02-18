package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.ScheduloDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Utente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaSchedulo {

    public static ScheduloDTO scheduloToDTO(Schedule schedule) {
        return new ScheduloDTO(ConvertitoreData.daStandardVersoTestuale(schedule.getStartDate().toString()), ConvertitoreData.daStandardVersoTestuale(schedule.getEndDate().toString()), schedule.isIllegal(),schedule.getId());
    }

    public static List<ScheduloDTO> scheduloEntitytoDTO(List<Schedule> schedulazioni){
        List<ScheduloDTO> scheduliDTO = new ArrayList<>();
        for (Schedule entity: schedulazioni){
            scheduliDTO.add(scheduloToDTO(entity));
        }
        return scheduliDTO;
    }



}
