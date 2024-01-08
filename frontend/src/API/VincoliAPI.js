export  class VincoloAPI {
    constructor() {
    }

    async getVincoli() {
        const response = await fetch('/api/constraints/');
        const body = await response.json();

        const vincoli = [];
        for (let i = 0; i < body.length; i++) {
            vincoli[i] = body[i];
        }

        return vincoli;

    }

    async setConfigurazioneVincoli(conf){
      let configurazione = {}

      configurazione.numGiorniPeriodo = conf.numGiorniPeriodo
      configurazione.maxMinutiPeriodo = conf.maxOrePeriodo*60
      configurazione.horizonTurnoNotturno = conf.horizonTurnoNotturno
      configurazione.numMaxMinutiConsecutiviPerTutti = conf.numMaxOreConsecutivePerTutti*60
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria = []
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[0] = {}
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[1] = {}
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[0].categoriaVincolata = conf.categoriaOver62
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[0].numMaxMinutiConsecutivi = conf.numMaxOreConsecutiveOver62*60
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[1].categoriaVincolata = conf.categoriaDonneIncinta
      configurazione.configVincoloMaxPeriodoConsecutivoPerCategoria[1].numMaxMinutiConsecutivi = conf.numMaxOreConsecutiveDonneIncinta*60


      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(configurazione)
      };

      const response = await fetch('/api/constraints/configuration',requestOptions);

      return response;
    }

    async getConfigurazioneVincoli(){
      const response = await fetch('/api/constraints/configuration');
      const body = await response.json();
      const conf = {}
      conf.numGiorniPeriodo = body.numGiorniPeriodo
      conf.maxOrePeriodo = body.maxMinutiPeriodo/60
      conf.horizonTurnoNotturno = body.horizonTurnoNotturno
      conf.numMaxOreConsecutivePerTutti = body.numMaxMinutiConsecutiviPerTutti/60
      conf.configVincoloMaxPeriodoConsecutivoPerCategoria = []
      for(let i = 0; i < body.configVincoloMaxPeriodoConsecutivoPerCategoria.length; i++){
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i] = {}
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].id = body.configVincoloMaxPeriodoConsecutivoPerCategoria[i].id
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].categoriaVincolata = body.configVincoloMaxPeriodoConsecutivoPerCategoria[i].categoriaVincolata
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].numMaxOreConsecutive = body.configVincoloMaxPeriodoConsecutivoPerCategoria[i].numMaxMinutiConsecutivi/60

      }
      return conf

    }



  }
