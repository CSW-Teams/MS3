export class SingleUserProfileAPI {
  async deleteSpecialization(doctorID, specialization){
    let jsonString = "{" + "\"doctorID\":" + doctorID + ",\"specialization\":\"" + specialization + "\"}";

    const requestOptions = {
      method: 'DELETE',
      headers: {'Content-Type': 'application/json'},
      body: jsonString
    };

    const response = await fetch('/api/doctors/user-profile/delete-specialization',requestOptions);
    return response.status;
  }


  async addSpecialization(doctorID, specialization){
    let jsonString = "{" + "\"userID\":" + doctorID + ",\"specialization\":\"" + specialization + "\"}";

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: jsonString
    };

    const response = await fetch('/api/users/user-profile/add-specialization',requestOptions);
    return response.status;
  }


  async deleteSystemActor(doctorID, systemActor) {
    let jsonString = "{" + "\"userID\":" + doctorID + ",\"systemActor\":\"" + systemActor + "\"}";

    const requestOptions = {
      method: 'DELETE',
      headers: {'Content-Type': 'application/json'},
      body: jsonString
    };

    const response = await fetch('/api/users/user-profile/delete-system-actor',requestOptions);
    return response.status;
  }
}
