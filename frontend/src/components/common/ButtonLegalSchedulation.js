import CheckCircleOutlineSharpIcon from '@mui/icons-material/CheckCircleOutlineSharp';import Button from "@mui/material/Button";
import WarningAmberSharpIcon from '@mui/icons-material/WarningAmberSharp';
import React, { useState} from "react";
import { ScheduleAPI } from "../../API/ScheduleAPI";
import Tooltip from '@mui/material/Tooltip';
import { t } from "i18next";
import {toast} from "react-toastify";
import {panic} from "./Panic";


export default function ButtonLegalSchedulation() {

  const [AllLegal,setallLegal] = React.useState(true)
  const [title,setTitle] = React.useState()


  // Scarico le schedulazioni illegali dal server
  async function getScheduleIllegal() {
    let scheduloAPI = new ScheduleAPI();
    let responde
    try {
      responde = await scheduloAPI.getSchedulaziniIllegali();
    } catch (err) {

      panic()
      return
    }
    let AllLegal = responde.length ==0;
    setallLegal(AllLegal);

    let title;

    if(AllLegal)
      title = t("All schedulations are complete")
    else
      title =t("Some schedulations are incomplete")

    setTitle(title)

  }

  //Aggiorno in modo asincrono lo stato del componente
  React.useEffect(() => {
    getScheduleIllegal();
  }, []);



  return (

    <Tooltip title={title}>
      {AllLegal?
                <CheckCircleOutlineSharpIcon style={{color: "rgb(0, 128, 55)",'margin-left': '96%','margin-right': 'auto','display': 'block', 'margin-top':'-3%'}} />
          :
                <WarningAmberSharpIcon  style={{color: "rgb(243, 156, 18)",'margin-left': '96%','margin-right': 'auto','display': 'block', 'margin-top':'-3%'}}/>

      }
    </Tooltip>

    )
}
