import {fetchWithAuth} from "../utils/fetchWithAuth";

export class GiustificaForzaturaAPI {

  async caricaGiustifica(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };
    const url = "/api/justify/uploadJustification";
    const response = await fetchWithAuth(url, requestOptions);
    return response.status;
  }
}
