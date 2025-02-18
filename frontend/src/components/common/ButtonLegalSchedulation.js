import CheckCircleOutlineSharpIcon
  from '@mui/icons-material/CheckCircleOutlineSharp';
import WarningAmberSharpIcon from '@mui/icons-material/WarningAmberSharp';
import React from "react";
import {ScheduleAPI} from "../../API/ScheduleAPI";
import Tooltip from '@mui/material/Tooltip';
import {t} from "i18next";
import {panic} from "./Panic";


export default function ButtonLegalSchedulation() {
  const [allLegal, setAllLegal] = React.useState(true)
  const [title, setTitle] = React.useState()

  // Scarico le schedulazioni illegali dal server
  async function getScheduleIllegal() {
    let scheduloAPI = new ScheduleAPI();
    let response
    try {
      response = await scheduloAPI.getSchedulaziniIllegali();
    } catch (err) {

      panic()
      return
    }
    let isAllLegal = (response.length === 0);
    setAllLegal(isAllLegal);

    let title;

    if (isAllLegal)
      title = t("All schedulations are complete")
    else
      title = t("Some schedulations are incomplete")

    setTitle(title)
  }

  //Aggiorno in modo asincrono lo stato del componente
  React.useEffect(() => {
    getScheduleIllegal();
  }, []);


  return (
    <Tooltip title={title}>
      {allLegal ?
        <CheckCircleOutlineSharpIcon style={{
          color: "rgb(0, 128, 55)",
          'margin-left': '96%',
          'margin-right': 'auto',
          'display': 'block',
          'margin-top': '-3%'
        }}/>
        :
        <WarningAmberSharpIcon style={{
          color: "rgb(243, 156, 18)",
          'margin-left': '96%',
          'margin-right': 'auto',
          'display': 'block',
          'margin-top': '-3%'
        }}/>

      }
    </Tooltip>

  )
}
