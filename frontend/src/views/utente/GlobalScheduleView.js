import React from "react"
import ScheduleView from "./ScheduleView.js"
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import TemporaryDrawer from "../../components/common/BottomViewAssegnazioneTurno.js";
import {panic} from "../../components/common/Panic";
import {MDBCard, MDBCardBody, MDBContainer} from "mdb-react-ui-kit";



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
      <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">
        <MDBCard alignment='center'>
          <MDBCardBody>
            {/*<Stack spacing={3}>*/}
              {localStorage.getItem("actor")!=="DOCTOR" && localStorage.getItem("actor")!=="CONFIGURATOR" && <TemporaryDrawer onPostAssegnazione = {()=>{this.componentDidMount() ;}} ></TemporaryDrawer>}
              {super.render("global")}
            {/*</Stack>*/}
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>

      );
  }
}
export default GlobalScheduleView;
