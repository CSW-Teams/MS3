import {RequestTurnChange} from "./RequestTurnChange";

export class RequestTurnChangeAPI{

  parseRequests(body){
    let requests = [];

    for (let i = 0; i < body.length; i++) {
      const requestId  = body[i].requestId;
      const turnDescription = body[i].turnDescription;

      const inizioEpochMilliseconds = body[i].inizioEpoch*1000;
      const inizioDate = new Date(inizioEpochMilliseconds);
      const fineEpochMilliseconds = body[i].fineEpoch*1000;
      const fineDate = new Date(fineEpochMilliseconds);

      const userDetails = body[i].userDetails;
      const status = body[i].status;

      requests[i] = new RequestTurnChange(
        requestId, turnDescription, inizioDate, fineDate, userDetails, status);
    }

    return requests;
  }

  async getTurnChangeRequestsByIdUser(id) {
    const response = await fetch('/api/assegnazioneturni/scambio/by/utente_id=' + id);
    const body = await response.json();

    return this.parseRequests(body);
  }

  async getTurnChangeRequestsToIdUser(id) {
    const response = await fetch('/api/assegnazioneturni/scambio/to/utente_id=' + id);
    const body = await response.json();

    return this.parseRequests(body);
  }
}
