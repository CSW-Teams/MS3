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
import EditIcon from '@mui/icons-material/Edit';
import Tooltip from '@mui/material/Tooltip';
import CloseIcon from "@mui/icons-material/Close";
import {ServizioAPI} from "../../API/ServizioAPI";

toast.configure();

const MedicalServiceUpdateDrawer = ({tasks, services, updateServicesList, currentServiceInfo}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => {
        setNewMedicalServiceName(currentServiceInfo.name);
        setSelectedTasks(currentServiceInfo.taskTypesString.split(", "));
        setOpen(true);
    }
    const handleClose = () => setOpen(false);
    const [selectedTasks, setSelectedTasks] = useState(currentServiceInfo.taskTypesString.split(", "));
    const [newMedicalServiceName, setNewMedicalServiceName] = useState(currentServiceInfo.name);

    const servizioAPI = new ServizioAPI();
    const names = Object.values(tasks);

    const handleCheckboxChange = (newTask) => {
        if (!selectedTasks.includes(newTask)) {
            setSelectedTasks(prev => [...prev, newTask]);
        } else {
            setSelectedTasks(selectedTasks.filter(item => item !== newTask));
        }
    };

    const isTaskTypeSelected = (item) => {
        return currentServiceInfo.taskTypesString.includes(item["item"]);
    }

    const isTaskTypeAssigned = (item) => {
        var taskBeingAnalyzed=currentServiceInfo.taskTypesList.filter(task => task.taskType===(item));
        return taskBeingAnalyzed[0] && taskBeingAnalyzed[0].assigned;
    }

    function alphaSort(array) {
        return array.sort((a, b) => a.localeCompare(b));
    }

    const postNewRequest = () => {
        handleClose();
        alphaSort(selectedTasks);
        const tasksArray = [];
        for (let i = 0; i < selectedTasks.length; i++) {
            var task = {};
            task.taskType = selectedTasks[i];
            tasksArray.push(task);
        }
        var params = {
            id:       currentServiceInfo.id,
            nome:     newMedicalServiceName,
            mansioni: tasksArray
        }
        var service = {
            name:           newMedicalServiceName,
            taskTypesString:  selectedTasks
        }
        const serviceNew = {}
        var str = "";
        for (let j = 0; j < selectedTasks.length; j++) {
            str = str.concat(
              selectedTasks[j],
              (j!=selectedTasks.length-1) ? ", " : ""
              );
        }
        serviceNew.id               = params.id
        serviceNew.name             = newMedicalServiceName.toUpperCase();
        serviceNew.taskTypesString  = str;
        setNewMedicalServiceName("");
        setSelectedTasks([]);
        servizioAPI.updateMedicalService(params);

        updateServicesList(serviceNew);
        toast.success("Servizio modificato con successo.");
    };

    return (
        <>
        <Tooltip title="Modifica servizio">
            <IconButton variant="outlined" aria-label="edit" color="primary" onClick={handleOpen}>
                <EditIcon />
            </IconButton>
        </Tooltip>
        <Drawer anchor="bottom" open={open} onClose={handleClose}>
            <AppBar position="static" color="transparent">
                <Toolbar>
                    <Box sx={{ display: 'flex', flexGrow: 1, justifyContent: 'center' }}>
                        <Typography variant="h5" component="div" sx={{ marginLeft: '20px' }}>
                            Modifica servizio
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
                        id            = "outlined-basic"
                        autoFocus     = "true"
                        label         = "Nome del servizio"
                        required      = "true"
                        defaultValue  = {currentServiceInfo.name.toUpperCase()}
                        variant       = "outlined"
                        onChange      = {
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
                            <Tooltip title = {
                            isTaskTypeAssigned(item) ?
                                "Impossibile rimuovere la mansione. Essa ha delle associazioni."
                                :
                                "Assegna/rimuovi mansione"}
                                >
                                <FormControlLabel
                                    key={item}
                                    control={
                                        <Checkbox
                                            defaultChecked  = {isTaskTypeSelected({item})}
                                            onChange        = {() => handleCheckboxChange(item)}
                                            disabled        = {isTaskTypeAssigned(item)}
                                        />
                                    }
                                    label={`${item}`}
                                />
                            </Tooltip>
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
                        Modifica servizio
                    </Button>
                </Box>
            </div>
        </Drawer>
        </>
    );
};
export default MedicalServiceUpdateDrawer;
