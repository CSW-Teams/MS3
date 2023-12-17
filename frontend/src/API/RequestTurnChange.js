export class RequestTurnChange{
  constructor(requestId, turnDescription, inizioDate, fineDate, userDetails, status){
    this.requestId = requestId;
    this.turnDescription = turnDescription;
    this.inizioDate = inizioDate;
    this.fineDate = fineDate;
    this.userDetails = userDetails;
    this.status = status;
  }
}
