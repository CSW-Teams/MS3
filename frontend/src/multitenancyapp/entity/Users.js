export class User {

  constructor(id, name, lastname, email, hospitals){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.email = email;
    this.systemHospitals = hospitals;
    this.label = name+" "+lastname+" - "+email;
  }
}
