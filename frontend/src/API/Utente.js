export class Utente{

    constructor(id, nome, cognome, ruoloEnum, label){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.ruoloEnum = ruoloEnum;
        this.label = nome+" "+cognome+" - "+ruoloEnum;
    }
}