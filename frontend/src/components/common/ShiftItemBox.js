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
import {t} from "i18next";

const ShiftItemBox: React.FC<{
  shiftData: {
    id: string;
    timeSlot: string;
    startHour: number;
    startMinute: number;
    durationMinutes: number;
    daysOfWeek: string[];
    medicalServices: { label: string };
    quantityShiftSeniority: {
      taskName: string;
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
        {t("Shift details")}
      </Typography>

      {/* Button for shift delete */}
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

      {/* Shift info */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
            <strong>{t("Time Slot")}:</strong> {t(shiftData.timeSlot)}
          </Typography>

          <Typography variant="body2" sx={{ marginBottom: 1 }}>
            <strong>{t("Start hour")}:</strong> {shiftData.startHour}:{shiftData.startMinute.toString().padStart(2, '0')}
          </Typography>

          <Typography variant="body2" sx={{ marginBottom: 1 }}>
            <strong>{t("Shift duration")}:</strong> {Math.floor(shiftData.durationMinutes / 60)} {t("hours")} {shiftData.durationMinutes % 60} {t("minutes")}
          </Typography>
        </Box>

        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
            <strong>{t("Days of the week")}:</strong>
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            {shiftData.daysOfWeek.map((day, index) => (
              <Typography variant="body2" key={index}>
                {t(day)}
              </Typography>
            ))}
          </Box>
        </Box>
      </Box>

      {/* Collapsable content */}
      <Collapse in={open}>
        <Divider sx={{ marginY: 2 }} />

        <Typography variant="subtitle1" sx={{ marginBottom: 1 }}>
          <strong>{t("Medical service")}:</strong> {shiftData.medicalServices.label}
        </Typography>

        <Divider sx={{ marginY: 2 }} />

        {/* Quantity per Seniority  */}
        <Typography variant="subtitle1" sx={{ marginBottom: 0 }}>
          <strong>{t("Quantity per Seniority")}:</strong>
        </Typography>
        <List sx={{ paddingLeft: 2, marginTop: 0 }}>
          {shiftData.quantityShiftSeniority.map((item, index) => (
            <ListItem key={index} sx={{ paddingY: 0.5 }}>
              <ListItemText
                primary={`${t(item.taskName)} - ${t(item.seniority)}`}
                secondary={`${t("Quantity")}: ${item.quantity}`}
                sx={{ margin: 0, padding: 0 }}
              />
            </ListItem>
          ))}
        </List>
      </Collapse>

      {/* Expand/reduce button (located bottom right) */}
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
