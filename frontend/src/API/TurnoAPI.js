export  class TurnoAPI {
    constructor() {
    }
   
    async getTurnByIdUser(id) {
        const response = await fetch('/api/turni/utente_id='+id);
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
        const response = await fetch('/api/turni/');
        const body = await response.json();

        let turni = [];

        for (let i = 0; i < body.length; i++) {
            let turno = new Object();
            turno.id = body[i].id;
            turno.startDate = body[i].inizio;
            turno.endDate =body[i].fine;

            let members = [];

            for (let j = 0; j < body[i].utenti.length; j++) {
                members[j] = body[i].utenti[j].id;
            }

            turno.members = members;
            turni[i] = turno;

        } 
        
        return turni;
        
    }
  }
  