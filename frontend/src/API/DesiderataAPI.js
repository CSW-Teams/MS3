import {fetchWithAuth} from "../utils/fetchWithAuth";

export  class DesiderateAPI {

  extractDesiderate(body) {

    const desiderate = [];

    for (let i = 0; i < body.length; i++) {
      let des = {}
      des.idDesiderata = body[i].preferenceId
      des.data= new Date(body[i].year, body[i].month-1, body[i].day)
      des.turnKinds = body[i].turnKinds ;
      desiderate[i]=des;
    }
    return desiderate;
  }

  /**
   * Saves the selected preferences
   * @param date An array of preferences
   * @param id The id of the doctor that wants to save such preferences
   * @returns {Promise<Response>} A Promise containing the new preferences with their own id
   */
  async salvaDesiderate(date,id) {

    let desiderate = []

    for (let i = 0; i < date.length; i++) {
      let desiderata = {}

      desiderata.year= date[i].year
      desiderata.month= date[i].month.number
      desiderata.day= date[i].day
      desiderata.turnKinds = date[i].turnKinds
      desiderate.push(desiderata)
    }

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(desiderate)
    };

    return await fetchWithAuth('/api/preferences/doctor_id=' + id, requestOptions);
  }

  /**
   * Retrieves all the preferences of a doctor
   * @param id The doctor's id
   * @returns {Promise<*[]>} A Promise containing the doctor's preferences
   */
  async getDesiderate(id) {
    const response = await fetchWithAuth('/api/preferences/doctor_id='+id);
    const body = await response.json();

    return this.extractDesiderate(body) ;
  }

  /**
   * Retrieves the days of the preferences of a doctor
   * @param id The doctor's id
   * @returns {Promise<*[]>} A Promise containing the doctor's preference days
   */
  async getDesiderateDate(id){
    let desiderate = await(this.getDesiderate(id))
    let desiderateDate = []
    for(let i = 0; i < desiderate.length; i++){
      desiderateDate[i] = desiderate[i].data
    }
    return desiderateDate
  }

  /**
   * Deletes a doctor's preference
   * @param idDesiderata The preference id
   * @param idUtente The user's id
   * @returns {Promise<number>} A Promise containing the deletion result
   */
  async deleteDesiderate(idDesiderata, idUtente){
    const response = await fetchWithAuth('/api/preferences/preference_id='+idDesiderata+'/doctor_id='+idUtente,
      { method: 'DELETE' });
    return response.status;
  }

  /**
   * Edits the preferences of a doctor, eventually adding new ones and deleting unwanted ones <br/>
   * Calls <b>POST api/preferences/edit</b>
   * @param prefsToEdit a list of the preferences that have been sent to be saved/edited
   * @param prefsToDelete the preferences that need to be deleted
   * @param doctorId the id of the doctor whose preferences need to be edited
   * @returns {Promise<*[]>} a Promise containing the saved preferences with eventually new ids
   */
  async editDesiderate(prefsToEdit, prefsToDelete, doctorId) {
    const reqBody = {} ;
    reqBody.doctorId = doctorId ;
    reqBody.remainingPreferences = [] ;
    reqBody.preferencesToDelete = [] ;

    prefsToEdit.forEach((value) => {
      const adaptedPreference = {} ;

      if(value.idDesiderata !== undefined) {
        adaptedPreference.id = value.idDesiderata ;
      }

      adaptedPreference.year= value.data.getFullYear() ;
      adaptedPreference.month= value.data.getMonth() +1 ;
      adaptedPreference.day= value.data.getDate() ;
      adaptedPreference.turnKinds = value.turnKinds ;

      reqBody.remainingPreferences.push(adaptedPreference) ;
    }) ;

    prefsToDelete.forEach((value) => {
      if(value.hasOwnProperty('idDesiderata')) {
        const adaptedPreference = {} ;

        adaptedPreference.doctorId = doctorId ;
        adaptedPreference.preferenceId = value.idDesiderata ;

        reqBody.preferencesToDelete.push(adaptedPreference) ;
      }
    }) ;

    const response = await fetchWithAuth('/api/preferences/edit',
      {method : "POST", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(reqBody)});

    const body = await response.json();

    const retVal = this.extractDesiderate(body) ;
    retVal.status = response.status ;

    return  retVal;
  }

}
