import {MedicalService} from "../entity/MedicalService";
import {Task} from "../entity/Task";
import {fetchWithAuth} from "../utils/fetchWithAuth";

export class ServiceAPI {
  constructor() {
  }

  async getService() {
    try {
      const response = await fetchWithAuth('/medical-services/');

      if (!response.ok) {
        throw new Error('Failed to fetchWithAuth data');
      }

      const body = await response.json();
      const servizi = body.map(item => item.name);
      return servizi;
    } catch (error) {
      console.error('Error fetching data:', error.message);
      return [];
    }
  }

  alphaSort(array) {
    return array.sort((a, b) => a.taskType.localeCompare(b.taskType));
  }

  async getAllServices() {
    const response = await fetchWithAuth('/api/medical-services/');
    const body = await response.json();

    const services = [];

    for (let i = 0; i < body.length; i++) {
      var receivedTaskList = body[i].mansioni;
      this.alphaSort(receivedTaskList);
      var taskList = [];
      for (let j = 0; j < receivedTaskList.length; j++) {
        taskList.push(new Task(receivedTaskList[j].id, receivedTaskList[j].taskType, receivedTaskList[j].assigned));
      }
      var service = new MedicalService(
        body[i].id,
        body[i].nome,
        taskList
      );
      services[i] = service;
    }
    return services;
  }

  async getAvailableTaskTypes() {
    const response = await fetchWithAuth('/api/medical-services/available-task-types');
    const body = await response.json();
    return body;
  }

  async createMedicalService(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetchWithAuth('/api/medical-services/', requestOptions);
    return response;
  }

  async updateMedicalService(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetchWithAuth('/api/medical-services/update', requestOptions);
    return response;
  }

  async deleteMedicalService(params) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(params)
    };

    const response = await fetchWithAuth('/api/medical-services/delete', requestOptions);
    return response;
  }
}
