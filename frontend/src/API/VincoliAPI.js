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
      configurazione.maxConsecutiveTimeForOver62 = conf.numMaxOreConsecutiveOver62*60
      configurazione.maxConsecutiveTimeForPregnant = conf.numMaxOreConsecutiveDonneIncinta*60

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
      conf.periodDaysNo = body.periodDaysNo
      conf.periodMaxTime = body.periodMaxTime/60
      conf.horizonNightShift = body.horizonNightShift
      conf.maxConsecutiveTimeForEveryone = body.maxConsecutiveTimeForEveryone/60
      conf.configVincMaxPerConsPerCategoria = []
      for(let i = 0; i < body.configVincMaxPerConsPerCategoria.length; i++){
        conf.configVincMaxPerConsPerCategoria[i] = {}
        conf.configVincMaxPerConsPerCategoria[i].id = body.configVincMaxPerConsPerCategoria[i].id
        conf.configVincMaxPerConsPerCategoria[i].constrainedCondition = body.configVincMaxPerConsPerCategoria[i].constrainedCondition
        conf.configVincMaxPerConsPerCategoria[i].numMaxOreConsecutive = body.configVincMaxPerConsPerCategoria[i].maxConsecutiveMinutes/60
      }
      return conf

    }



  }
