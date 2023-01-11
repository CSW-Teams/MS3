export class CategoriaUtenteAPI {
  constructor() {
  }
  async getCategoriaUtente(idUtente) {
    const response = await fetch('/api/categorie/utente_id='+idUtente);
    const body = await response.json();
    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      let categoria = new Object();
      categoria.categoria = body[i].categoria
      categoria.inizio = body[i].inizioValidita
      categoria.fine = body[i].fineValidita
      categorie[i]=categoria
    }

    console.log(categorie)
    return categorie;

  }
}
