// Small API wrapper used by navbar logout to invalidate session server-side before local cleanup.
export class LogoutAPI {

  /**
   * Richiede al backend il logout dell'utente.
   * Il JWT viene inviato tramite header Authorization.
   * @param {string} jwtToken
   * @returns La risposta del backend
   */
  async postLogout(jwtToken) {

    const requestOptions = {
      method: 'POST', headers: {
        'Authorization': `Bearer ${jwtToken}`
      }
    };

    // Logout errors are non-blocking in UI: frontend still clears local session data.
    return await fetch('/api/logout/', requestOptions);
  }
}
