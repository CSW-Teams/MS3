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

      configurazione.periodDaysNo = conf.numGiorniPeriodo
      configurazione.periodMaxTime = conf.maxOrePeriodo*60
      configurazione.horizonNightShift = conf.horizonTurnoNotturno
      configurazione.maxConsecutiveTimeForEveryone = conf.numMaxOreConsecutivePerTutti*60
      configurazione.configVincMaxPerConsPerCategoria = []
      configurazione.configVincMaxPerConsPerCategoria[0] = {}
      configurazione.configVincMaxPerConsPerCategoria[1] = {}
      configurazione.configVincMaxPerConsPerCategoria[0].constrainedCondition = conf.categoriaOver62
      configurazione.configVincMaxPerConsPerCategoria[0].maxConsecutiveMinutes = conf.numMaxOreConsecutiveOver62*60
      configurazione.configVincMaxPerConsPerCategoria[1].constrainedCondition = conf.categoriaDonneIncinta
      configurazione.configVincMaxPerConsPerCategoria[1].maxConsecutiveMinutes = conf.numMaxOreConsecutiveDonneIncinta*60


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
      conf.numGiorniPeriodo = body.periodDaysNo
      conf.maxOrePeriodo = body.periodMaxTime/60
      conf.horizonTurnoNotturno = body.horizonNightShift
      conf.numMaxOreConsecutivePerTutti = body.maxConsecutiveTimeForEveryone/60
      conf.configVincoloMaxPeriodoConsecutivoPerCategoria = []
      for(let i = 0; i < body.configVincMaxPerConsPerCategoria.length; i++){
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i] = {}
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].id = body.configVincMaxPerConsPerCategoria[i].id
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].categoriaVincolata = body.configVincMaxPerConsPerCategoria[i].constrainedCondition
        conf.configVincoloMaxPeriodoConsecutivoPerCategoria[i].numMaxOreConsecutive = body.configVincMaxPerConsPerCategoria[i].maxConsecutiveMinutes/60

      }
      return conf

    }



  }
