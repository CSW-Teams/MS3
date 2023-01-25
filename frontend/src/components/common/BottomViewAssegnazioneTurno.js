import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import MultipleSelect from './MultipleSelect';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { UtenteAPI } from '../../API/UtenteAPI';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import InformationDialogs from './InformationVincoloComponent';
import {MDBCard, MDBCardBody, MDBCardTitle} from "mdb-react-ui-kit";
import FilesUpload from './FilesUpload'
import {Icon} from "@material-ui/core";



export default function TemporaryDrawer(props) {

  const [user,setUser] = React.useState([{}])
  const [data,setdata] = React.useState("")
  const [turno,setTurno] = React.useState("")
  const [servizio,setServizio] = React.useState("")
  const [forced,setForced] = React.useState(false)
  const [utentiSelezionatiGuardia,setUtentiSelezionatiGuardia] = React.useState([])
  const [utentiSelezionatiReperibilità,setUtentiSelezionatiReperibilita] = React.useState([])
  const [state, setState] = React.useState({bottom: false});
  const [commentText,setCommentText] = React.useState(" ")
  const [giustificato, setGiustificato] = React.useState(false)



  //Sono costretto a dichiarare questa funzione per poterla invocare in modo asincrono.
  async function getUser() {
    let userApi = new UtenteAPI();
    let utenti = await userApi.getAllUsersInfo()
    setUser(utenti);
  }

  //Questa funzione aggiorna lo stato del componente.
  React.useEffect(() => {
    getUser();
  }, []);

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare la data.
  //Viene passata al componente <BasicDatePicker>
  const handleData = (data) => {
    setdata(data);
  }


  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare il turno.
  //Viene passata al componente <MultipleSelect>
  const handleTurno = (turno) => {
    setTurno(turno);
  }

  const handleServizio = (servizio) => {
    setServizio(servizio);
  }



  //Funzione che apre la schermata secondaria che permette di creare un associazione.
  //Viene passata come callback al componente <Drawer>
  const toggleDrawer = (anchor, open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

  };

  //La funzione verrà invocata quando l'utente schiaccerà il bottone per creare una nuova assegnazione.
  //Viene passata come callback al componente <Button>Assegna turno</Button>
  const assegnaTurno = (anchor, open) => async (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

    let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI()
    let response; //risposta http del server. In base al suo valore è possibile capire se si sono verificati errori


    response = await assegnazioneTurnoAPI.postAssegnazioneTurno(data,turno,utentiSelezionatiGuardia,utentiSelezionatiReperibilità, servizio,forced)

    //Chiamo la callback che aggiorna i turni visibili sullo scheduler.
    props.onPostAssegnazione()

    //Verifico la risposta del server analizzando il codice di risposta http
    if(response.status===202){
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
      //alert('assegnazione creata con successo');
    }else if (response.status === 400){
      toast.error('Errore nei parametri di input!', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });

      //alert('Errore nei parametri');
    } else if( response.status === 406 ){

      let responseBody = await response.json();
      toast.error('Violazione dei vincoli.'+ responseBody.message, {
        position: "top-center",
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });

    }

    setState({ ...state, [anchor]: open });

  }

  function Giustifica() {
    if (!giustificato)
        return (
          <MDBCard>
              <MDBCardBody>
                <MDBCardTitle className="text-center">Motiva la forzatura</MDBCardTitle>
                <Stack spacing={1}>
                            <textarea
                              name="motivazione"
                              value={commentText}
                              placeholder="Inserisci la motivazione."
                              onChange={e => setCommentText(e.target.value)}>
                             </textarea>
                  <FilesUpload/>
                  <Button onClick={() => setGiustificato(true)}> Conferma </Button>
                </Stack>
              </MDBCardBody>
            </MDBCard>
    )
    return (
      <MDBCard>
        <MDBCardBody>
         Giustificazione compilata.
        </MDBCardBody>
      </MDBCard>
    )};



  return (
    <div>
      <React.Fragment key= 'bottom'>

        <Button onClick={toggleDrawer('bottom', true)} style={{
          'display': 'block',
          'margin-left': 'auto',
          'margin-right': 'auto',
          'margin-top':'1%',
          'margin-bottom':'-1%'
        }} >Aggiungi assegnazione</Button>
        <Drawer anchor='bottom' open={state['bottom']} onClose={toggleDrawer('bottom', false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '75vh',
          }}>


            <Stack spacing={3} >
              <BasicDatePicker onSelectData={handleData}></BasicDatePicker>
              <MultipleSelect onSelectTurno = {handleTurno} onSelectServizio = {handleServizio}></MultipleSelect>
              <Autocomplete
                onChange={(event, value) => setUtentiSelezionatiGuardia(value)}
                multiple
                options={user}
                sx={{ width: 300 }}
                renderInput={(params) => <TextField {...params} label="Medici Guardia" />}
              />
              <Autocomplete
                onChange={(event, value) => setUtentiSelezionatiReperibilita(value)}
                multiple
                options={user}
                sx={{ width: 300 }}
                renderInput={(params) => <TextField {...params} label="Medici Reperibili" />}
              />
              <div>
                <FormControlLabel control={<Switch  onClick={() => {setForced(!forced)}}/>} label="Forza Vincoli non stringenti" />
                <InformationDialogs></InformationDialogs>
                { (forced && <Giustifica  />) && ( <Giustifica  />|| !giustificato)}
              </div>


              <Button variant="contained" size="small" onClick={assegnaTurno('bottom', false)} >
                Assegna turno
              </Button>
            </Stack>
          </div>
        </Drawer>
      </React.Fragment>
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

  );
}
