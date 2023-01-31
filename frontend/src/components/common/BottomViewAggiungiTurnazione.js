import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import SelectCategoria from './SelectCategoria';
import Button from '@mui/material/Button';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {CategoriaUtenteAPI} from "../../API/CategoriaUtenteAPI";

export default function TemporaryDrawer(props) {

  const [dataInizio,setDataInizio] = React.useState("")
  const [dataFine,setDataFine] = React.useState("")
  const [categoria,setCategoria] = React.useState("")
  const [state, setState] = React.useState({bottom: false});

  //Sono costretto a dichiarare questa funzione per poterla invocare in modo asincrono.
  async function getCategoria() {
  }

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare la data.
  //Viene passata al componente <BasicDatePicker>
  const handleDataInizio = (dataInizio) => {
    setDataInizio(dataInizio);
  }

  const handleDataFine = (dataFine) => {
    setDataFine(dataFine);
  }

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare il turno.
  //Viene passata al componente <MultipleSelect>
  const handleCategoria = (categoria) => {
    setCategoria(categoria);
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
  const aggiungiCategoria= (anchor, open) => async (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });

    let categoriaUtenteAPI = new CategoriaUtenteAPI()
    let url = window.location.href;
    let index = url.lastIndexOf("/");
    let utente_id = url.substring(index+1);
    let status; //Codice di risposta http del server. In base al suo valore è possibile capire se si sono verificati errori
    status = await categoriaUtenteAPI.postAggiungiTurnazione(categoria, dataInizio, dataFine, utente_id)

    props.onPostAssegnazione()

    //Verifico la risposta del server analizzando il codice di risposta http
    if(status===202){
      toast.success('Rotazione creata con successo', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }else if (status === 400){
      toast.error('Errore nei parametri. Riprova con nuove date', {
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
        }} > <i className="fa fa-plus" aria-hidden="true"></i></Button>
        <Drawer anchor='bottom' open={state['bottom']} onClose={toggleDrawer('bottom', false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '65vh',
          }}>
            <Stack spacing={1}>
                <label>Data inizio</label>
                <BasicDatePicker  onSelectData={handleDataInizio}></BasicDatePicker>
              <label>Data fine</label>
              <BasicDatePicker  onSelectData={handleDataFine}></BasicDatePicker>
              <label>Rotazioni</label>
              <SelectCategoria onSelectCategoria = {handleCategoria} ></SelectCategoria>

              <Button variant="contained" size="small" onClick={aggiungiCategoria('bottom', false)}>
                <i className="fa fa-plus" aria-hidden="true"> </i>
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
