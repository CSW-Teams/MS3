import {fetchWithAuth} from "../utils/fetchWithAuth";

export class TwoFactorAPI {
  async getStatus() {
    return await fetchWithAuth('/api/2fa/status');
  }

  async startEnrollment() {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({})
    };
    return await fetchWithAuth('/api/2fa/enroll', requestOptions);
  }

  async confirmEnrollment(code) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({code})
    };
    return await fetchWithAuth('/api/2fa/confirm', requestOptions);
  }

  async disableTwoFactor(code, isRecoveryCode = false) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({code, recoveryCode: isRecoveryCode})
    };
    return await fetchWithAuth('/api/2fa/disable', requestOptions);
  }
}
