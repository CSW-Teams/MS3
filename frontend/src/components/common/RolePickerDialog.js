import React from 'react';
import {
  Card,
  CardActionArea,
  CardContent,
  Dialog,
  DialogContent,
  Grid
} from '@mui/material';
import Typography from "@mui/material/Typography";

export default function RoleSelectionDialog({open, onClose, systemActors}) {
  const onSelectRole = (role) => {
    onClose(role);
  };

  const roles = []

  systemActors.forEach((systemActor) => {
    roles.push({name: systemActor, description: 'Accedi come ' + systemActor})
  })

  const handleClose = () => {
    onClose();
  };

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogContent>
          <Grid container spacing={2}>
            {roles.map((role) => (
              <Grid item key={role.name}>
                <Card>
                  <CardActionArea onClick={() => onSelectRole(role.name)}>
                    <CardContent>
                      <Typography variant="h6">{role.name}</Typography>
                      <Typography variant="body2" color="textSecondary">
                        {role.description}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              </Grid>
            ))}
          </Grid>
        </DialogContent>
      </Dialog>
    </div>
  );
}
