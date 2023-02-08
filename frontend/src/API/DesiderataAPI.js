

export  class DesiderateAPI {

  async salvaDesiderate(date,id) {
    console.log(id)

    let desiderate = []

    for (let i = 0; i < date.length; i++) {
      let desiderata = new Object()

      desiderata.anno= date[i].year
      desiderata.mese= date[i].month.number
      desiderata.giorno= date[i].day

      desiderate.push(desiderata)

    }

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
