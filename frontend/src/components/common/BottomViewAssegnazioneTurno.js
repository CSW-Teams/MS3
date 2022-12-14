import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import MultipleSelect from './MultipleSelect';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import { UtenteAPI } from '../../API/UtenteAPI';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import MuiAlert from '@mui/material/Alert';

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});


export default function TemporaryDrawer(props) {

  const [user,setUser] = React.useState([{}])
  const [data,setdata] = React.useState("")
  const [turno,setTurno] = React.useState("")
  const [servizio,setServizio] = React.useState("")
  const [utentiSelezionatiGuardia,setUtentiSelezionatiGuardia] = React.useState([])
  const [utentiSelezionatiReperibilitÃ ,setUtentiSelezionatiReperibilita] = React.useState([])
  const [openMessage,setMessageOpen] = React.useState(true)


  const [state, setState] = React.useState({bottom: false});

  //Sono costretto a dichiarare questa funzione per poterla invocare in modo asincrono.
  async function getUser() {
    let userApi = new UtenteAPI();
    let utenti = await userApi.getAllUserOnlyNameSurname()
    setUser(utenti);
  }

  //Questa funzione aggiorna lo stato del componente.
  React.useEffect(() => {
    getUser();
  }, []);

  //Funzione che implementa l'inversione di controllo. VerrÃ  invocata dal componente figlio che permette di selezionare la data.
  //Viene passata al componente <BasicDatePicker>
  const handleData = (data) => {
    setdata(data);
  }

  //Funzione che implementa l'inversione di controllo. VerrÃ  invocata dal componente figlio che permette di selezionare il turno.
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

  //La funzione verrÃ  invocata quando l'utente schiaccerÃ  il bottone per creare una nuova assegnazione.
  //Viene passata come callback al componente <Button>Assegna turno</Button>
  const assegnaTurno = (anchor, open) => async (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

    let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI()
    let assegnazione = await assegnazioneTurnoAPI.postAssegnazioneTurno(data,turno,utentiSelezionatiGuardia,utentiSelezionatiReperibilitÃ , servizio)

    //Chiamo la callback che aggiorna i turni visibili sullo scheduler.
    props.onPostAssegnazione()

    if(assegnazione==null){
      alert('errore assegnazione');
    }else{
      alert('assegnazione creata con successo');
    }

    setState({ ...state, [anchor]: open });

  }


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
              height: '65vh',
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
                <Button variant="contained" size="small" onClick={assegnaTurno('bottom', false)} >
                  Assegna turno
                </Button>
            </Stack>

            </div>
          </Drawer>
        </React.Fragment>
    </div>
  );
}
