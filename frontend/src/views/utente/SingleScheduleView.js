import {AssegnazioneTurnoAPI} from '../../API/AssegnazioneTurnoAPI';
import ScheduleView from "./ScheduleView.js"
import {
  AppointmentSingleContent
} from "../../components/common/CustomAppointmentComponents.js"
import React from "react";
import {panic} from "../../components/common/Panic";
import {MDBCard, MDBCardBody, MDBContainer} from "mdb-react-ui-kit";


/**
 * A view showing shift scheduling of a single user
 */
export default class SingleScheduleView extends ScheduleView {

  async componentDidMount() {
    let apiTurno = new AssegnazioneTurnoAPI();
    let turni
    try {
      turni = await apiTurno.getShiftByIdUser(localStorage.getItem("id"));
    } catch (err) {

      panic()
      super.componentDidMount(turni)
      return
    }

    this.setState(
      {appointmentContentComponent: AppointmentSingleContent},
    )
    super.componentDidMount(turni);

  }

  render() {
    return (
      <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">
        <MDBCard alignment='center'>
          <MDBCardBody>
            {super.render()}
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
    )
  }
}
