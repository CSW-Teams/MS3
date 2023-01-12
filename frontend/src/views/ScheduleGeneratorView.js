import React from "react";
import Button from '@mui/material/Button';
import BasicDatePicker from '../components/common/DataPicker';
import Stack from '@mui/material/Stack';
import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import {Alert} from "@mui/lab";
import {Collapse} from "shards-react";
import {IconButton, Snackbar} from "@mui/material";

/*
* Schermata che permette di generare un nuovo schedulo
*/
export class SchedulerGeneratorView extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            dataStart: "",
            dataEnd: "",
            generationDone: ""

        }

    }


    handleDataStart = (data) => {
        this.setState( {dataStart:data})
    }

    handleDataEnd = (data) => {
        this.setState( {dataEnd:data})
    }

    //Al click del bottone richiedo al backend la generazione della pianificazione nel range di date specificate
    handleOnClick = async () =>{
        let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI()
        let assegnazione = await assegnazioneTurnoAPI.postGenerationSchedule(this.state.dataStart,this.state.dataEnd)
        if(assegnazione==null){
          this.setState({generationDone: "-1"})
        }else{
          this.setState({generationDone: "1"})
      }


    }


    render(){
      const generationDone = this.state.generationDone
      let alert
      if(generationDone === "-1"){
        alert = <Alert severity="error" onClose={() => {}}>Errore nella generazione della pianificazione</Alert>
      }else if(generationDone === "1"){
        alert = <Alert severity="success">Pianificazione generata con successo</Alert>
      }

        return (
            <div style={{
                display: 'flex',
                'padding-top': '30px',
                justifyContent: 'center',
                height: '85vh',
              }}>
            <Stack spacing={3} >
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                }}>
                    Inserisci inizio e fine pianificazione
                </div>
                <BasicDatePicker onSelectData={this.handleDataStart} label={"data inizio"}></BasicDatePicker>
                <BasicDatePicker onSelectData={this.handleDataEnd} label={"data fine"}></BasicDatePicker>
                <Button variant="contained" size="small" onClick={this.handleOnClick} >
                  Genera Pianificazione
                </Button>
                {alert}
            </Stack>

            </div>
        )
    }

}
