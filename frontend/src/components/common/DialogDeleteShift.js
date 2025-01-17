import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { toast } from 'react-toastify';
import { TurnoAPI } from "../../API/TurnoAPI";
import { panic } from "./Panic";

toast.configure();

export default function DialogDeleteShift({ currentShiftInfo, updateShiftsList, disabled }) {
  const [open, setOpen] = React.useState(false);
  const turnoAPI = new TurnoAPI();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleRemove = async () => {
    try {
      await turnoAPI.deleteShift(currentShiftInfo.id);
      updateShiftsList(currentShiftInfo); // Rimuove il turno dall'elenco.
      toast.success("Turno eliminato con successo.");
    } catch (err) {
      panic();
      toast.error("Errore durante l'eliminazione del turno.");
    }
    setOpen(false);
  };

  const handleNotRemove = () => {
    setOpen(false);
  };

  return (
    <div>
      <Tooltip title={(disabled) ? "Impossibile eliminare il turno." : "Elimina turno"}>
        <span>
          <IconButton
            variant="outlined"
            aria-label="delete"
            disabled={disabled}
            color="error"
            onClick={handleClickOpen}
          >
            <DeleteIcon />
          </IconButton>
        </span>
      </Tooltip>
      <Dialog
        open={open}
        onClose={handleNotRemove}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Conferma eliminazione turno"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Sei sicuro di voler eliminare il turno {currentShiftInfo.name}? Questa azione non può essere annullata.
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
