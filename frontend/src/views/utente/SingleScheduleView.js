import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { UserAPI } from '../../API/UserAPI';
import ScheduleView  from "./ScheduleView.js"
import {AppointmentSingleContent} from "../../components/common/CustomAppointmentComponents.js"
import React from "react";
import { ToastContainer, toast } from 'react-toastify';
import {Button} from "@mui/material";
import { t } from "i18next";
import {panic} from "../../components/common/Panic";


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
      {appointmentContentComponent:AppointmentSingleContent},
    )
    super.componentDidMount(turni);

  }

  render() {


    return (
      <div>
      { super.render()}
    </div>

    )

  }
}

