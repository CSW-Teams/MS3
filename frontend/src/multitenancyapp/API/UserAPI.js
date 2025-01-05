import {teal} from '@mui/material/colors';
import {fetchWithAuth} from "../../utils/fetchWithAuth";

export  class UserAPI {
  constructor() {
  }

  async getAllUsersInfo() {
    const response = await fetchWithAuth('/api/tenant/users/');
    const body = await response.json();

    const userList = [];

    for (let i = 0; i < body.length; i++) {
      const user = {};
      user.id = body[i].id;
      user.name = body[i].name;
      user.lastname = body[i].lastname;
      user.birthday = body[i].birthday;
      user.color = teal;
      user.email = body[i].email;

      userList[i]=user;
    }
    return userList;
  }

}
