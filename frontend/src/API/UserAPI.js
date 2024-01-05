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
    user.name = body.name;
    user.lastname = body.lastname;
    user.email = body.email;
    user.birthday = body.birthday;
    user.role = body.role;
    return user;
  }

  async getAllUsersInfo() {
    console.log("Mi rompo qui?")
    const response = await fetch('/api/users/');
    const body = await response.json();

    const utenti = [];

    for (let i = 0; i < body.length; i++) {
      const utente = {};
      utente.id = body[i].id;
      utente.name = body[i].name;
      utente.lastname = body[i].lastname;
      utente.birthday = body[i].birthday;
      utente.systemActors =body[i].systemActors;

      utenti[i]=utente;
    }

    return utenti;

  }

}
