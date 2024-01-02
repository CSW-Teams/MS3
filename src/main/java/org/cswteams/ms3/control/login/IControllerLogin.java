package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.LoginDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;

import java.text.ParseException;
import java.util.Set;

public interface IControllerLogin {

    UtenteDTO autenticaUtente(LoginDTO loginDTO);

}
