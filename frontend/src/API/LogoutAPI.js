export class LogoutAPI {

  /**

Richiede al backend il logout dell'utente.
Il JWT viene inviato tramite header Authorization.*
@param {string} jwtToken
@returns La risposta del backend*/
async postLogout(jwtToken) {

    const requestOptions = {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwtToken}`
      }
    };

    return await fetch('/api/logout/', requestOptions);
  }
}
