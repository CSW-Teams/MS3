import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import {useState} from "react";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import {AppBar, Checkbox, Toolbar} from "@mui/material";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import {ServizioAPI} from "../../API/ServizioAPI";

toast.configure();

const MedicalServiceCreationDrawer = ({tasks, services, updateServicesList}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const [updatedResults, setUR] = useState([]);
    const [selectedTasks, setSelectedTasks] = useState([]);
    const [newMedicalServiceName, setNewMedicalServiceName] = useState("");

    const servizioAPI = new ServizioAPI();
    const names = Object.values(tasks);

    const handleCheckboxChange = (newTask) => {
        if (!selectedTasks.includes(newTask)) {
            setSelectedTasks(prev => [...prev, newTask]);
        } else {
            setSelectedTasks(selectedTasks.filter(item => item !== newTask));
        }
    };

    function alphaSort(array) {
        return array.sort((a, b) => a.localeCompare(b));
    }

    const postNewRequest = () => {
        //check if exists
        const servicesNames = services.map(services => services.name)
        const matches = servicesNames.filter(service => service.toUpperCase() === (newMedicalServiceName.toUpperCase()))
        if(matches.length==0) {
            handleClose();
            alphaSort(selectedTasks);
            var params = {
                name:       newMedicalServiceName,
                taskTypes:  selectedTasks
            }
            var service = {
                name:           newMedicalServiceName,
                taskTypesList:  selectedTasks
            }
            const serviceNew={}
            var str="";
            for (let j = 0; j < selectedTasks.length; j++) {
                str =str.concat(
                  selectedTasks[j],
                  (j!=selectedTasks.length-1) ? ", " : ""
                  );
            }
            serviceNew.name=newMedicalServiceName.toUpperCase();
            serviceNew.taskTypesList=str;
            setNewMedicalServiceName("");
            setSelectedTasks([]);
            servizioAPI.createMedicalService(params);

            updateServicesList(serviceNew);
            toast.success("Servizio creato con successo.");
            } else {
            toast.error("Il servizio è già esistente. Riprovare.");
        }
    };

    return (
        <>
        <Button
            onClick = {handleOpen}
            style   = {{
                'display'       : 'block',
                'margin-left'   : 'auto',
                'margin-right'  : 'auto',
                'margin-top'    : '1%',
                'margin-bottom' : '1%'
            }}
            >
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
                    marginBottom    = {2}
                    marginTop       = {2}
                    display         = "flex"
                    flexDirection   = "column"
                    justifyContent  = "center"
                    alignItems      = "center"
                    >
                    <TextField
                        id        = "outlined-basic"
                        label     = "Nome del servizio"
                        variant   = "outlined"
                        onChange  = {
                            (event) => {
                                setNewMedicalServiceName(event.target.value);
                                event.target.value = (event.target.value).toUpperCase();
                            }
                        }
                    />
                    <p>
                    </p>
                    <Typography variant="h6">
                        Seleziona le mansioni da assegnare:
                    </Typography>
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
                            // Without this, names[0] would be undefined!
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
                        disabled={newMedicalServiceName==="" || selectedTasks.length===0}
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
