

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

  async getAllPendingRequests() {
    const response = await fetch('/api/assegnazioneturni/richiesterimozione/pendenti')
    const body = await response.json();

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.id = body[i].assegnazioneTurnoId;
      request.idUser = body[i].utenteId;
      request.justification = body[i].descrizione;
      request.outcome = body[i].esito;

      requests[i] = request;
    }

    return requests;
  }
}
