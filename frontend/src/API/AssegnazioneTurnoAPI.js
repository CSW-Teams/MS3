import {teal} from "@material-ui/core/colors";
import {AssignedShift} from "./Schedulable";
import {Utente} from "./Utente";

export  class AssegnazioneTurnoAPI {

   /**
   * Parses content of query response body to extract a list of shifts
   */
  parseAllocatedShifts(body){
    let turni = [];

    for (let i = 0; i < body.length; i++) {

        const inizioEpochMilliseconds = body[i].inizioEpoch*1000
        const inizioDate = new Date(inizioEpochMilliseconds);

        const fineEpochMilliseconds = body[i].fineEpoch*1000
        const fineDate = new Date(fineEpochMilliseconds);

        let turno = new AssignedShift(
          body[i].mansione + " in " + body[i].servizio.nome,
          inizioDate,
          fineDate,
          teal);
        turno.id = body[i].id;
        turno.type ="Assigned"

        let utenti_guardia = [];
        let utenti_reperibili = [];
        let utenti_guardia_id = [];
        let utenti_reperibili_id = []
        let utenti_rimossi = [];
        let utenti_rimossi_id = [];


        for (let j = 0; j < body[i].utentiDiGuardia.length; j++) {
          let currentUserDto = body[i].utentiDiGuardia[j];
          let utenteAllocato = new Utente(
            currentUserDto.id,
            currentUserDto.nome,
            currentUserDto.cognome,
            currentUserDto.ruoloEnum,
          )
          utenti_guardia[j] = utenteAllocato;
          utenti_guardia_id[j] = utenteAllocato.id;
        }

        for (let j = 0; j < body[i].utentiReperibili.length; j++) {
          let currentUserDto = body[i].utentiReperibili[j];
          let utenteReperibile = new Utente(
            currentUserDto.id,
            currentUserDto.nome,
            currentUserDto.cognome,
            currentUserDto.ruoloEnum,
          )
          utenti_reperibili[j] = utenteReperibile;
          utenti_reperibili_id[j] = utenteReperibile.id;
        }

        for (let j = 0; j < body[i].retiredUsers.length; j++) {
          let currentUserDto = body[i].retiredUsers[j];
          let utenteRimosso = new Utente(
            currentUserDto.id,
            currentUserDto.nome,
            currentUserDto.cognome,
            currentUserDto.ruoloEnum,
          )
          utenti_rimossi[j] = utenteRimosso;
          utenti_rimossi_id[j] = utenteRimosso.id;
        }


        turno.utenti_guardia = utenti_guardia;
        turno.utenti_reperibili = utenti_reperibili;
        turno.utenti_guardia_id = utenti_guardia_id;
        turno.utenti_reperibili_id = utenti_reperibili_id;
        turno.utenti_rimossi = utenti_rimossi;
        turno.utenti_rimossi_id = utenti_rimossi_id;

      turno.tipologia = body[i].tipologiaTurno;
      turno.servizio = body[i].servizio.nome;
      turno.mansione = body[i].mansione;
      console.log(turno.mansione)
      turno.reperibilitaAttiva = body[i].reperibilitaAttiva;

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
        if (id === turni[i].utenti_guardia[j]) {
          turni[i].turno ="GUARDIA" ;
        }
      }
      for (let j = 0; j < body[i].utentiReperibili.length; j++) {
        if (id === turni[i].utenti_reperibili[j]) {
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
   * @param mansione
   * @param {*} forced
   * @returns La risposta del backend:
   * 202 se è andato tutto ok, dunque la risposta contiene l'oggetto assegnazione generato;
   * 400 se i parametri della richiesta sono malformati e il backend non è riuscito a interpretarli;
   * 406 se la richiesta di assegnazone è stata rigettata, ad esempio perché violerebbe dei vincoli per la sua pianificazione.
   */
  async postAssegnazioneTurno(data,turnoTipologia,utentiSelezionatiGuardia,utentiReperibilita,servizioNome,mansione,forced) {

      let assegnazioneTurno = {};

      const giorno = data.$d.getDate();
      const mese = data.$d.getMonth() + 1; // January is 0, so we add 1 to get 1-12 range
      const anno = data.$d.getFullYear();

      // Creating an ISO 8601 formatted date string
      assegnazioneTurno.giorno = `${anno}-${mese.toString().padStart(2, '0')}-${giorno.toString().padStart(2, '0')}`;

      assegnazioneTurno.forced = forced;

      assegnazioneTurno.servizio = servizioNome;
      assegnazioneTurno.mansione = mansione;
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

    let assegnazioneModificata = {};
    assegnazioneModificata.idAssegnazione = appointmentChanged.id;
    assegnazioneModificata.utenti_guardia = changes.utenti_guardia_id
    assegnazioneModificata.utenti_reperibili = changes.utenti_reperibili_id
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
  let assegnazioneConModifiche = {}
  assegnazioneConModifiche.idAssegnazione = assegnazione.id;
  assegnazioneConModifiche.utenti_guardia = []
  assegnazioneConModifiche.utenti_reperibili = []
  assegnazioneConModifiche.utenteModificatoreId = idLoggato;

  for(let i =0; i<assegnazione.utenti_guardia.length; i++){
    assegnazioneConModifiche.utenti_guardia[i] = assegnazione.utenti_guardia_id[i]
    if(assegnazione.utenti_guardia_id[i] === idLoggato)
      assegnazioneConModifiche.utenti_guardia[i] = utenteCambio.id;
  }

  for(let i =0; i<assegnazione.utenti_reperibili.length; i++){
    assegnazioneConModifiche.utenti_reperibili[i] = assegnazione.utenti_reperibili_id[i]
    if(assegnazione.utenti_reperibili_id[i] === idLoggato)
      assegnazioneConModifiche.utenti_reperibili[i] = utenteCambio.id;
  }

  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(assegnazioneConModifiche)
  };

  const response = await fetch('/api/assegnazioneturni/',requestOptions);
  return response;

}



async eliminaAssegnazioneTurno(idDaEliminare) {

  const requestOptions = {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  };

  console.log(idDaEliminare)
  const response = await fetch('/api/assegnazioneturni/'+idDaEliminare,requestOptions);
  return response;

}



    async getGlobalTurn() {
        const response = await fetch('/api/assegnazioneturni/');
        const body = await response.json();

        return this.parseAllocatedShifts(body);
    }

    async postGenerationSchedule(dataStart,dataEnd) {

      let requestGeneration = {};

      const giornoInizio = dataStart.$d.getDate();
      const meseInizio = dataStart.$d.getMonth()+1; // January is 0, so we add 1 to get 1-12 range
      const annoInizio = dataStart.$d.getFullYear();

      // Creating an ISO 8601 formatted date string
      requestGeneration.giornoInizio = `${annoInizio}-${meseInizio.toString().padStart(2, '0')}-${giornoInizio.toString().padStart(2, '0')}`;

      const giornoFine = dataEnd.$d.getDate();
      const meseFine = dataEnd.$d.getMonth()+1; // January is 0, so we add 1 to get 1-12 range
      const annoFine = dataEnd.$d.getFullYear();

      // Creating an ISO 8601 formatted date string
      requestGeneration.giornoFine = `${annoFine}-${meseFine.toString().padStart(2, '0')}-${giornoFine.toString().padStart(2, '0')}`;

      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestGeneration)
      };

      const response = await fetch('/api/schedule/generation',requestOptions);
      return response.status;

  }

  }
