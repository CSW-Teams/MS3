import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';
import {useState} from "react";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import {AppBar, Checkbox, Toolbar} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import {ServizioAPI} from "../../API/ServizioAPI";

const MedicalServiceCreationDrawer = ({tasks}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const [selectedTasks, setSelectedTasks] = useState([]);

    const servizioAPI = new ServizioAPI();
    const names = Object.values(tasks);

    const handleCheckboxChange = (newTask) => {
        if (!selectedTasks.includes(newTask)) {
            setSelectedTasks(prev => [...prev, newTask]);
        } else {
            setSelectedTasks(selectedTasks.filter(item => item !== newTask));
        }
    };

    const postNewRequest = () => {
        handleClose();
        return servizioAPI.createMedicalService(selectedTasks);
    };

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
                        hhhhhh
                    </Typography>
                    <p>
                        yo
                    </p>
                    {
                    (names && names[0]) ?
                        Object.values(names[0]).map((item) => (
                            <FormControlLabel
                                key={item}
                                control={
                                    <Checkbox
                                        onChange={() => handleCheckboxChange(item)}
                                    />
                                }
                                label={`${item}`}
                            />
                            ))
                        :
                            (
                            {names}
                            )
                    }
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
                        onClick={postNewRequest}
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
