import React from "react"
import ScheduleView from "./ScheduleView.js"
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import TemporaryDrawer from "../../components/common/BottomViewAssegnazioneTurno.js";
import { Stack } from "@mui/system";
import {panic} from "../../components/common/Panic";

class GlobalScheduleView extends ScheduleView {

  async componentDidMount() {

    let turni = [];
    let turnoAPI = new AssegnazioneTurnoAPI();
    try {
      turni = await turnoAPI.getGlobalShift();
    } catch (err) {

      panic()
    }
    super.componentDidMount(turni);
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
