import {ShiftChangeRequest} from "./ShiftChangeRequest";

export class ShiftChangeRequestAPI{

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

      requests[i] = new ShiftChangeRequest(
        requestId, turnDescription, inizioDate, fineDate, userDetails, status);
    }

    return requests;
  }

  async getTurnChangeRequestsByIdUser(id) {
    const response = await fetch('/api/change-shift-request/by/user_id=' + id);
    const body = await response.json();

    return this.parseRequests(body);
  }

  async getTurnChangeRequestsToIdUser(id) {
    const response = await fetch('/api/change-shift-request/to/user_id=' + id);
    const body = await response.json();

    return this.parseRequests(body);
  }

   async answerRequest(requestId, answer) {

    let dto = {};
    dto.hasAccepted = answer;
    dto.requestID = requestId;

    const requestOptions = {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    };

    return fetch('/api/change-shift-request/answer', requestOptions);
  }

}
