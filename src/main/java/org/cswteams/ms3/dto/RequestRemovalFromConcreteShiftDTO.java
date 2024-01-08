package org.cswteams.ms3.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RequestRemovalFromConcreteShiftDTO {
    /**
     * <code>null</code> all'atto della creazione della richiesta
     */
    private Long idRequest;

    @NotNull
    private Long idShift;

    @NotNull
    private Long idRequestingUser;

    private Long idSubstitute;

    /**
     * Considerato solo all'atto della risoluzione della richiesta da parte del <i>Pianificatore</i>
     */
    @NotNull
    private boolean outcome;

    @NotNull
    @NotEmpty
    private String justification;

    @NotNull
    private boolean examined;

    private byte[] file;

    public RequestRemovalFromConcreteShiftDTO() {
    }

    public RequestRemovalFromConcreteShiftDTO(Long richiestaRimozioneDaTurnoId, Long idShift, Long idRequestingUser, boolean outcome, String justification, byte[] file, boolean examined) {
        this.idRequest = richiestaRimozioneDaTurnoId;
        this.idShift = idShift;
        this.idRequestingUser = idRequestingUser;
        this.outcome = outcome;
        this.justification = justification;
        this.file = file;
        this.examined = examined;
    }

    public RequestRemovalFromConcreteShiftDTO(Long richiestaRimozioneDaTurnoId, Long idShift, Long idRequestingUser, Long idSubstitute, boolean outcome, String justification, byte[] file, boolean examined) {
        this.idRequest = richiestaRimozioneDaTurnoId;
        this.idShift = idShift;
        this.idRequestingUser = idRequestingUser;
        this.idSubstitute = idSubstitute;
        this.outcome = outcome;
        this.justification = justification;
        this.file = file;
        this.examined = examined;
    }

    public RequestRemovalFromConcreteShiftDTO(Long idShift, Long idRequestingUser, boolean outcome, String justification) {
        this.idShift = idShift;
        this.idRequestingUser = idRequestingUser;
        this.outcome = outcome;
        this.justification = justification;
    }
}
