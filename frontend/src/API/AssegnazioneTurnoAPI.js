import {teal} from "@material-ui/core/colors";
import {AssignedShift} from "./Schedulable";
import {Doctor} from "../entity/Doctor";

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
          let utenteAllocato = new Doctor(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            currentUserDto.seniority,
            currentUserDto.task,
          )
          utenti_guardia[j] = utenteAllocato;
          utenti_guardia_id[j] = utenteAllocato.id;
        }

        for (let j = 0; j < body[i].doctorsOnCall.length; j++) {
          let currentUserDto = body[i].doctorsOnCall[j];
          let utenteReperibile = new Doctor(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            currentUserDto.seniority,
            currentUserDto.task,
          )
          utenti_reperibili[j] = utenteReperibile;
          utenti_reperibili_id[j] = utenteReperibile.id;
        }

        for (let j = 0; j < body[i].deletedDoctors.length; j++) {
          let currentUserDto = body[i].deletedDoctors[j];
          let seniority = currentUserDto.seniority === "STRUCTURED" ? "Strutturato" : "Specializzando";
          let utenteRimosso = new Doctor(
            currentUserDto.id,
            currentUserDto.name,
            currentUserDto.lastname,
            seniority,
            currentUserDto.task,
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

  async getAvailableUsersForShiftExchange(params){

    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params)
    };

    const response = await fetch('/api/concrete-shifts/available-users-for-replacement/', requestOptions);
    const body = await response.json();
    let availableUsers = [];

    for (let i = 0; i < body.length; i++) {
      console.log(body[i].task);
      let doctor = new Doctor(body[i].id, body[i].name, body[i].lastname, body[i].seniority,body[i].task);
      console.log(doctor.label);
      availableUsers.push(doctor);
    }

    return availableUsers;

  }

  async getShiftByIdUser(id) {
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
  async postAssegnazioneTurno(data,turnoTipologia,utentiSelezionatiGuardia,utentiReperibilita,servizio,mansione,forced) {

      let assegnazioneTurno = {};

      const day = data.$d.getDate();
      const month = data.$d.getMonth() + 1; // January is 0, so we add 1 to get 1-12 range
      const year = data.$d.getFullYear();

      // Creating an ISO 8601 formatted date string
      // assegnazioneTurno.giorno = `${anno}-${mese.toString().padStart(2, '0')}-${giorno.toString().padStart(2, '0')}`;

      assegnazioneTurno.day = day;
      assegnazioneTurno.month = month;
      assegnazioneTurno.year = year;

      assegnazioneTurno.forced = forced;

      assegnazioneTurno.servizio = servizio;
      // assegnazioneTurno.mansione = mansione;
      assegnazioneTurno.timeSlot = turnoTipologia;

      let onDutyDoctorsValues = [];
      let onCallDoctorsValues = [];

      for (let i = 0; i < utentiSelezionatiGuardia.length; i++) {
        utentiSelezionatiGuardia[i].value.systemActors = ["DOCTOR"]
        onDutyDoctorsValues.push(utentiSelezionatiGuardia[i].value);
      }

      for (let i = 0; i < utentiReperibilita.length; i++) {
        utentiReperibilita[i].value.systemActors = ["DOCTOR"]
        onCallDoctorsValues.push(utentiReperibilita[i].value);
      }

      assegnazioneTurno.onDutyDoctors = onDutyDoctorsValues;
      assegnazioneTurno.onCallDoctors = onCallDoctorsValues;

      console.log("Assegnazione turno: ", assegnazioneTurno);

      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assegnazioneTurno)
      };

    return await fetch('/api/concrete-shifts/', requestOptions);

  }


  async aggiornaAssegnazioneTurno(appointmentChanged,changes,idLoggato) {

    let assegnazioneModificata = {};
    assegnazioneModificata.concreteShiftId = appointmentChanged.id;
    assegnazioneModificata.onDutyDoctors = changes.utenti_guardia_id
    assegnazioneModificata.onCallDoctors = changes.utenti_reperibili_id
    assegnazioneModificata.modifyingDoctorId = idLoggato;

    console.log("FANFADEBUG :" + appointmentChanged.id)
    console.log("FANFADEBUG :" + changes.utenti_guardia_id)
    console.log("FANFADEBUG :" + changes.utenti_reperibili_id)
    console.log("FANFADEBUG :" + idLoggato)

    const requestOptions = {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(assegnazioneModificata)
    };

    return await fetch('/api/concrete-shifts/', requestOptions);

}

async requestShiftChange(utenteCambio, assegnazione, idLoggato) {
  let shiftChangeRequest = {}
  shiftChangeRequest.concreteShiftId = assegnazione.id;
  shiftChangeRequest.senderId = idLoggato;
  shiftChangeRequest.receiverId = utenteCambio.value;

  const requestOptions = {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(shiftChangeRequest)
  };
  let response = await fetch('/api/change-shift-request/', requestOptions);
  console.log(response)
  console.log(response.body)
  return response;
}



async eliminaAssegnazioneTurno(idDaEliminare) {

  const requestOptions = {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
  };

  const response = await fetch('/api/concrete-shifts/'+idDaEliminare,requestOptions);
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

      console.log(JSON.stringify(requestGeneration))
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestGeneration)
      };

      const response = await fetch('/api/schedule/generation',requestOptions);
      return response.status;

  }

  }
