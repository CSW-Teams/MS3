export  class VincoloAPI {
    constructor() {
    }
   
    async getVincoli() {
        const response = await fetch('/api/vincoli/');
        const body = await response.json();

        const vincoli = [];
        for (let i = 0; i < body.length; i++) {
            vincoli[i] = body[i];
        } 
        
        return vincoli;
        
    }


    
  }
  