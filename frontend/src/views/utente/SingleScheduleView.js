import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { UserAPI } from '../../API/UserAPI';
import ScheduleView  from "./ScheduleView.js"
import {AppointmentSingleContent} from "../../components/common/CustomAppointmentComponents.js"
import React from "react";
import { ToastContainer, toast } from 'react-toastify';
import {Button} from "@mui/material";
import { t } from "i18next";


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

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
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
      <ToastContainer
        position="top-center"
        autoClose={5000}
        hideProgressBar={true}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
      { super.render()}
    </div>

    )

  }
}

