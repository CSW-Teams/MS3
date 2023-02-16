import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';

export default function TemporaryDrawerSchedulo(props) {

  const [dataInizio,setDataInizio] = React.useState("")
  const [dataFine,setDataFine] = React.useState("")
  const [open,setOpen] = React.useState(false)

  const handleDataInizio = (dataInizio) => {
    setDataInizio(dataInizio);
  }

  const handleDataFine = (dataFine) => {
    setDataFine(dataFine);
  }


  //Funzione che apre la schermata secondaria che permette di creare un nuovo schedulo.
  //Viene passata come callback al componente <Drawer>
  const toggleDrawer = (open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setOpen(open);
  };

  //La funzione verrà invocata quando l'utente schiaccerà il bottone per creare un nuovo schedulo.
  const aggiungiSchedulo= async () => {
    let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI();
    let assegnazione = await assegnazioneTurnoAPI.postGenerationSchedule(dataInizio,dataFine)

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
    setOpen(false)
    props.onPostGeneration();
  }

  return (
    <div>
      <React.Fragment key= 'bottom'>
        <Button onClick={toggleDrawer(true)} style={{
          'display': 'block',
          'margin-left': 'auto',
          'margin-right': 'auto',
          'margin-top':'3%',
        }} variant="contained" size="small" > Crea schedulazione </Button>

        <Drawer anchor='bottom' open={open} onClose={toggleDrawer(false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '45vh',
          }}>
            
            <Stack spacing={3} >
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                }}>
                    Inserisci inizio e fine pianificazione
                </div>
                <BasicDatePicker onSelectData={handleDataInizio} label={"data inizio"}></BasicDatePicker>
                <BasicDatePicker onSelectData={handleDataFine} label={"data fine"}></BasicDatePicker>
                <Button variant="contained" size="small" onClick={aggiungiSchedulo} >
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
        </Drawer>
      </React.Fragment>
    </div>

  );
}
