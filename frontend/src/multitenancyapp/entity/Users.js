export class User {

  constructor(id, name, lastname, email, tenant, jwt){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.email = email;
    this.tenant = tenant;
    this.jwt = jwt
    this.label = name+" "+lastname+" - "+email;
  }
}
