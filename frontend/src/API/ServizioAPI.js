export  class ServizioAPI {
    constructor() {
    }

    async getService() {
        const response = await fetch('/api/servizi/');
        const body = await response.json();

        const servizi = [];

        for (let i = 0; i < body.length; i++) {
            servizi[i] = body[i].nome;
        }

        return servizi;

    }



  }
