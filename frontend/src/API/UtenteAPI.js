import {teal,blue} from '@mui/material/colors';

export  class UtenteAPI {
  constructor() {
  }

  async getAllUser() {
    const response = await fetch('/api/utenti/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = new Object();
      utente.text = body[i].nome+" "+body[i].cognome+"  - "+ body[i].ruoloEnum;
      utente.id =body[i].id;
          if (body[i].ruoloEnum==="SPECIALIZZANDO") {
           utente.color = teal;
         } else {
         utente.color = blue;
       }
      utenti[i]=utente;
    }

    return utenti;

  }

}
