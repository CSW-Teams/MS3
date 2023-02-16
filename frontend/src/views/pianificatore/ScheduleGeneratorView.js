import React from "react";
import Button from '@mui/material/Button';
import BasicDatePicker from '../../components/common/DataPicker';
import Stack from '@mui/material/Stack';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

/*
* Schermata che permette di generare un nuovo schedulo
*/
export class SchedulerGeneratorView extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            dataStart: "",
            dataEnd: "",


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
          toast.error('Errore nella generazione della pianificazione', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });

        }else{
          toast.success('Pianificazione creata con successo', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });
        }
    }

    render(){
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

            </Stack>
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
            </div>
        )
    }

}
