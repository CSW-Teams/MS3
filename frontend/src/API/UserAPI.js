import {teal, red} from '@mui/material/colors';

export  class UserAPI {
  constructor() {
  }

  async getAllUser() {
    const response = await fetch('/api/users/');
    const body = await response.json();

    const userList = [];

    for (let i = 0; i < body.length; i++) {
      const user = {};
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

    const user = {};

    user.label = body.name+" "+body.lastname;
    user.id = body.id;
    user.name = body.name;
    user.lastname = body.lastname;
    user.dataNascita = body.dataNascita;
    user.codiceFiscale = body.codiceFiscale;
    user.systemActor = body.systemActor;
    user.email = body.email;
    user.attore =body.attore;
    return user;

  }

  async getAllUsersInfo() {
    const response = await fetch('/api/users/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = {};
      utente.label = body[i].name+" "+body[i].lastname+" "+body[i].systemActor.substring(0, 3);
      utente.id = body[i].id;
      utente.name = body[i].name;
      utente.lastname = body[i].lastname;
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
