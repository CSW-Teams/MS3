

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

  async risolviRichiesta(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetch('/api/assegnazioneturni/richiesterimozione/risolvi', requestOptions);
    console.log(response);
    return response;
  }

  async getAllRequests() {
    const response = await fetch('/api/assegnazioneturni/richiesterimozione/')
    const body = await response.json();
    console.log("Body della richesta:", body);

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.idRichiestaRimozioneDaTurno = body[i].idRichiestaRimozioneDaTurno;
      request.idAssegnazioneTurno = body[i].idAssegnazioneTurno;
      request.idUtenteRichiedente = body[i].idUtenteRichiedente;
      request.idUtenteSostituto = body[i].idUtenteSostituto;
      request.descrizione = body[i].descrizione;
      request.esaminata = body[i].esaminata;
      request.esito = body[i].esito;
      request.allegato = body[i].allegato;

      requests[i] = request;
    }

    return requests;
  }

  async getAllPendingRequests() {
    const response = await fetch('/api/assegnazioneturni/richiesterimozione/pendenti')
    const body = await response.json();

    const requests = [];

    for (let i = 0; i < body.length; i++) {
      const request = new Object();
      request.idRichiestaRimozioneDaTurno = body[i].idRichiestaRimozioneDaTurno;
      request.idAssegnazioneTurno = body[i].idAssegnazioneTurno;
      request.idUtenteRichiedente = body[i].idUtenteRichiedente;
      request.idUtenteSostituto = body[i].idUtenteSostituto;
      request.descrizione = body[i].descrizione;
      request.esaminata = body[i].esaminata;
      request.esito = body[i].esito;
      request.allegato = body[i].allegato;

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
      request.idRichiestaRimozioneDaTurno = body[i].idRichiestaRimozioneDaTurno;
      request.idAssegnazioneTurno = body[i].idAssegnazioneTurno;
      request.idUtenteRichiedente = body[i].idUtenteRichiedente;
      request.idUtenteSostituto = body[i].idUtenteSostituto;
      request.descrizione = body[i].descrizione;
      request.esaminata = body[i].esaminata;
      request.esito = body[i].esito;
      request.allegato = body[i].allegato;

      requests[i] = request;
    }


    return requests;
  }

}
