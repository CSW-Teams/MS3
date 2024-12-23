import {fetchWithAuth} from "../utils/fetchWithAuth";

export class ScheduleAPI {


  async getSchedulazini() {
    const response = await fetchWithAuth('/api/schedule/');
    return await response.json();
  }

  async getSchedulesOnlyWithStartAndEndDate() {
    const response = await fetchWithAuth('/api/schedule/dates/');
    const body = await response.json();
    return body;
  }

  async getSchedulaziniIllegali() {
    const response = await fetchWithAuth('/api/schedule/illegals');
    const body = await response.json();
    return body;
  }


  async deleteSchedule(idSchedulo) {
    const response = await fetchWithAuth('/api/schedule/id=' + idSchedulo,
      {method: 'DELETE'});
    return response.status;
  }

  async rigeneraSchedule(id) {

    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
    };

    const response = await fetchWithAuth('/api/schedule/regeneration/id=' + id, requestOptions);
    return response.status;

  }


}
