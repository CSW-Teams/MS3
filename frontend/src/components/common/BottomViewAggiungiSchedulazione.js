import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { toast } from 'react-toastify';
import { t } from "i18next";


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

  const toggleDrawer = (open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setOpen(open);
  };

  const aggiungiSchedulo = () => {
    // Validazione Input
    if (!dataInizio || !dataFine) {
        toast.error(t("Please select both dates for schedule generation."), {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            draggable: true,
            theme: "colored",
        });
        return; // Blocca l'esecuzione se le date non sono selezionate
    }

    // 1. Chiudi subito il drawer per dare feedback immediato
    setOpen(false);

    // 2. Delega al padre la logica (ScheduleGeneratorView.js)
    if (props.onGenerateSchedule) {
      // Chiamata senza await per non bloccare la chiusura del drawer, il padre gestisce il loading globale
      props.onGenerateSchedule(dataInizio, dataFine);
    }
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

        <Drawer anchor='bottom'
                open={open}
                onClose={toggleDrawer(false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '45vh',
          }}>
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
          </div>
        </Drawer>
      </React.Fragment>
    </div>
  );
}
