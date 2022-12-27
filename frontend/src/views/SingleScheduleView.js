import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import { UtenteAPI } from '../API/UtenteAPI';
import ScheduleView  from "./ScheduleView.js"
import {AppointmentSingleContent} from "../components/common/CustomAppointmentComponents.js"
import React from "react";

/**
 * A view showing shift scheduling of a single user
 */




export default class SingleScheduleView extends ScheduleView {


  async componentDidMount() {
    let apiTurno = new AssegnazioneTurnoAPI();
    let turni = await apiTurno.getTurnByIdUser('1');


    // FIXME: Only colleagues should be queried here, not all users
    let utenti = await (new UtenteAPI()).getAllUser();

    this.setState(
      {appointmentContentComponent:AppointmentSingleContent},
    )
    super.componentDidMount(turni, utenti);

  }

  render() {
    return super.render();
  }
}


