import CheckCircleOutlineSharpIcon from '@mui/icons-material/CheckCircleOutlineSharp';import Button from "@mui/material/Button";
import WarningAmberSharpIcon from '@mui/icons-material/WarningAmberSharp';
import React, { useState} from "react";
import { ScheduloAPI } from "../../API/ScheduloAPI";
import Tooltip from '@mui/material/Tooltip';



export default function ButtonLegalSchedulation() {
 
  const [AllLegal,setallLegal] = React.useState(true)
  const [title,setTitle] = React.useState()

  
  // Scarico le schedulazioni illegali dal server
  async function getScheduleIllegal() {
    let scheduloAPI = new ScheduloAPI();
    let responde = await scheduloAPI.getSchedulaziniIllegali();
    let AllLegal = responde.length ==0;
    setallLegal(AllLegal);

    let title;

    if(AllLegal)
      title ="Tutte le schedulazioni sono complete"
    else
      title ="Alcune schedulazioni sono incomplete"

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
