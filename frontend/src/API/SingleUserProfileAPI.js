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


  async addSpecializations(doctorID, specializations){
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({doctorID,specializations})
    };

    const response = await fetch('/api/doctors/user-profile/add-specialization',requestOptions);
    return response.status;
  }

  async getSpecializations(){
    const response = await fetch('/api/specializations');
    const body = await response.json();

    let specializationList = [];

    for (let i = 0; i < body.specializations.length; i++) {
        specializationList[i] = body.specializations[i];
    }

    return specializationList;
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

    async addSystemActors(userID, systemActors) {
      const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({userID,systemActors})
      };

      const response = await fetch('/api/users/user-profile/add-system-actors',requestOptions);
      return response.status;
    }

  async getSystemActors() {
    const response = await fetch('/api/system-actors');
    const body = await response.json();

    let systemActorList = [];

    for (let i = 0; i < body.systemActors.length; i++) {
      systemActorList[i] = body.systemActors[i];
    }

    return systemActorList;
  }
}
