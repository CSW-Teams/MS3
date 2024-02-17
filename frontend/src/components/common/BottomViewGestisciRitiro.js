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
import {Modal} from "react-bootstrap";
import {toast} from "react-toastify";
import { t } from "i18next";


const TemporaryDrawerRetirement = ({request, shifts, users, updateRequest}) => {
  const [open, setOpen] = useState(false);
  const [openAlert, setOpenAlert] = useState(false);
  const [selectedReperibile, setSelectedReperibile] = useState(null);

  const shift = shifts.find(shift => shift.id === request.idShift);

  const retiringUser = users.find(user => user.id === request.idRequestingUser);

  let seniority = retiringUser.seniority === "STRUCTURED" ? "Strutturato" : "Specializzando";
  const doctorInfo = retiringUser.name + " " + retiringUser.lastname + " - " + seniority;

  const handleOpen = () => {
    if (shift.utenti_reperibili.length === 0) {
      setOpenAlert(true);
    } else {
      setOpen(true);
    }
  }
  const handleClose = () => {
    setOpen(false);
    setOpenAlert(false);
  }

  const handleCheckboxChange = (userId) => {
    setSelectedReperibile(userId);
  };

  const richiestaRimozioneDaTurnoAPI = new RichiestaRimozioneDaTurnoAPI();

  const handleApprove = () => {

    handleClose();

    request = {
      idRequest: request.idRequest,
      idShift: request.idShift,
      idRequestingUser: request.idRequestingUser,
      idSubstitute: selectedReperibile,
      outcome: true,
      justification: request.descrizione,
      examined: true,
      file: request.file
    }


    updateRequest(request);

    try {
      return richiestaRimozioneDaTurnoAPI.risolviRichiesta(request);
    } catch (err) {

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
    }

  };

  const handleReject = () => {
    handleClose();

    request = {
      idRequest: request.idRequest,
      idShift: request.idShift,
      idRequestingUser: request.idRequestingUser,
      idSubstitute: null,
      outcome: false,
      justification: request.justification,
      examined: true,
      file: request.file
    }


    updateRequest(request);

    try {
      return richiestaRimozioneDaTurnoAPI.risolviRichiesta(request);
    } catch (err) {

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
    }
  };


  return (
    <>
      <Button onClick={handleOpen}>
        Gestisci richiesta
      </Button>

      <Modal show={openAlert} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Errore</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div style={{textAlign:'center'}}>
            <p>Non sono disponibili utenti reperibili per questo turno</p>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="primary" onClick={handleClose}>
            Ok
          </Button>
        </Modal.Footer>
      </Modal>


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
              {doctorInfo} ha richiesto di ritirarsi dal turno.
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
                label={`${utente.name} ${utente.lastname}`}
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


