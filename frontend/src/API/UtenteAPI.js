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
      utente.color = teal;
      utenti[i]=utente;
    }

    return utenti;

  }

  async getAllUserOnlyNameSurname() {
    const response = await fetch('/api/utenti/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = new Object();
      utente.label = body[i].nome+" "+body[i].cognome;
      utente.id = body[i].id;
      utente.nome = body[i].nome;
      utente.cognome = body[i].cognome;
      utente.dataNascita = body[i].dataNascita;
      utente.codiceFiscale = body[i].codiceFiscale;
      utente.ruoloEnum = body[i].ruoloEnum;
      utente.email = body[i].email;

      utenti[i]=utente;
    }

    return utenti;

  }

}
