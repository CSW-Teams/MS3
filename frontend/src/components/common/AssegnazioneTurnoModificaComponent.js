import {
    AppointmentForm,
  } from '@devexpress/dx-react-scheduler-material-ui';

  import React, {useState} from 'react';
import Drawer from '@material-ui/core/Drawer';
import { UtenteAPI } from '../../API/UtenteAPI';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import { Button } from '@mui/material';
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
 * Questo componente aggiunge una label al form che permette di modificare un assegnazione turno
 * @param {*} param0
 * @returns
 */
export  const BasicLayout = ({ onFieldChange, appointmentData, ...restProps }) => {

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

  export  const SingleLayout = ({ onFieldChange, appointmentData, ...restProps }) => {
    const [user,setUser] = React.useState([{}])
    const [utentiSelezionati,setUtentiSelezionati] = React.useState([])
    let assegnazioneTurnoApi = new AssegnazioneTurnoAPI();

    async function getUser() {
      let userApi = new UtenteAPI();
      let utenti = await userApi.getAllUsersInfo()
      setUser(utenti);
    }

    async function buildAssegnazioneModificata(){
      let response = await assegnazioneTurnoApi.richiediRinunciaTurno(utentiSelezionati,appointmentData,1)
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

          let turni = await assegnazioneTurnoApi.getGlobalTurn();
          this.forceUpdate();
        }

    }

    React.useEffect(() => {
      getUser();
    }, []);

    return (

      <AppointmentForm.BasicLayout
        appointmentData={appointmentData}
        onFieldChange={onFieldChange}
        {...restProps}
      >

      <Autocomplete
        options={user}
        onChange={(event, value) =>  setUtentiSelezionati(value)}
        sx={{ width: 300 }}
        renderInput={(params) => <TextField {...params} label="Seleziona sostituto" />}
      />

      <Button onClick={()=>{buildAssegnazioneModificata()}}>Salva</Button>

      </AppointmentForm.BasicLayout>
    );
  };


  export  const CommandLayout = ({ ...restProps }) => {

    return (

      <AppointmentForm.CommandLayout
        hideDeleteButton
        disableSaveButton
        {...restProps}
      >


      </AppointmentForm.CommandLayout>
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
 * Questo componente crea la base del form attraverso cui sarà possibile modificare un assegnazione turno
 * @param {*} param0
 * @returns
 */
export const Overlay = ({
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
        {children}
        <div style={{height: '15vh',}}></div>
      </Drawer>

    );
  };


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
        <div style={{display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center'}}>
            <h2>Chiedi sostituzione</h2>
        </div>

        {children}
        <div style={{height: '15vh',}}></div>
      </Drawer>

    );
  };
