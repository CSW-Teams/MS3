

export class RichiestaRimozioneDaTurnoAPI {
  /**
   * Richiede al backend il salvataggio di una nuova richiesta di rimozione da un turno
   */
  async postRequest(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetch('/api/assegnazioneturni/richiesterimozione', requestOptions);

    return response;
  }

  async getAllRequests() {
    const response = await fetch('/api/assegnazioneturni/richiesterimozione')
    const body = await response.json();

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.id = body.assegnazioneTurnoId;
      request.idUser = body.utenteId;
      request.justification = body.descrizione;
      request.outcome = body.esito;

      requests[i] = request;
    }

    return requests;
  }
}
