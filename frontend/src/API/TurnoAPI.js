
export  class TurnoAPI {
  constructor() {
  }

  async getTurniByServizio(servizio) {
    const response = await fetch('/api/shifts/service='+servizio);
    const body = await response.json();

    const turni = [];

    for (let i = 0; i < body.length; i++) {
      let turno = {}
      turno.tipologia = body[i].timeslot
      turno.mansione = body[i].medicalServices
      turni[i] = turno
    }

    return turni;

  }

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
