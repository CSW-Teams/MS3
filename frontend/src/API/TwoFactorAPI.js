import {fetchWithAuth} from "../utils/fetchWithAuth";

// 2FA endpoints backing enrollment, challenge confirmation, and recovery-driven disable flows.
export class TwoFactorAPI {
  // Reads enforcement flags so UI can branch between informational and mandatory enrollment states.
  async getStatus() {
    return await fetchWithAuth('/api/2fa/status');
  }

  // Starts a fresh enrollment session; backend returns QR/manual secret plus one-time recovery codes.
  async startEnrollment() {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({})
    };
    return await fetchWithAuth('/api/2fa/enroll', requestOptions);
  }

  // Confirms secret binding with a current authenticator code; success enables 2FA for future logins.
  async confirmEnrollment(code) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({code})
    };
    return await fetchWithAuth('/api/2fa/confirm', requestOptions);
  }

  // `recoveryCode` flag tells backend how to validate `code` and whether recovery semantics apply.
  async disableTwoFactor(code, isRecoveryCode = false) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({code, recoveryCode: isRecoveryCode})
    };
    return await fetchWithAuth('/api/2fa/disable', requestOptions);
  }
}
