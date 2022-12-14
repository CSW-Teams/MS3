
export  class TurnoAPI {
  constructor() {
  }

  async getTurniByServizio(servizio) {
    const response = await fetch('/api/turni/servizio='+servizio);
    const body = await response.json();

    const turni = [];

    for (let i = 0; i < body.length; i++) {
        turni[i] = body[i].tipologiaTurno
    }

    console.log(turni)
    return turni;

  }

  async getTurnoByServizioTipologia(servizio,tipologia) {
    const response = await fetch('/api/turni/servizio='+servizio);
    const body = await response.json();

    for (let i = 0; i < body.length; i++) {
        if( body[i].tipologiaTurno == tipologia)
          return body[i];
    }

    return {};  

  }


}
