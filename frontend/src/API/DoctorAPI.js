

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

      doctors[i] = doctor;
    }

    return doctors;

  }
}
