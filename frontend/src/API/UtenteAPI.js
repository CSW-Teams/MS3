import {teal, red} from '@mui/material/colors';

export  class UtenteAPI {
  constructor() {
  }

  async getAllUser() {
    const response = await fetch('/api/users/');
    const body = await response.json();

    const userList = [];

    for (let i = 0; i < body.length; i++) {
      const user = {};
      //TODO remove
      console.log("fadsfds " + body[i].name);
      user.text = body[i].name+" "+body[i].lastname+"  - "+ body[i].systemActor;
      user.id =body[i].id;
      user.color = teal;
      userList[i]=user;
    }

    return userList;

  }

  async getUserDetails(id){
    const response = await fetch('/api/users/user_id=' + id);
    const body = await response.json();

    const utente = {};

    utente.label = body.nome+" "+body.cognome;
    utente.id = body.id;
    utente.nome = body.nome;
    utente.cognome = body.cognome;
    utente.dataNascita = body.dataNascita;
    utente.codiceFiscale = body.codiceFiscale;
    utente.systemActor = body.systemActor;
    utente.email = body.email;
    utente.attore =body.attore;
    return utente;

  }

  async getAllUsersInfo() {
    const response = await fetch('/api/users/');
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
      utente.systemActor = body[i].systemActor;
      utente.email = body[i].email;
      utente.categorie = body[i].categorie;
      utente.attore =body[i].attore;

      utenti[i]=utente;
    }

    return utenti;

  }

}
