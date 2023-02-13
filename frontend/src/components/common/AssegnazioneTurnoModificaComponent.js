/**
 * Questo file contiene tutte le componenti necessarie per modificare il template grafico in base alle nostre esigenze.
 * Il templete grafico utilizzato mette già a disposizione componenti grafici per poter effettuare la modifica di un turno.
 * Le componenti presenti in questo file servono per personalizzare le componenti grafiche già esistenti.
 * Le componenti grafiche da personalizzare sono sostanzialmente 3:
 *    - Overlay : componente che permette di far sollevare dal basso il drawer nel momento in cui si decide di
 *      modificare un assegnazione.
 *    - CommandLayout : definisce quali elementi devono essere visualizzati sulla cima del drawer. Di default è visualizzato
 *      il bottone "save" e il bottone per eliminare un assegnazione turno.
 *    - BasicLayout: Definisce gli elementi che devono essere visualizzati all'interno del drawer.
 * Di seguito sono presenti i componenti utilizzati sia in singleScheduleView che in GlobalScheduleView
 */

import {
    AppointmentForm,
  } from '@devexpress/dx-react-scheduler-material-ui';

import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import { UtenteAPI } from '../../API/UtenteAPI';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import { Button, Stack } from '@mui/material';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { ToastContainer, toast } from 'react-toastify';


/**
 * Componente utilizzato per visualizzare i messaggi di errori della violazione dei vincoli
 * quando si modifica un assegnazione turno
 * @param {} props
 * @returns
 */
function ViolationLog(props){

  return (
    <div>
        <ul>
          {props.log.map((msg) => <li> {msg} </li>)}
        </ul>
    </div>
  );
}



/**
 * Questo componente è utilizzato nello ScheduleGlobalView. Serve solo per aggiungere una label al tamplate grafico.
 * @param {*} param0
 * @returns
 */
export const BasicLayout = ({ onFieldChange, appointmentData, ...restProps }) => {

    return (

      <AppointmentForm.BasicLayout
        appointmentData={appointmentData}
        onFieldChange={onFieldChange}
        {...restProps}
      >
        <AppointmentForm.Label
          text="Effettua le modifiche selezionando i nuovi utenti da allocare "
          type="ordinaryLabel"
        />

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

      </AppointmentForm.BasicLayout>
    );
  };

  /**
   * Queto componente è invece utilizzato da SingleScheduleView. Quando si vorrà modificare un turno da SingleScheduleView
   * è perchè si vuole richiedere di scambiare un turno con un altro utente. Questo componente contiene
   * la logica per chiedere al backend di modificare un assegnazione turno.
   * @param {*} param0
   * @returns
   */
  export function SingleLayout ({ onFieldChange, appointmentData, ...restProps }) {
    const [user,setUser] = React.useState([{}])
    const [utentiSelezionati,setUtentiSelezionati] = React.useState([])
    let assegnazioneTurnoApi = new AssegnazioneTurnoAPI();

    async function getUser() {
      let userApi = new UtenteAPI();
      let utenti = await userApi.getAllUsersInfo()
      setUser(utenti);
    }

    React.useEffect(() => {
      getUser();
    }, []);

    /**
     * Riceve in ingresso il "contesto" dello schedule view. In questo modo può invocare la funzione che
     * permette di aggiornare i turni sullo schedulo nel momento in cui avviene la modifica.
     * @param {*} contesto
     */
    async function buildAssegnazioneModificata(contesto){

      let response = await assegnazioneTurnoApi.richiediRinunciaTurno(utentiSelezionati,appointmentData,localStorage.getItem("id"))
      let responseStatusClass = Math.floor(response.status / 100)

        if(responseStatusClass==5){

          toast.error('Errore nel server!', {
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

        else if(responseStatusClass!= 2){

          let responseBody = await response.json();

          toast.error(ViolationLog({log : responseBody.messagges}), {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
            autoClose: false,
          });

        }else{

          toast.success('Assegnazione creata con successo', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });

          //Aggiorno i turni sull'interfaccia
          let turni = await assegnazioneTurnoApi.getTurnByIdUser(localStorage.getItem("id"));
          contesto.setState({data:turni});



        }

    }



    return (

      <AppointmentForm.BasicLayout
        appointmentData={appointmentData}
        onFieldChange={onFieldChange}
        {...restProps}
      >

      <Stack spacing={2} style={{
          display: 'flex',
          'padding-top': '20px',
          justifyContent: 'center',
        }}>

      <Autocomplete
        options={user}
        onChange={(event, value) =>  setUtentiSelezionati(value)}
        renderInput={(params) => <TextField {...params} label="Seleziona sostituto" />}
      />

      <Button onClick={()=>{buildAssegnazioneModificata(this)}}>Salva</Button>
      </Stack>
      </AppointmentForm.BasicLayout>
    );
  };




/**
 * Questo componente è utilizzato nel form che si crea quando bisogna modificare un assegnazione turno.
 * Serve per eliminare alcune componenti che il template mette a disposizione di default.
 * Utilizzato nel componente <AppointmentForm>
 * @returns
 */
export const Nullcomponent = () => {
    // eslint-disable-next-line react/destructuring-assignment
    return null;
  };


/**
 * Questo componente è utilizzato nel GlobalScheduleView. Leggere i commenti a inizio file per capire la sua funzione.
 * @param {*} param0
 * @returns
 */
export function Overlay({
    children,
    visible,
    className,
    fullSize,
    target,
    onHide,
  }) {

    return (
      <Drawer anchor='bottom' open={visible} >
        <div style={{height: '5vh',}}></div>
        {children}
        <div style={{height: '15vh',}}></div>
      </Drawer>

    );
  };


  /**
 * Questo componente è utilizzato in SingleScheduleView.Ha la stessa funzione di Overlay ma è utilizzato in SingleScheduleView
 * invece che in GlobalScheduleView.
 * Leggere i commenti a inizio file per capire la sua funzione.
 * @param {*} param0
 * @returns
 */
  export const OverlaySingle = ({
    children,
    visible,
    className,
    fullSize,
    target,
    onHide,
  }) => {


    return (
      <Drawer anchor='bottom' open={visible} >
        <div style={{height: '5vh',}}></div>

            <h3 style={{
                'position': 'relative',
                'z-index': '4000',
                'display': 'block',
                'margin-left': 'auto',
                'margin-right': 'auto',
                'margin-top':'1%',
                'margin-bottom':'-3%'
            }} >Chiedi sostituzione</h3>


        {children}
        <div style={{height: '15vh',}}></div>
      </Drawer>

    );
  };





