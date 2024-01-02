export  class ScheduleAPI {


  async getSchedulazini() {
    const response = await fetch('/api/schedule/');
    const body = await response.json();
    return body;
  }

  async getSchedulesOnlyWithStartAndEndDate() {
    const response = await fetch('/api/schedule/dates/');
    const body = await response.json();
    return body;
  }

  async getSchedulaziniIllegali() {
    const response = await fetch('/api/schedule/illegals');
    const body = await response.json();
    return body;
  }


  async deleteSchedulo(idSchedulo){
    const response = await fetch('/api/schedule/id='+idSchedulo,
      { method: 'DELETE' });
    return response.status;
  }

  async rigeneraSchedulo(id) {

    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    };

    const response = await fetch('/api/schedule/regeneration/id='+id,requestOptions);
    return response.status;

}


}
