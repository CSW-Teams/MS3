import {teal, red} from '@mui/material/colors';

export  class UtenteAPI {
  constructor() {
  }

  async getAllUser() {
    const response = await fetch('/api/utenti/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = {};
      utente.text = body[i].nome+" "+body[i].cognome+"  - "+ body[i].ruoloEnum;
      utente.id =body[i].id;
      utente.color = teal;
      utenti[i]=utente;
    }

    return utenti;

  }

  async getUserDetails(id){
    const response = await fetch('/api/utenti/utente_id=' + id);
    const body = await response.json();

    const utente = {};

    utente.label = body.nome+" "+body.cognome;
    utente.id = body.id;
    utente.nome = body.nome;
    utente.cognome = body.cognome;
    utente.dataNascita = body.dataNascita;
    utente.codiceFiscale = body.codiceFiscale;
    utente.ruoloEnum = body.ruoloEnum;
    utente.email = body.email;
    utente.attore =body.attore;
    return utente;

  }

  async getAllUsersInfo() {
    const response = await fetch('/api/utenti/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = {};
      utente.label = body[i].nome+" "+body[i].cognome+" "+body[i].ruoloEnum.substring(0, 3);
      utente.id = body[i].id;
      utente.nome = body[i].nome;
      utente.cognome = body[i].cognome;
      utente.dataNascita = body[i].dataNascita;
      utente.codiceFiscale = body[i].codiceFiscale;
      utente.ruoloEnum = body[i].ruoloEnum;
      utente.email = body[i].email;
      utente.categorie = body[i].categorie;
      utente.attore =body[i].attore;

      utenti[i]=utente;
    }

    return utenti;

  }

}
