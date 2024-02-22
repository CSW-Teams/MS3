package org.cswteams.ms3.dto;


import lombok.Data;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
public class GiustificazioneForzaturaVincoliDTO {

    private String message;

    private String utenteGiustificatoreId;

    private int giorno;
    private int mese;
    private int anno;

    private TimeSlot timeSlot;
    private Set<Long> utentiAllocati;
    private MedicalServiceDTO servizio;


    //private Set<Waiver> liberatorie;

    public GiustificazioneForzaturaVincoliDTO() {

    }

    public GiustificazioneForzaturaVincoliDTO(String message, String utenteGiustificatoreId, int giorno, int mese, int anno,
                                              TimeSlot timeSlot, Set<Long> utentiAllocati, MedicalServiceDTO servizio) {

        this.message = message;
        this.utenteGiustificatoreId = utenteGiustificatoreId;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.timeSlot = timeSlot;
        this.utentiAllocati = utentiAllocati;
        this.servizio = servizio;

    }



}
