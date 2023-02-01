import { TurnoAPI } from "./TurnoAPI";
import {blue, red, teal} from "@material-ui/core/colors";
import { AssignedShift, SchedulableType } from "./Schedulable";

export  class AssegnazioneTurnoAPI {

  /**
   * Parses content of query response body to extract a list of shifts
   */
  parseAllocatedShifts(body){
    let turni = [];

    for (let i = 0; i < body.length; i++) {
        let turno = new AssignedShift(
          body[i].mansione + " in " + body[i].servizio.nome,
          body[i].inizio,
          body[i].fine,
          teal);
        turno.id = body[i].id;
        turno.type ="Assigned"

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
      turno.mansione = body[i].mansione;

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

  /**
   * Richiede al backend di registrare un'assegnazione turno.
   * @param {*} data 
   * @param {*} turnoTipologia 
   * @param {*} utentiSelezionatiGuardia 
   * @param {*} utentiReperibilita 
   * @param {*} servizioNome 
   * @param {*} forced 
   * @returns La risposta del backend:
   * 202 se è andato tutto ok, dunque la risposta contiene l'oggetto assegnazione generato;
   * 400 se i parametri della richiesta sono malformati e il backend non è riuscito a interpretarli;
   * 406 se la richiesta di assegnazone è stata rigettata, ad esempio perché violerebbe dei vincoli per la sua pianificazione.
   */  
  async postAssegnazioneTurno(data,turnoTipologia,utentiSelezionatiGuardia,utentiReperibilita,servizioNome,forced) {

      let assegnazioneTurno = new Object();

      assegnazioneTurno.giorno = data.$d.getDate();
      assegnazioneTurno.mese = data.$d.getMonth()+1;
      assegnazioneTurno.anno = data.$d.getFullYear();

      assegnazioneTurno.forced = forced;

      assegnazioneTurno.servizio = servizioNome;
      assegnazioneTurno.tipologiaTurno = turnoTipologia
      assegnazioneTurno.utentiDiGuardia = utentiSelezionatiGuardia;
      assegnazioneTurno.utentiReperibili = utentiReperibilita;
     
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assegnazioneTurno)
      };

      const response = await fetch('/api/assegnazioneturni/',requestOptions); 
       
      return response;

  }


  async aggiornaAssegnazioneTurno(appointmentChanged,changes,idLoggato) {

    let assegnazioneModificata = new Object();
    assegnazioneModificata.idAssegnazione = appointmentChanged.id;
    assegnazioneModificata.utenti_guardia = changes.utenti_guardia
    assegnazioneModificata.utenti_reperibili = changes.utenti_reperibili
    assegnazioneModificata.utenteModificatoreId = idLoggato;
    
    const requestOptions = {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(assegnazioneModificata)
    };

    console.log(assegnazioneModificata)

    const response = await fetch('/api/assegnazioneturni/',requestOptions); 
    return response;

}

async richiediRinunciaTurno(utenteCambio,assegnazione,idLoggato) {


  for(let i =0; i<assegnazione.utenti_guardia.length; i++){
    if(assegnazione.utenti_guardia[i] == idLoggato)
      assegnazione.utenti_guardia[i] = utenteCambio.id; 
  }

  for(let i =0; i<assegnazione.utenti_reperibili.length; i++){
    if(assegnazione.utenti_reperibili[i] == idLoggato)
    assegnazione.utenti_reperibili[i] = utenteCambio.id; 
  }

  let assegnazioneModificata = new Object();
  assegnazioneModificata.idAssegnazione = assegnazione.id;
  assegnazioneModificata.utenti_guardia = assegnazione.utenti_guardia
  assegnazioneModificata.utenti_reperibili = assegnazione.utenti_reperibili
  assegnazioneModificata.utenteModificatoreId = idLoggato;
  
  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(assegnazioneModificata)
  };

  const response = await fetch('/api/assegnazioneturni/',requestOptions); 
  return response;

}



async eliminaAssegnazioneTurno(idDaEliminare) {
 
  const requestOptions = {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  };

  const response = await fetch('/api/assegnazioneturni/'+idDaEliminare,requestOptions); 
  return response;

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
