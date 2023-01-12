import {teal, red} from '@mui/material/colors';

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

    //Dummy user to change holiday color
    const dummy = new Object;
    dummy.text ="Ih-Oh";
    dummy.id = -1;
    dummy.color = red;
    utenti[body.length] = dummy

    return utenti;

  }

  async getUserDetails(id){
    const response = await fetch('/api/utenti/utente_id=' + id);
    const body = await response.json();

    const utente = new Object();

    utente.label = body.nome+" "+body.cognome;
    utente.id = body.id;
    utente.nome = body.nome;
    utente.cognome = body.cognome;
    utente.dataNascita = body.dataNascita;
    utente.codiceFiscale = body.codiceFiscale;
    utente.ruoloEnum = body.ruoloEnum;
    utente.email = body.email;
    return utente;

  }

  async getAllUsersInfo() {
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
      utente.categorie = body[i].categorie;

      utenti[i]=utente;
    }

    return utenti;

  }

}
