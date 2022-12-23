import ScheduleView from "./ScheduleView.js"
import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import { UtenteAPI } from '../API/UtenteAPI';

class GlobalScheduleView extends ScheduleView {

  async componentDidMount() {
    
    let utenti = [];
    let turni = [];
    let turnoAPI = new AssegnazioneTurnoAPI();
    let utenteAPI = new UtenteAPI();

    utenti = await utenteAPI.getAllUser();
    turni = await turnoAPI.getGlobalTurn();

    super.componentDidMount(turni, utenti);
  }

  render(){
    return super.render();
  }
}
export default GlobalScheduleView;
