import React, {useState} from 'react';

import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {
  Collapse,
  Divider,
  List,
  ListItem,
  ListItemText
} from "@mui/material";
import { ExpandMore, Delete } from '@mui/icons-material';
import IconButton from "@mui/material/IconButton";

const ShiftItemBox: React.FC<{
  shiftData: {
    id: string;
    timeSlot: string;
    startHour: number;
    startMinute: number;
    durationMinutes: number;
    daysOfWeek: string[];
    medicalService: { label: string };
    quantityshiftseniority: {
      task: string;
      seniority: string;
      quantity: number
    }[];
  };
  onDelete: (shift: number) => void;
}> = ({shiftData, onDelete}) => {
  const [open, setOpen] = useState(false);

  const toggleExpand = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  return (
    <Box
      sx={{
        border: '1px solid #ccc',
        borderRadius: 4,
        padding: 3,
        width: '95%',
        backgroundColor: '#f9f9f9',
        margin: '0 auto',
        position: 'relative',
        marginBottom: 2,
      }}
    >
      <Typography variant="h6" gutterBottom>
        Dettagli Turno
      </Typography>

      {/* Bottone per cancellare */}
      <IconButton
        onClick={() => onDelete(shiftData.id)}
        color="error"
        sx={{
          position: 'absolute',
          top: 16,
          right: 16,
        }}
      >
        <Delete />
      </IconButton>

      {/* Informazioni del turno */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
            <strong>Time Slot:</strong> {shiftData.timeSlot}
          </Typography>

          <Typography variant="body2" sx={{ marginBottom: 1 }}>
            <strong>Ora Inizio:</strong> {shiftData.startHour}:{shiftData.startMinute.toString().padStart(2, '0')}
          </Typography>

          <Typography variant="body2" sx={{ marginBottom: 1 }}>
            <strong>Durata:</strong> {`${Math.floor(shiftData.durationMinutes / 60)} ore ${shiftData.durationMinutes % 60} minuti`}
          </Typography>
        </Box>

        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
            <strong>Giorni della Settimana:</strong>
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            {shiftData.daysOfWeek.map((day, index) => (
              <Typography variant="body2" key={index}>
                {day}
              </Typography>
            ))}
          </Box>
        </Box>
      </Box>

      {/* Contenuto espandibile */}
      <Collapse in={open}>
        <Divider sx={{ marginY: 2 }} />

        {/* Servizio Medico */}
        <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
          <strong>Servizio Medico:</strong> {shiftData.medicalService.label}
        </Typography>

        <Divider sx={{ marginY: 2 }} />

        {/* Quantità per Seniority */}
        <Typography variant="subtitle1" sx={{ marginBottom: 0 }}>
          <strong>Quantità per Seniority:</strong>
        </Typography>
        <List sx={{ paddingLeft: 2, marginTop: 0 }}>
          {shiftData.quantityshiftseniority.map((item, index) => (
            <ListItem key={index} sx={{ paddingY: 0.5 }}>
              <ListItemText
                primary={`${item.task} - ${item.seniority}`}
                secondary={`Quantità: ${item.quantity}`}
                sx={{ margin: 0, padding: 0 }}
              />
            </ListItem>
          ))}
        </List>
      </Collapse>

      {/* Bottone per espandere/ridurre (posizionato in basso a destra) */}
      <IconButton
        onClick={toggleExpand}
        sx={{
          position: 'absolute',
          bottom: 16,
          right: 16,
          transform: open ? 'rotate(180deg)' : 'rotate(0deg)',
          transition: 'transform 0.3s ease',
        }}
      >
        <ExpandMore />
      </IconButton>
    </Box>
  );
};

export default ShiftItemBox;
