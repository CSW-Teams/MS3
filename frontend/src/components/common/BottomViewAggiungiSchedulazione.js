import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { t } from "i18next";
import "./../../style/LoadingOverlay.css"
import {panic} from "./Panic";


export default function TemporaryDrawerSchedulo(props) {

  const [dataInizio,setDataInizio] = React.useState("")
  const [dataFine,setDataFine] = React.useState("")
  const [open,setOpen] = React.useState(false)
  const [loading, setLoading] = React.useState(false)

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

    setLoading(true)

    let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI();
    let status ;
    try {
      status = await assegnazioneTurnoAPI.postGenerationSchedule(dataInizio,dataFine)
    } catch (err) {

      panic()
      return
    }

    if(status==202){

      toast.success(t("Schedule successfully created"), {
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
    else if (status == 206){
      toast.warning(t("Warning! Incomplete Schedule"), {
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
    else if (status == 406){
      toast.error(t("Error: Schedule already exists"), {
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
    else{
      toast.error(t("Schedule Generation Error"), {
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
    setLoading(false)
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
        }} variant="contained" size="small" > {t("Create schedule")} </Button>

        <Drawer anchor='bottom' open={open} onClose={toggleDrawer(false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '45vh',
          }}>

            {loading ? (
              // Visualizza la schermata di caricamento
              <div className="loading-overlay">
                <div className="loading-spinner"></div>
              </div>
            ) : (
              // Visualizza il contenuto normale quando non è in corso il caricamento
              <Stack spacing={3}>
                <div style={{
                  display: 'flex',
                  justifyContent: 'center',
                }}>
                  {t("Insert Schedule Start and End Date")}
                </div>
                <BasicDatePicker onSelectData={handleDataInizio} label={t("Start Date")}></BasicDatePicker>
                <BasicDatePicker onSelectData={handleDataFine} label={t("End Date")}></BasicDatePicker>
                <Button variant="contained" size="small" onClick={aggiungiSchedulo} >
                  {t("Create schedule")}
                </Button>
              </Stack>
            )}

          </div>
        </Drawer>
      </React.Fragment>
    </div>

  );
}
