export class User {

    constructor(id, name, lastname, systemActor, tenant, jwt){
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.systemActor = systemActor;
        this.tenant = tenant;
        this.jwt = jwt
        this.label = name+" "+lastname+" - "+systemActor;
    }
}
