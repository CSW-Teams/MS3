export class CategoriaUtenteAPI {
  constructor() {
  }
  async getCategoriaUtente(idUtente) {
    const response = await fetch('/api/categoria/utente_id='+idUtente);
    const body = await response.json();

    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      categorie[i].categoria = body[i].categoria
      categorie[i].inizio = body[i].inizioValidita
      categorie[i].fine = body[i].fineValidita
    }

    console.log(categorie)
    return categorie;

  }
}
