import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';
import {useState} from "react";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import {AppBar, Checkbox, Toolbar} from "@mui/material";
import ClearIcon from '@mui/icons-material/Clear';
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";

const TemporaryDrawerRetirement = ({request, shifts, users}) => {
  const [open, setOpen] = useState(false);
  const [selectedReperibile, setSelectedReperibile] = useState(null);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const shift = shifts.find(shift => shift.id === request.idShift);
  console.log("Shift:", shift);

  const retiringUser = users.find(user => user.id === request.idUser);
  console.log(retiringUser);

  const handleCheckboxChange = (userId) => {
    setSelectedReperibile(userId);
  };

  const handleApprove = (idReperibile) => {
    console.log("Approvato ritiro dell'utente con id", retiringUser.id, ". Verrà sostituito dall'utente con id", selectedReperibile);
    handleClose();
  };

  const handleReject = () => {
    console.log("Ritiro rifiutato");
    handleClose();
  };

  return (
    <>
      <Button onClick={handleOpen}>
        Gestisci richiesta
      </Button>


      <Drawer anchor="bottom" open={open} onClose={handleClose}>

        <AppBar position="static" color="transparent">
          <Toolbar>
            <Box sx={{ display: 'flex', flexGrow: 1, justifyContent: 'center' }}>
              <Typography variant="h5" component="div" sx={{ marginLeft: '20px' }}>
                Gestione richiesta di ritiro
              </Typography>
            </Box>
            <IconButton color="inherit" onClick={handleClose}>
              <CloseIcon />
            </IconButton>
          </Toolbar>
        </AppBar>

        <div style={{textAlign: 'center', padding: '20px'}}>

          <Box
            marginBottom={2}
            marginTop={2}
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
          >
            <Typography variant="h6">
              {retiringUser.text} ha richiesto di ritirarsi dal turno.
            </Typography>
          </Box>

          <Box
            marginBottom={2}
            marginTop={2}
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
          >
            <Typography variant="h6">Seleziona un utente reperibile per la sostituzione:</Typography>
          </Box>

          <Box
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
          >
            {shift.utenti_reperibili.map((utente) => (
              <FormControlLabel
                key={utente.id}
                control={
                  <Checkbox
                    checked={selectedReperibile === utente.id}
                    onChange={() => handleCheckboxChange(utente.id)}
                  />
                }
                label={`${utente.nome} ${utente.cognome}`}
              />
            ))}
          </Box>

          <Box
            mt={4}
          >
            <Button
              variant="contained"
              color="success"
              onClick={handleApprove}
              disabled={selectedReperibile === null}
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


