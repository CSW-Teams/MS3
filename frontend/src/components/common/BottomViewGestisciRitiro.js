import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';
import {useState} from "react";
import {TurnoAPI} from "../../API/TurnoAPI";
import {AssegnazioneTurnoAPI} from "../../API/AssegnazioneTurnoAPI";
import {AppointmentForm} from "@devexpress/dx-react-scheduler-material-ui";
import {UtenteAPI} from "../../API/UtenteAPI";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import {Checkbox} from "@mui/material";

const TemporaryDrawerRetirement = ({request, shifts, users}) => {
  const [open, setOpen] = useState(false);
  const [selectedReperibili, setSelectedReperibili] = useState([]);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const shift = shifts.find(shift => shift.id === request.idShift);
  console.log("Shift:", shift);

  const retiringUser = users.find(user => user.id === request.idUser);
  console.log(retiringUser);

  const handleCheckboxChange = (userId) => {
    // Verifica se l'utente è già stato selezionato
    if (selectedReperibili.includes(userId)) {
      // Rimuovi l'utente dalla lista dei selezionati
      setSelectedReperibili((prevSelected) =>
        prevSelected.filter((id) => id !== userId)
      );
    } else {
      // Aggiungi l'utente alla lista dei selezionati
      setSelectedReperibili((prevSelected) => [...prevSelected, userId]);
    }
  };

  const handleApprove = () => {
    // Logica per l'approvazione
    console.log('Approvato:', selectedReperibili);
    handleClose();
  };

  const handleReject = () => {
    // Logica per il rifiuto
    console.log('Rifiutato:', selectedReperibili);
    handleClose();
  };

  return (
    <>
      <Button onClick={handleOpen}>
        Gestisci richiesta
      </Button>

      <Drawer anchor="bottom" open={open} onClose={handleClose}>
        <div style={{textAlign: 'center', padding: '20px'}}>
          <Typography variant="h5">
            {retiringUser.text} vuole ritirarsi
          </Typography>

          <Typography variant="h6">Utenti Reperibili:</Typography>

          {shift.utenti_reperibili.map((utente) => (
            <FormControlLabel
              key={utente.id}
              control={
                <Checkbox
                  checked={selectedReperibili.includes(utente.id)}
                  onChange={() => handleCheckboxChange(utente.id)}
                />
              }
              label={`${utente.nome} ${utente.cognome}`}
            />
          ))}

          <Box mt={2}>
            <Button
              variant="contained"
              color="success"
              onClick={handleApprove}
              disabled={selectedReperibili.length === 0}
            >
              Approva
            </Button>

            <Button
              variant="contained"
              color="error"
              onClick={handleReject}
              style={{ marginLeft: '10px' }}
            >
              Rifiuta
            </Button>
          </Box>

        </div>
      </Drawer>
    </>
  );
};

export default TemporaryDrawerRetirement;


