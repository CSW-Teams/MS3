

export  class DesiderateAPI {

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
      desiderate.push(desiderata)
    }

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(desiderate)
    };

    const response = await fetch('/api/preferences/doctor_id='+id, requestOptions);

    return response;
  }

  /**
   * Retrieves all the preferences of a doctor
   * @param id The doctor's id
   * @returns {Promise<*[]>} A Promise containing the doctor's preferences
   */
  async getDesiderate(id) {
    const response = await fetch('/api/preferences/doctor_id='+id);
    const body = await response.json();

    const desiderate = [];

    for (let i = 0; i < body.length; i++) {
      let des = {}
      des.idDesiderata = body[i].preferenceId
      des.data= new Date(body[i].year, body[i].month-1, body[i].day).toLocaleDateString()
      desiderate[i]=des;
    }
    return desiderate;
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
    const response = await fetch('/api/preferences/preference_id='+idDesiderata+'/doctor_id='+idUtente,
      { method: 'DELETE' });
    return response.status;
  }



}
