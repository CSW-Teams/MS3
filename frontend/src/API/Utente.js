export class Utente{

  constructor(id, nome, cognome, systemActor){
    this.id = id;
    this.nome = nome;
    this.cognome = cognome;
    this.systemActor = systemActor;
    this.label = nome+" "+cognome+" - "+systemActor;
  }
}
