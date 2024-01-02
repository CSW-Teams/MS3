import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { UtenteAPI } from '../../API/UtenteAPI';
import ScheduleView  from "./ScheduleView.js"
import {AppointmentSingleContent} from "../../components/common/CustomAppointmentComponents.js"
import React from "react";
import { ToastContainer, toast } from 'react-toastify';
import {Button} from "@mui/material";


/**
 * A view showing shift scheduling of a single user
 */

export default class SingleScheduleView extends ScheduleView {



  async componentDidMount() {
    let apiTurno = new AssegnazioneTurnoAPI();
    let turni = await apiTurno.getTurnByIdUser(localStorage.getItem("id"));

    // FIXME: Only colleagues should be queried here, not all users
    let utenti = await (new UtenteAPI()).getAllUser();

    this.setState(
      {appointmentContentComponent:AppointmentSingleContent},
    )
    super.componentDidMount(turni, utenti);

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


