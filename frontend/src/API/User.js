export class User {

  constructor(id, name, lastname, systemActor){
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.systemActor = systemActor;
    this.label = name+" "+lastname+" - "+systemActor;
  }
}
