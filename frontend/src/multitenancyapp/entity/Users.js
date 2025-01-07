export class User {

  constructor(id, name, lastname, email, hospitals, jwt){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.email = email;
    this.systemHospitals = hospitals;
    this.jwt = jwt
    this.label = name+" "+lastname+" - "+email;
  }
}
