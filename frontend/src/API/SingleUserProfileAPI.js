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

  async deletePermanentCondition(doctorID, conditionID,condition) {
    let jsonString = "{" + "\"doctorID\":" + doctorID + ",\"conditionID\":" + conditionID + ",\"condition\":\"" + condition + "\"}";

    const requestOptions = {
      method: 'DELETE',
      headers: {'Content-Type': 'application/json'},
      body: jsonString
    };

    const response = await fetch('/api/doctors/user-profile/delete-permanent-condition',requestOptions);
    return response.status;
  }

  async deleteTemporaryCondition(doctorID, conditionID, condition) {
    let jsonString = "{" + "\"doctorID\":" + doctorID + ",\"conditionID\":" + conditionID + ",\"condition\":\"" + condition + "\"}";

    const requestOptions = {
      method: 'DELETE',
      headers: {'Content-Type': 'application/json'},
      body: jsonString
    };

    const response = await fetch('/api/doctors/user-profile/delete-temporary-condition',requestOptions);
    return response.status;
  }

    /**
     * Method used to return all the conditions saved in the database to the frontend, ù
     * to show all conditions when we want to add one
     * @returns {Promise<void>}
     */
    async getAllConditionSaved() {
      const response = await fetch('/api/conditions');
      const body = await response.json();

      let conditionList = [];


      for (let i = 0; i < body.allSavedConditions.length; i++) {
            conditionList[i] = body.allSavedConditions[i];
        }

      return conditionList;
    }

  /**
   * Api to add a new condition to a doctor
   * @param doctorID
   * @param newCondition
   * @returns {Promise<number>}
   */
  async addCondition(doctorID, newCondition) {
    let condition = {};

    condition["condition"] = newCondition.label;
    condition["startDate"] = newCondition.startDate/1000;
    condition["endDate"] = newCondition.endDate/1000;

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({doctorID,condition})
    };

    const response = await fetch('/api/doctors/user-profile/add-condition',requestOptions);
    const json = await response.json();
    const id = json.conditionID;
    return id;
  }


}
