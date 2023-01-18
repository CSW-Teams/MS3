export class CategoriaUtenteAPI {
  constructor() {
  }

  async getCategoriaUtente(idUtente) {
    const response = await fetch('/api/categorie/stato/utente_id='+idUtente);
    const body = await response.json();
    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      let categoria = new Object();
      categoria.categoria = body[i].categoria.nome
      categoria.inizio = body[i].inizioValidita
      categoria.fine = body[i].fineValidita
      categorie[i]=categoria
    }

    console.log(categorie)
    return categorie;

  }

  async getSpecializzazioniUtente(idUtente) {
    const response = await fetch('/api/categorie/specializzazioni/utente_id='+idUtente);
    const body = await response.json();
    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      let categoria = new Object();
      categoria.categoria = body[i].categoria.nome
      categoria.inizio = body[i].inizioValidita
      categoria.fine = body[i].fineValidita
      categorie[i]=categoria
    }

    console.log(categorie)
    return categorie;
  }

  async getTurnazioniUtente(idUtente) {
    const response = await fetch('/api/categorie/turnazioni/utente_id='+idUtente);
    const body = await response.json();
    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      let categoria = new Object();
      categoria.categoria = body[i].categoria.nome
      categoria.inizio = body[i].inizioValidita
      categoria.fine = body[i].fineValidita
      categorie[i]=categoria
    }

    console.log(categorie)
    return categorie;
  }
}
