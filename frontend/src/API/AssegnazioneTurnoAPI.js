export  class AssegnazioneTurnoAPI {
    constructor() {
    }

    async getTurnByIdUser(id) {
        const response = await fetch('/api/assegnazioneturni/utente_id='+id);
        const body = await response.json();

        const turni = [];

        for (let i = 0; i < body.length; i++) {
            const turno = new Object();
            turno.startDate = body[i].inizio;
            turno.endDate =body[i].fine;
            turno.id = body[i].id;
            turni[i]=turno;
        }

        return turni;

    }


    async getGlobalTurn() {
        const response = await fetch('/api/assegnazioneturni/');
        const body = await response.json();

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


          turno.members = utenti_guardia;
          turni[i] = turno;

        }

        return turni;

    }
  }
