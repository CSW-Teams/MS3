import React from 'react';
import {
  Card,
  CardActionArea,
  CardContent,
  CardMedia,
  Dialog,
  DialogContent,
  Grid
} from '@mui/material';
import Typography from "@mui/material/Typography";

export default function HospitalSelectionDialog({ open, onClose, hospitals }) {
  const onSelectHospital = (hospital) => {
    onClose(hospital);
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      aria-labelledby="hospital-selection-dialog-title"
      aria-describedby="hospital-selection-dialog-description"
      disableEnforceFocus
    >
      <DialogContent>
        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            marginBottom: 16,
          }}
        >
          <Typography variant="h5">{"Seleziona l'ospedale per accedere:"}</Typography>
        </div>

        <Grid
          container
          spacing={2}
          wrap="nowrap"
          style={{ overflowX: 'auto', display: 'flex', justifyContent: 'center' }}
        >
          {hospitals.map((hospital) => (
            <Grid
              item
              key={hospital.id}
              style={{
                flex: '1 1 0',
                maxWidth: '200px'
              }}
            >
              <Card style={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column'
              }}>
                <CardActionArea onClick={() => onSelectHospital(hospital.id)}>
                  <CardMedia
                    component="img"
                    height="140"
                    image={hospital.imgPath || '/images/default_hospital.svg'}
                    alt="Hospital Icon"
                    style={{ objectFit: 'contain', padding: 16 }}
                  />
                  <CardContent style={{ flexGrow: 1 }}>
                    <Typography variant="body2" color="textSecondary">
                      {"Accesso all'ospedale:"}
                    </Typography>
                    <Typography variant="h6">{hospital.name}</Typography>
                  </CardContent>
                </CardActionArea>
              </Card>
            </Grid>
          ))}
        </Grid>
      </DialogContent>
    </Dialog>
  );
}
