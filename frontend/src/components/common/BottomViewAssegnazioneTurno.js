import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import MultipleSelect from './TurniListSelectorView';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import { UtenteAPI } from '../../API/UtenteAPI';


export default function TemporaryDrawer(props) {

  const [user,setUser] = React.useState([{}])
  const [data,setdata] = React.useState("")
  const [turno,setTurno] = React.useState("")
  const [utentiSelezionati,setUtentiSelezionati] = React.useState([])
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

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare la data.
  const handleData = (data) => {
    setdata(data);
  }

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare il turno.
  const handleTurno = (turno) => {
    setTurno(turno);
  }

  const toggleDrawer = (anchor, open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

  };

  const assegnaTurno = (anchor, open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

    console.log(data)
    console.log(turno)
    console.log(utentiSelezionati)
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
              height: '50vh',
            }}>
              
            
            <Stack spacing={3}>
                <BasicDatePicker onSelectData={handleData}></BasicDatePicker>
                <MultipleSelect serviceName={props.serviceName} onSelectTurno={handleTurno}></MultipleSelect>
                <Autocomplete
                  onChange={(event, value) => setUtentiSelezionati(value)}
                  multiple
                  options={user}
                  sx={{ width: 300 }}
                  renderInput={(params) => <TextField {...params} label="Medici" />}
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
