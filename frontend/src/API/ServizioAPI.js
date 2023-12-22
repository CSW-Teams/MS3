export class ServizioAPI {
  async getService() {
    try {
      const response = await fetch('/servizi/');

      if (!response.ok) {
        throw new Error('Failed to fetch data');
      }

      const body = await response.json();
      const servizi = body.map(item => item.name);
      return servizi;
    } catch (error) {
      console.error('Error fetching data:', error.message);
      return [];
    }
  }
}
