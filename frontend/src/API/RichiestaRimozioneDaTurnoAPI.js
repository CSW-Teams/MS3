import {fetchWithAuth} from "../utils/fetchWithAuth";


export class RichiestaRimozioneDaTurnoAPI {

  parse(body) {
    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.idRequest = body[i].idRequest;
      request.idShift = body[i].idShift;
      request.idRequestingUser = body[i].idRequestingUser;
      request.idSubstitute = body[i].idSubstitute;
      request.justification = body[i].justification;
      request.examined = body[i].examined;
      request.outcome = body[i].outcome;
      request.file = body[i].file;

      requests[i] = request;
    }

    return requests;
  }

  /**
   * Richiede al backend il salvataggio di una nuova richiesta di rimozione da un turno
   */
  async postRequest(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetchWithAuth('/api/concrete-shifts/retirement-request/', requestOptions);

    return response;
  }

  async risolviRichiesta(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetchWithAuth('/api/concrete-shifts/retirement-request/resolve', requestOptions);
    return response;
  }

  async getAllRequests() {
    const response = await fetchWithAuth('/api/concrete-shifts/retirement-request/')
    const body = await response.json();
    return this.parse(body);
  }

  async getAllPendingRequests() {
    const response = await fetchWithAuth('/api/concrete-shifts/retirement-request/pending')
    const body = await response.json();
    return this.parse(body);
  }

  async getAllRequestsForUser(idUser) {
    const response = await fetchWithAuth(`/api/concrete-shifts/retirement-request/user/${idUser}`)
    const body = await response.json();
    return this.parse(body);
  }

}
