import React from "react"
import ScheduleView from "./ScheduleView.js"
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { UserAPI } from '../../API/UserAPI';
import TemporaryDrawer from "../../components/common/BottomViewAssegnazioneTurno.js";
import { Stack } from "@mui/system";

class GlobalScheduleView extends ScheduleView {

  async componentDidMount() {

    let utenti = [];
    let turni = [];
    let turnoAPI = new AssegnazioneTurnoAPI();
    let utenteAPI = new UserAPI();

    utenti = await utenteAPI.getAllUser();
    turni = await turnoAPI.getGlobalShift();
    super.componentDidMount(turni, utenti);
  }


  render(){
    return (
      <Stack spacing={3}>
        {localStorage.getItem("actor")!=="DOCTOR" && localStorage.getItem("actor")!=="CONFIGURATOR" && <TemporaryDrawer onPostAssegnazione = {()=>{this.componentDidMount() ;}} ></TemporaryDrawer>}
        {super.render("global")}
      </Stack>
      );
  }
}
export default GlobalScheduleView;
