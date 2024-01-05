import {teal} from "@material-ui/core/colors";
import {AssignedShift} from "./Schedulable";
import {User} from "./User";

export  class AssegnazioneTurnoAPI {

   /**
   * Parses content of query response body to extract a list of shifts
   */
  parseAllocatedShifts(body){
    let turni = [];

    for (let i = 0; i < body.length; i++) {
        const inizioEpochMilliseconds = body[i].startDateTime*1000
        const inizioDate = new Date(inizioEpochMilliseconds);

        const fineEpochMilliseconds = body[i].endDateTime*1000
        const fineDate = new Date(fineEpochMilliseconds);

        let turno = new AssignedShift(
          body[i].medicalServiceTask + " in " + body[i].medicalServiceLabel,
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


        for (let j = 0; j < body[i].doctorsOnDuty.length; j++) {
          let currentUserDto = body[i].doctorsOnDuty[j];
          let utenteAllocato = new User(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            currentUserDto.systemActor,
          )
          utenti_guardia[j] = utenteAllocato;
          utenti_guardia_id[j] = utenteAllocato.id;
        }

        for (let j = 0; j < body[i].doctorsOnCall.length; j++) {
          let currentUserDto = body[i].doctorsOnCall[j];
          let utenteReperibile = new User(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            currentUserDto.systemActor,
          )
          utenti_reperibili[j] = utenteReperibile;
          utenti_reperibili_id[j] = utenteReperibile.id;
        }

        for (let j = 0; j < body[i].deletedDoctors.length; j++) {
          let currentUserDto = body[i].deletedDoctors[j];
          let utenteRimosso = new User(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            currentUserDto.systemActor,
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

      turno.tipologia = body[i].timeSlot;
      turno.servizio = body[i].medicalServiceLabel;
      turno.mansione = body[i].medicalServiceTask;
      turno.reperibilitaAttiva = body[i].reperibilitaAttiva;

      turni[i] = turno;
    }
    return turni;
}

  async getTurnByIdUser(id) {
    const response = await fetch('/api/concrete-shifts/user_id=' + id);
    const body = await response.json();
    let turni = this.parseAllocatedShifts(body);

    for (let i = 0; i < turni.length; i++) {

      for (let j = 0; j < body[i].doctorsOnDuty.length; j++) {
        if (id === turni[i].utenti_guardia[j]) {
          turni[i].turno ="GUARDIA" ;
        }
      }
      for (let j = 0; j < body[i].doctorsOnCall.length; j++) {
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

    return await fetch('/api/concrete-shifts/', requestOptions);

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

    return await fetch('/api/concrete-shifts/', requestOptions);

}

async requestShiftChange(utenteCambio, assegnazione, idLoggato) {
    console.log("Ci arrivo qui?")
  let shiftChangeRequest = {}
  shiftChangeRequest.concreteShiftId = assegnazione.id;
  shiftChangeRequest.senderId = idLoggato;
  shiftChangeRequest.receiverId = utenteCambio.value;

  console.log("concrete shift id : " + shiftChangeRequest.concreteShiftId);
  console.log("sender: " + shiftChangeRequest.senderId);
  console.log("receiver: " + shiftChangeRequest.receiverId);

  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(shiftChangeRequest)
  };

  return await fetch('/api/change-shift-request/', requestOptions);
}



async eliminaAssegnazioneTurno(idDaEliminare) {

  const requestOptions = {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  };

  const response = await fetch('/api/concrete_shifts/'+idDaEliminare,requestOptions);
  return response;

}



    async getGlobalShift() {
        const response = await fetch('/api/concrete-shifts/');
        const body = await response.json();

        const risultati = this.parseAllocatedShifts(body);

        return risultati;
    }

    async postGenerationSchedule(dataStart,dataEnd) {

      const initialDay = dataStart.$d.getDate();
      const initialMonth = dataStart.$d.getMonth()+1; // January is 0, so we add 1 to get 1-12 range
      const initialYear = dataStart.$d.getFullYear();
      const finalDay = dataEnd.$d.getDate();
      const finalMonth = dataEnd.$d.getMonth()+1; // January is 0, so we add 1 to get 1-12 range
      const finalYear = dataEnd.$d.getFullYear();

      let requestGeneration = {
        initialDay: initialDay,
        initialMonth: initialMonth,
        initialYear: initialYear,
        finalDay: finalDay,
        finalMonth: finalMonth,
        finalYear: finalYear
      };

      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestGeneration)
      };

      const response = await fetch('/api/schedule/generation',requestOptions);
      return response.status;

  }

  }
