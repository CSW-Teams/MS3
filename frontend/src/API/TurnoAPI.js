
export  class TurnoAPI {
  constructor() {
  }

  /**
   * API to retrieve shift data : queries <b>/api/shift/service={servizio}</b>
   * @param servizio The name of the service
   * @returns {Promise<*[]>} A Promise containing the shift's details
   */
  async getTurniByServizio(servizio) {
    const response = await fetch('/api/shifts/service='+servizio);
    const body = await response.json();

    const turni = [];

    for (let i = 0; i < body.length; i++) {
      let turno = {}
      turno.tipologia = body[i].timeslot
      turno.daysOfWeek = body[i].daysOfWeek
      turno.mansione = body[i].medicalServices
      turni[i] = turno
    }

    return turni;

  }

  /**
   * API to retrieve shift data only of a determined timeslot : queries <b>/api/shift/service={servizio}</b>
   * @param servizio The name of the service
   * @param tipologia The timeslot to select
   * @returns {Promise<*[]>} A Promise containing the shift's details
   */
  async getTurnoByServizioTipologia(servizio,tipologia) {
    const response = await fetch('/api/shifts/service='+servizio);
    const body = await response.json();

    for (let i = 0; i < body.length; i++) {
        if( body[i].timeslot === tipologia)
          return body[i];
    }

    return {};

  }


}
