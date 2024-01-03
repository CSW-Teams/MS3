import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';
import {useState} from "react";
import Typography from "@mui/material/Typography";
import {AppBar, Toolbar} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";

const MedicalServiceCreationDrawer = ({tasks}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    //test
    var names = Object.values(tasks).map(
        function(item) {
            return item['availableTaskTypes'];
            }
        );
    return (
        <>
        <Button onClick={handleOpen}>
            Crea nuovo servizio
        </Button>
        <Drawer anchor="bottom" open={open} onClose={handleClose}>
            <AppBar position="static" color="transparent">
                <Toolbar>
                    <Box sx={{ display: 'flex', flexGrow: 1, justifyContent: 'center' }}>
                        <Typography variant="h5" component="div" sx={{ marginLeft: '20px' }}>
                            Crea nuovo servizio
                        </Typography>
                    </Box>
                    <IconButton color="inherit" onClick={handleClose}>
                        <CloseIcon/>
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
                        {names[1]} ha richiesto di ritirarsi dal turno.
                        {tasks[1]} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify(names)} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify(tasks)[1]} ha richiesto di ritirarsi dal turno.
                        {Object.values(tasks)[1]} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify((tasks))} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify(Object.values(tasks))} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify(Object.values(tasks)[1])} ha richiesto di ritirarsi dal turno.
                        {JSON.stringify(Object.values(tasks))[1]} ha richiesto di ritirarsi dal turno.
                    </Typography>
                </Box>
                <Box
                    display="flex"
                    flexDirection="column"
                    justifyContent="center"
                    alignItems="center"
                    >
                </Box>
                <Box
                    mt={4}
                    >
                    <Button
                        variant="contained"
                        color="success"
                        //disabled={TODO}
                        >
                        Crea servizio
                    </Button>
                </Box>
            </div>
        </Drawer>
        </>
    );
};
export default MedicalServiceCreationDrawer;
