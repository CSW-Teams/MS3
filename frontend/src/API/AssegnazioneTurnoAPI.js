import { TurnoAPI } from "./TurnoAPI";
import {blue, red} from "@material-ui/core/colors";

export  class AssegnazioneTurnoAPI {

  /**
   * Parses content of query response body to extract a list of shifts
   */
  parseAllocatedShifts(body){
    let turni = [];

    for (let i = 0; i < body.length; i++) {
        let turno = new Object();
        turno.id = body[i].id;
        turno.startDate = body[i].inizio;
        turno.endDate =body[i].fine;

        let utenti_guardia = [];
        let utenti_reperibili = [];


      for (let j = 0; j < body[i].utentiDiGuardia.length; j++) {
           utenti_guardia[j] = body[i].utentiDiGuardia[j].id;
        }

      for (let j = 0; j < body[i].utentiReperibili.length; j++) {
          utenti_reperibili[j] = body[i].utentiReperibili[j].id;
      }

      turno.title= "Turno in "+  body[i].servizio.nome;

      turno.utenti_guardia = utenti_guardia;
      turno.utenti_reperibili = utenti_reperibili;

      turno.tipologia = body[i].tipologiaTurno;

      turno.servizio = body[i].servizio.nome;

      turni[i] = turno;

    }

    return turni;
}

  async getTurnByIdUser(id) {
    const response = await fetch('/api/assegnazioneturni/utente_id=' + id);
    const body = await response.json();


    let turni = this.parseAllocatedShifts(body);

    for (let i = 0; i < turni.length; i++) {
      for (let j = 0; j < body[i].utentiDiGuardia.length; j++) {
        if (id == turni[i].utenti_guardia[j]) {
          turni[i].turno ="GUARDIA" ;
        }
      }
      for (let j = 0; j < body[i].utentiReperibili.length; j++) {
        if (id == turni[i].utenti_reperibili[j]) {
          turni[i].turno = "REPERIBILITA'";
        }
      }
    }

    return turni;
  }

    async postAssegnazioneTurno(data,turnoTipologia,utentiSelezionatiGuardia,utentiReperibilita,servizioNome) {

      let assegnazioneTurno = new Object();

      assegnazioneTurno.giorno = data.$d.getDate();
      assegnazioneTurno.mese = data.$d.getMonth()+1;
      assegnazioneTurno.anno = data.$d.getFullYear();

      assegnazioneTurno.data= data.$d.getTime();
      assegnazioneTurno.servizio = servizioNome
      assegnazioneTurno.tipologiaTurno = turnoTipologia
      assegnazioneTurno.utentiDiGuardia = utentiSelezionatiGuardia;
      assegnazioneTurno.utentiReperibili = utentiReperibilita;

      console.log(assegnazioneTurno)
      console.log(data.$d.getDay())
      console.log(data.$D)


      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assegnazioneTurno)
      };

      const response = await fetch('/api/assegnazioneturni/',requestOptions);
      if(response.status != 202)
        return null
        
      return assegnazioneTurno;

  }


    async getGlobalTurn() {
        const response = await fetch('/api/assegnazioneturni/');
        const body = await response.json();

        return this.parseAllocatedShifts(body);
    }


  }
