export class ServizioAPI {
    constructor() {
    }

//TODO deprecare?
  async getService() {
    try {
      const response = await fetch('/medical-services/');

      if (!response.ok) {
        throw new Error('Failed to fetch data');
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
        const response = await fetch('/api/medical-services/');
        const body = await response.json();

        const services = [];

        for (let i = 0; i < body.length; i++) {
            const service = {};
            service.id        = body[i].id;
            service.name      = body[i].nome;
            let assignedCheck = false;
            var taskTypesString = body[i].mansioni;
            this.alphaSort(taskTypesString);
            service.taskTypesString = "";
            service.taskTypesList = [];
            for (let j = 0; j < taskTypesString.length; j++) {
                if(taskTypesString[j].assigned == true){
                    assignedCheck = true;
                }
                service.taskTypesString = service.taskTypesString.concat(
                    taskTypesString[j].taskType,
                    (j!=taskTypesString.length-1) ? ", " : ""
                );
                service.taskTypesList.push(taskTypesString[j]);
            }
            service.assigned = assignedCheck;
            services[i] = service;
        }
        return services;
    }

    async getAvailableTaskTypes() {
        const response = await fetch('/api/medical-services/available-task-types');
        const body = await response.json();
        return body;
    }

    async createMedicalService(params) {
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(params)
        };

        const response = await fetch('/api/medical-services/', requestOptions);
        return response;
    }

    async updateMedicalService(params) {
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(params)
        };

        const response = await fetch('/api/medical-services/update', requestOptions);
        return response;
    }

    async deleteMedicalService(params) {
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(params)
        };

        const response = await fetch('/api/medical-services/delete', requestOptions);
        return response;
    }
}
