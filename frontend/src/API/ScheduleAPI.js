import {fetchWithAuth} from "../utils/fetchWithAuth";

/**
 * @see docs/scheduling_flow/README.md
 * This class is an API wrapper for managing Schedule entities.
 * It handles fetching, deleting (DELETE /api/schedule/id=...), and regenerating (POST /api/schedule/regeneration/id=...) schedules.
 * These methods are primarily used by the SchedulerGeneratorView to manage the lifecycle of schedules.
 */
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
