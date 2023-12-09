

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
}
