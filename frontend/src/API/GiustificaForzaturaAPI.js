export class GiustificaForzaturaAPI {

  async caricaGiustifica(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };
    const url = "/api/justify/uploadJustification";
    const response = await fetch(url, requestOptions);
    return response.status;
  }
}
