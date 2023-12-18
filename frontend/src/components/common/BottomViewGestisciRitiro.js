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
import {RichiestaRimozioneDaTurnoAPI} from "../../API/RichiestaRimozioneDaTurnoAPI";


const TemporaryDrawerRetirement = ({request, shifts, users, updateRequest}) => {
  const [open, setOpen] = useState(false);
  const [selectedReperibile, setSelectedReperibile] = useState(null);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const shift = shifts.find(shift => shift.id === request.idAssegnazioneTurno);
  console.log("Shift:", shift);

  const retiringUser = users.find(user => user.id === request.idUtenteRichiedente);
  console.log(retiringUser);

  const handleCheckboxChange = (userId) => {
    setSelectedReperibile(userId);
  };

  const richiestaRimozioneDaTurnoAPI = new RichiestaRimozioneDaTurnoAPI();

  const handleApprove = () => {

    handleClose();

    request = {
      idRichiestaRimozioneDaTurno: request.idRichiestaRimozioneDaTurno,
      idAssegnazioneTurno: request.idAssegnazioneTurno,
      idUtenteRichiedente: request.idUtenteRichiedente,
      idUtenteSostituto: selectedReperibile,
      esito: true,
      descrizione: request.descrizione,
      esaminata: true,
      allegato: request.allegato
    }

    console.log("Params:", request)

    updateRequest(request);
    return richiestaRimozioneDaTurnoAPI.risolviRichiesta(request);

  };

  const handleReject = () => {
    handleClose();

    const request = {
      idRichiestaRimozioneDaTurno: request.idRichiestaRimozioneDaTurno,
      idAssegnazioneTurno: request.idAssegnazioneTurno,
      idUtenteRichiedente: request.idUtenteRichiedente,
      idUtenteSostituto: null,
      esito: false,
      descrizione: request.descrizione,
      esaminata: true,
      allegato: request.allegato
    }

    console.log("Params:", request)

    updateRequest(request);
    return richiestaRimozioneDaTurnoAPI.risolviRichiesta(request);
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


