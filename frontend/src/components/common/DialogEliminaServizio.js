import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { ToastContainer, toast } from 'react-toastify';
import {ServizioAPI} from "../../API/ServizioAPI";

toast.configure();

export default function DialogEliminaServizio({currentServiceInfo, updateServicesList}) {
  const [open, setOpen] = React.useState(false);
  const servizioAPI = new ServizioAPI();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleRemove = () => {
    servizioAPI.deleteMedicalService(currentServiceInfo.id);
    updateServicesList(currentServiceInfo);
    toast.success("Servizio eliminato con successo.");
    setOpen(false);
  };

  const handleNotRemove = () => {
    setOpen(false);
  };

  return (
    <div>
      <Button variant="outlined" color="error" onClick={handleClickOpen}>
        Elimina
      </Button>
      <Dialog
        open={open}
        onClose={handleNotRemove}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Conferma eliminazione servizio"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Eliminare il servizio {currentServiceInfo.name}?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleNotRemove} color="primary">
            No
          </Button>
          <Button onClick={handleRemove} color="error" autoFocus>
            Sì
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
