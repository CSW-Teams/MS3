import {Doctor} from "../entity/Doctor";
import {fetchWithAuth} from "../utils/fetchWithAuth";


export class DoctorAPI {

  async getAllDoctorsInfo() {
    const response = await fetchWithAuth('/api/doctors/');
    const body = await response.json();

    const doctors = [];

    for (let i = 0; i < body.length; i++) {
      let seniority = body[i].seniority === "STRUCTURED" ? "Strutturato" : (body[i].seniority === "SPECIALIST_JUNIOR" ? "Specializzando I/II anno" : "Specializzando III/IV/V anno");
      const doctor = {};
      doctor.id = body[i].id;
      doctor.name = body[i].name;
      doctor.lastname = body[i].lastname;
      doctor.seniority = seniority;
      doctor.task="";
      doctors[i] = doctor;
    }
    return doctors;
  }

  async getDoctorById(id) {
    const response = await fetchWithAuth('/api/doctors/' + id);
    const body = await response.json();
    return new Doctor(body.id, body.name, body.lastname, body.seniority,"");
  }
}
