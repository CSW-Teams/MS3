import { TurnoAPI } from "./TurnoAPI";
import {blue, red, teal} from "@material-ui/core/colors";
import { AssignedShift, SchedulableType } from "./Schedulable";

export  class LoginAPI {

  /**
   * Richiede al backend l'autenticazione di un utente.
   * @param {*} credenziali
   * @returns La risposta del backend TODO
   *
   */
  async postLogin(credenziali) {

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(credenziali)
    };

    const response = await fetch('/api/login/', requestOptions);

    return response;
  }
}

