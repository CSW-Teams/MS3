import {fetchWithAuth} from "../utils/fetchWithAuth";

/**
 * Deprecated class
 */
export class CategoriaAPI {

  async getTurnazioni() {

    const response = await fetchWithAuth('/api/categorie/turnazioni/');
    const body = await response.json();

    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      categorie[i] = body[i].nome;
    }

    return categorie;

  }

  async getStati() {

    const response = await fetchWithAuth('/api/categorie/stato/');
    const body = await response.json();

    const categorie = [];

    for (let i = 0; i < body.length; i++) {
      categorie[i] = body[i].nome;
    }

    return categorie;

  }

}
