

export  class DesiderateAPI {

  async salvaDesiderate(date,id) {
    console.log(id)
    let desiderate = new Object()

    for (let i = 0; i < date.length; i++) {
      let date_i = new Object()
      let desiderata = new Object()
      date_i= date[i].toString().split("/")


      desiderata.anno= date_i[0]
      desiderata.mese=date_i[1]
      desiderata.giorno=date_i[2]

      desiderate[i]=desiderata

    }
    console.log(desiderate)

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(desiderate)
    };

    const response = await fetch('/api/desiderate/utente_id='+id, requestOptions);

    return response;
  }

  async getDesiderate(id) {
    const response = await fetch('/api/desiderate/utente_id='+id);
    const body = await response.json();

    const desiderate = [];

    for (let i = 0; i < body.length; i++) {
      desiderate[i] = body[i];
    }

    return desiderate;
  }


  async deleteDesiderate(idDesiderata, idUtente){
    const response = await fetch('/api/desiderate/desiderata_id='+idDesiderata+'/utente_id='+idUtente,
      { method: 'DELETE' });
    return response.status;
  }



}
