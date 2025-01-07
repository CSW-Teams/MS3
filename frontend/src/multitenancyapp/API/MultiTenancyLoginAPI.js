export  class MultiTenancyLoginAPI {

  /**
   * Richiede al backend l'autenticazione di un utente.
   * @param {*} credenziali
   * @returns La risposta del backend TODO
   *
   */
  async postLogin(credenziali) {

    const userDetails = {
      email: credenziali.email,
      password: credenziali.password,
    }

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(userDetails)
    };

    return await fetch('/api/multitenancy/login/', requestOptions);
  }

  async postTenantSelection(hospital, jwt) {
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${jwt}`
      },
      body: JSON.stringify({ tenant: hospital })
    };

    return await fetch('/api/multitenancy/tenant/select/', requestOptions);
  }

}
