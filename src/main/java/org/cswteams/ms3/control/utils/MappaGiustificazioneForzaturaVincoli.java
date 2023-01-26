package org.cswteams.ms3.control.utils;


import lombok.Data;
import org.cswteams.ms3.dto.GiustificazioneForzaturaDto;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;

@Data
public class MappaGiustificazioneForzaturaVincoli {

    public static GiustificazioneForzaturaVincoli GiustificazioneForzaturaVincoliDtoToEntity(GiustificazioneForzaturaDto giustificazioneForzaturaDto) {
        return new GiustificazioneForzaturaVincoli(giustificazioneForzaturaDto.getMessage(),giustificazioneForzaturaDto.getUtenteGiustificante(),giustificazioneForzaturaDto.getDelibere());
    }


}
