export  class LoginAPI {

  /**
   * Richiede al backend l'autenticazione di un utente.
   * @param {*} credenziali
   * @returns La risposta del backend TODO
   *
   */
  async postLogin(credenziali) {

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(credenziali)
    };

    return await fetch('/api/login/', requestOptions);
  }

  /**
   * Richiede al backend la modifca della password di un utente.
   * @param {*} credenziali (id, vecchia e nuova password)
   * @returns La risposta del backend
   *
   */
  async postPassword(credenziali) {

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(credenziali)
    };

    const response = await fetch('/api/password/', requestOptions);

    return response;
  }

  /**
   * Richiede al backend la registrazione di un nuovo utente.
   * @param {*} datiAnagrafici (nome, cognome, dataNascita, codiceFiscale, systemActor, username, password, attore)
   * @returns La risposta del backend
   *
   */
  async postRegistration(datiAnagrafici) {

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(datiAnagrafici)
    };

    const response = await fetch('/api/registrazione/', requestOptions);

    return response;
  }

}
