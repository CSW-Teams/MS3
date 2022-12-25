import { TurnoAPI } from "./TurnoAPI";

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
        const response = await fetch('/api/assegnazioneturni/utente_id='+id);
        const body = await response.json();

        return this.parseAllocatedShifts(body);

    }
 
    async postAssegnazioneTurno(data,turnoTipologia,utentiSelezionatiGuardia,utentiReperibilita,servizioNome) {

      let turnoAPI = new TurnoAPI();
      let assegnazioneTurno = new Object();
      let turno = await turnoAPI.getTurnoByServizioTipologia(servizioNome,turnoTipologia);
      let mese =data.$M;
      let giorno = data.$D;

      // modifico il formato del giorno e del mese. Esempio: gennaio è identificato dal numero 0 e febbraio da 1. 
      // Devo convertire rispettivamente 0 -> 01 e 1 ->02 perchè questo è il formato accettato dal backend.
      // Stesso ragionamento per il giorno del mese.
      mese = mese+1;
      if(mese<10){
        mese = '0'+mese
      }
      giorno = giorno+1;
      if(giorno<10){
        giorno = '0'+giorno
      }

      console.log(turno)

      assegnazioneTurno.inizio= data.$y+'-'+mese+'-'+giorno+'T'+turno.oraInizio+'.000+0000';
      assegnazioneTurno.fine= data.$y+'-'+mese+'-'+giorno+'T'+turno.oraFine+'.000+0000';
      assegnazioneTurno.idTurno = turno.id
      assegnazioneTurno.servizio = turno.servizio
      assegnazioneTurno.tipologiaTurno = turnoTipologia
      assegnazioneTurno.utentiDiGuardia = utentiSelezionatiGuardia;
      assegnazioneTurno.utentiReperibili = utentiReperibilita;

      console.log(assegnazioneTurno)

      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assegnazioneTurno)
      };

      const response = await fetch('/api/assegnazioneturni/',requestOptions);
      const body = await response.json();
      
      // TODO gestione dell'errore
      return;

  }


    async getGlobalTurn() {
        const response = await fetch('/api/assegnazioneturni/');
        const body = await response.json();

        return this.parseAllocatedShifts(body);
    }


  }
