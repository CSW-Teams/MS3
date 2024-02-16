export class Doctor {

  constructor(id, name, lastname, seniority, task){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.seniority = seniority;
    if(task != ""){
      this.task = (task === "WARD" ? "Guardia" : (task === "CLINIC" ? "Clinica" : (task === "EMERGENCY" ? "Emergenza" : "Sala operatoria")));
    }else{
      this.task = "";
    }
    this.label = name + " " + lastname + " - "+this.task+" - "+ (seniority === "STRUCTURED" ? "Strutturato" : (seniority === "SPECIALIST_JUNIOR" ? "Specializzando I/II anno" : "Specializzando III/IV/V anno"));
    console.log("label: "+this.label)
  }
}
