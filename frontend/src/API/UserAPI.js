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
    const response = await fetch('/api/users/');
    const body = await response.json();

    const userList = [];

    for (let i = 0; i < body.length; i++) {
      const user = {};
      user.id = body[i].id;
      user.name = body[i].name;
      user.lastname = body[i].lastname;
      user.birthday = body[i].birthday;
      user.color = teal;
      user.systemActors = body[i].systemActors;

      userList[i]=user;
    }
    return userList;
  }


  async getAllDoctorsInfo() {
    const response = await fetch('/api/doctors/');
    const body = await response.json();

    const dctorList = [];

    for (let i = 0; i < body.length; i++) {
      const doctor = {};
      doctor.id = body[i].id;
      doctor.name = body[i].name;
      doctor.lastname = body[i].lastname;
      doctor.birthday = body[i].birthday;
      doctor.color = teal;
      doctor.systemActors = body[i].systemActors;
      doctor.seniority = body[i].seniority;
      doctor.text = body[i].lastname+" "+body[i].lastname+"  - "+ ((body[i].seniority === "STRUCTURED") ? "Strutturato" : ((body[i].seniority === "SPECIALIST_JUNIOR") ? "Specializzando I/II anno" : "Specializzando III/IV/V anno" ) ); // Needed to show in change shift view in SignleScheduleView

      dctorList[i]=doctor;
    }

    return dctorList;

  }

}
