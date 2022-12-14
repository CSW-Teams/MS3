import { TurnoAPI } from "./TurnoAPI";
import {blue, red} from "@material-ui/core/colors";
import { AssignedShift } from "./Schedulable";

export  class AssegnazioneTurnoAPI {

  /**
   * Parses content of query response body to extract a list of shifts
   */
  parseAllocatedShifts(body){
    let turni = [];

    for (let i = 0; i < body.length; i++) {
        let turno = new AssignedShift(
          "Turno in "+  body[i].servizio.nome,
          body[i].inizio,
          body[i].fine);
        turno.id = body[i].id;

        let utenti_guardia = [];
        let utenti_reperibili = [];


      for (let j = 0; j < body[i].utentiDiGuardia.length; j++) {
           utenti_guardia[j] = body[i].utentiDiGuardia[j].id;
        }

      for (let j = 0; j < body[i].utentiReperibili.length; j++) {
          utenti_reperibili[j] = body[i].utentiReperibili[j].id;
      }

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

    async postGenerationSchedule(dataStart,dataEnd) {

      let requestGeneration = new Object();

      requestGeneration.giornoInizio = dataStart.$d.getDate();
      requestGeneration.meseInizio = dataStart.$d.getMonth()+1;
      requestGeneration.annoInizio = dataStart.$d.getFullYear();

      requestGeneration.giornoFine= dataEnd.$d.getDate();
      requestGeneration.meseFine = dataEnd.$d.getMonth()+1;
      requestGeneration.annoFine = dataEnd.$d.getFullYear();
      
      console.log(requestGeneration)
      

      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestGeneration)
      };

      const response = await fetch('/api/schedule/generation',requestOptions);
      if(response.status != 202)
        return null
        
      return requestGeneration;

  }

  }
