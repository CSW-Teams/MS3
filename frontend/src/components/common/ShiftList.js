import React, {useState} from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  Divider,
  List,
  ListItem,
  ListItemText,
  Typography
} from "@mui/material";
import CircleIcon from "@mui/icons-material/Circle";

const ShiftItem = ({shift}) => {
  const [open, setOpen] = useState(false); // Stato per aprire/chiudere il Dialog

  // Funzione per aprire il dialog
  const handleClickOpen = () => {
    console.log('clicked');
    setOpen(true);
  };

  // Funzione per chiudere il dialog
  const handleClose = () => {
    setOpen(false);
  };

  // Formattiamo data e orari
  const dateObj = new Date(shift.startDate);
  const formattedDate = dateObj.toLocaleDateString("it-IT", {
    day: "2-digit",
    month: "short",
    weekday: "short",
  });

  const formattedStartTime = new Date(shift.startDate).toLocaleTimeString(
    [],
    {hour: "numeric", minute: "2-digit"}
  );

  const formattedEndTime = new Date(shift.endDate).toLocaleTimeString([], {
    hour: "numeric",
    minute: "2-digit",
  });

  return (
    <>
      {/* Dialog */}
      <Dialog open={open} onClose={handleClose}>
        <DialogContent>
          <Typography variant="h6" sx={{
            textAlign: 'center',
            fontWeight: 'bold',
            marginBottom: 1
          }}>Dettagli del turno</Typography>
          <Typography
            variant="body2"><strong>Servizio:</strong> {shift.servizio}
          </Typography>
          <Typography
            variant="body2"><strong>Tipologia:</strong> {shift.tipologia}
          </Typography>
          <Typography variant="body2"><strong>Stato:</strong> {shift.shiftState}
          </Typography>
          <Typography
            variant="body2"><strong>Mansione:</strong> {shift.mansione}
          </Typography>
          <Typography variant="body2"><strong>Reperibilità
            Attiva:</strong> {shift.reperibilitaAttiva ? "Sì" : "No"}
          </Typography>

          <Typography variant="body2"><strong>Utenti in
            Guardia:</strong></Typography>
          <List sx={{padding: 0}}>
            {shift.utenti_guardia.map((user) => (
              <ListItem key={user.id} sx={{paddingTop: 0, paddingBottom: 0}}>
                <ListItemText primary={user.label}/>
              </ListItem>
            ))}
          </List>

          <Typography variant="body2"><strong>Utenti
            Reperibili:</strong></Typography>
          <List sx={{padding: 0}}>
            {shift.utenti_reperibili.map((user) => (
              <ListItem key={user.id} sx={{paddingTop: 0, paddingBottom: 0}}>
                <ListItemText primary={user.label}/>
              </ListItem>
            ))}
          </List>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Chiudi
          </Button>
        </DialogActions>
      </Dialog>

      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          paddingY: 1.2,
          paddingX: 2,
          width: "100%",
          cursor: "pointer",
          transition: "transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1), background-color 0.3s",
          backgroundColor: "transparent",
          borderRadius: "20px",
          ":active": {
            backgroundColor: "rgba(0, 0, 0, 0.1)", // Scurisce il background al click
            transform: "scale(0.95)", // Effetto di riduzione della dimensione per l'animazione del click
          },
          ":hover": {
            backgroundColor: "rgba(0, 0, 0, 0.05)", // Aggiunge un effetto al passaggio del mouse
          },
        }}

        onClick={() => handleClickOpen()}
      >
        {/* Data */}
        <Typography
          variant="body2"
          sx={{minWidth: "80px", fontWeight: "bold"}}
        >
          {formattedDate.toUpperCase()}
        </Typography>

        {/* Pallino colorato */}
        <CircleIcon sx={{color: shift.color, fontSize: 12, marginX: 1}}/>

        {/* Orario */}
        <Typography variant="body2" sx={{minWidth: "100px"}}>
          {shift.allDay ? "Tutto il giorno" : `${formattedStartTime} – ${formattedEndTime}`}
        </Typography>

        {/* Titolo */}
        <Typography variant="body1" sx={{fontWeight: "bold", marginLeft: 2}}>
          {shift.title}
        </Typography>
      </Box>
    </>
  );
};

const ShiftList = ({shifts}) => {
  const sortedShifts = [...shifts].sort((a, b) =>
    new Date(a.startDate) - new Date(b.startDate)
  );

  // Raggruppiamo per giorno
  const groupedShifts = sortedShifts.reduce((acc, shift) => {
    const dateKey = new Date(shift.startDate).toLocaleDateString("it-IT");
    if (!acc[dateKey]) acc[dateKey] = [];
    acc[dateKey].push(shift);
    return acc;
  }, {});

  return (
    <Box sx={{ width: "100%", padding: 2 }}>
      {Object.entries(groupedShifts).length === 0 ? (
        <Box sx={{ textAlign: "center", color: "gray" }}>
          <p>No shifts available</p>
        </Box>
      ) : (
        Object.entries(groupedShifts).map(([date, shiftsForDate], index) => (
          <Box key={date}>
            {index > 0 && <Divider sx={{ my: 1, borderColor: "#333" }} />}
            {shiftsForDate.map((shift) => (
              <ShiftItem key={shift.id} shift={shift} />
            ))}
          </Box>
        ))
      )}
    </Box>
  );
};

export default ShiftList;
