

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
    const response = await fetch('/api/assegnazioneturni/richiesterimozione/')
    const body = await response.json();
    console.log("Body della richesta:", body);

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.id = body[i].idRichiestaRimozioneDaTurno;
      request.idShift = body[i].idAssegnazioneTurno;
      request.idUser = body[i].idUtenteRichiedente;
      request.justification = body[i].descrizione;
      request.examinated = body[i].esaminata;
      request.outcome = body[i].esito;
      request.file = body[i].allegato;

      requests[i] = request;
    }

    return requests;
  }

  async getAllPendingRequests() {
    const response = await fetch('/api/assegnazioneturni/richiesterimozione/pendenti')
    const body = await response.json();

    console.log("Richieste pendenti:", body);

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.id = body[i].idRichiestaRimozioneDaTurno;
      request.idShift = body[i].idAssegnazioneTurno;
      request.idUser = body[i].idUtenteRichiedente;
      request.justification = body[i].descrizione;
      request.examinated = body[i].esaminata;
      request.outcome = body[i].esito;
      request.file = body[i].allegato;

      requests[i] = request;
    }

    return requests;
  }

  async getAllRequestsForUser(idUser) {
    const response = await fetch(`/api/assegnazioneturni/richiesterimozione/utente/${idUser}`)
    const body = await response.json();
    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.id = body[i].idRichiestaRimozioneDaTurno;
      request.idShift = body[i].idAssegnazioneTurno;
      request.idUser = body[i].idUtenteRichiedente;
      request.justification = body[i].descrizione;
      request.examinated = body[i].esaminata;
      request.outcome = body[i].esito;
      request.file = body[i].allegato;

      requests[i] = request;
    }

    return requests;
  }

}
