import {Doctor} from "../entity/Doctor";


export class DoctorAPI {

  async getAllDoctorsInfo() {
    const response = await fetch('/api/doctors/');
    const body = await response.json();

    const doctors = [];

    for (let i = 0; i < body.length; i++) {
      const doctor = {};
      doctor.id = body[i].id;
      doctor.name = body[i].name;
      doctor.lastname = body[i].lastname;
      doctor.seniority = body[i].seniority;
      doctor.task="";
      doctors[i] = doctor;
    }

    return doctors;
  }

  async getDoctorById(id) {
    const response = await fetch('/api/doctors/' + id);
    const body = await response.json();
    return new Doctor(body.id, body.name, body.lastname, body.seniority,"");
  }
}
