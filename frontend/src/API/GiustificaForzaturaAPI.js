export class GiustificaForzaturaAPI {

  async caricaGiustifica(message, utenteId, turno, utenti, data, servizio) {
    let giustificazione = {};
    giustificazione.message = message;
    giustificazione.utenteGiustificatoreId = utenteId;
    giustificazione.giorno = data.$d.getDate();
    giustificazione.mese = data.$d.getMonth()+1;
    giustificazione.anno = data.$d.getFullYear();
    giustificazione.utentiAllocati = utenti;
    giustificazione.tipologiaTurno = turno;
    giustificazione.servizio = servizio;
    //giustificazione.liberatoria = liberatoria;

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(giustificazione)
    };
    const url = "/api/justify/uploadJustification";
    const response = await fetch(url, requestOptions);
    return response.status;
  }
}
