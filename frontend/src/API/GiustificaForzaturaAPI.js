export class GiustificaForzaturaAPI {

  async caricaGiustifica(message, utenteId) {
    let giustificazione = new Object();
    giustificazione.message = message;
    giustificazione.utenteGiustificatoreId = utenteId;
    //giustificazione.liberatoria = liberatoria;

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(giustificazione)
    };
    const url = "/api/giustifica/caricaGiustificazione";
    const response = await fetch(url, requestOptions);
    return response.status;
  }
}
