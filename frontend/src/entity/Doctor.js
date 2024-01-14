export class Doctor {

  constructor(id, name, lastname, seniority){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.seniority = seniority;
    this.label = name + " " + lastname + " - " + (seniority === "STRUCTURED" ? "Strutturato" : (seniority === "SPECIALIST_JUNIOR" ? "Specializzando I/II anno" : "Specializzando III/IV/V anno"));
  }
}
