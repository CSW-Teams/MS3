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
import {t} from "i18next";

export default function RoleSelectionDialog({open, onClose, systemActors}) {
  const roles = []
  systemActors.forEach((systemActor) => {
    roles.push({
      name: t(systemActor),
      imgPath: `/images/${systemActor.toLowerCase()}.svg`
    })
  })

  const onSelectRole = (role) => {
    onClose(role);
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
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
          <Typography variant="h5">{t("Select role to assign:")}</Typography>
        </div>

        <Grid
          container
          spacing={2}
          wrap="nowrap"
          style={{overflowX: 'auto', display: 'flex', justifyContent: 'center'}}
        >
          {roles.map((role) => (
            <Grid
              item
              key={role.name}
              style={{
                flex: '1 1 0',
                maxWidth: '200px'
              }} // Le card si adattano e mantengono una larghezza massima
            >
              <Card style={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column'
              }}>
                <CardActionArea onClick={() => onSelectRole(role.name)}>
                  <CardMedia
                    component="img"
                    height="140"
                    image={role.imgPath}
                    alt="Role Icon"
                    style={{objectFit: 'contain', padding: 16}}
                  />
                  <CardContent style={{flexGrow: 1}}>
                    <Typography variant="body2" color="textSecondary">
                      {t("Login as:")}
                    </Typography>
                    <Typography variant="h6">{role.name}</Typography>
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
