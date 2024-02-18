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
import {MedicalService} from "../../entity/MedicalService";
import {Task} from "../../entity/Task";
import { t } from "i18next";
import {panic} from "./Panic";

toast.configure();

const MedicalServiceUpdateDrawer = ({availableTasks, services, updateServicesList, currentServiceInfo}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => {
        setNewMedicalServiceName(currentServiceInfo.name);
        setCheckedTasksStringArray(currentServiceInfo.getTasksAsString().split(", "));
        setOpen(true);
    }
    const handleClose = () => setOpen(false);
    const [checkedTasksStringArray, setCheckedTasksStringArray] = useState(currentServiceInfo.getTasksAsString().split(", "));
    const [newMedicalServiceName, setNewMedicalServiceName] = useState(currentServiceInfo.name);

    const serviceAPI = new ServizioAPI();
    const names = Object.values(availableTasks);

    const handleCheckboxChange = (newTask) => {
        if (!checkedTasksStringArray.includes(newTask)) {
            setCheckedTasksStringArray(prev => [...prev, newTask]);
        } else {
            setCheckedTasksStringArray(checkedTasksStringArray.filter(item => item !== newTask));
        }
    };

    const isTaskTypeSelected = (item) => {
        return currentServiceInfo.getTasksAsString().includes(item["item"]);
    }

    const isTaskTypeAssigned = (taskTypeParam) => {
        var taskBeingAnalyzed = currentServiceInfo.tasks.filter(task => task.taskType === (taskTypeParam));
        return taskBeingAnalyzed[0] && taskBeingAnalyzed[0].assigned;
    }

    function alphaSort(array) {
        return array.sort((a, b) => a.localeCompare(b));
    }

    const postNewRequest = () => {
        handleClose();
        alphaSort(checkedTasksStringArray);

        // build params for API request and view update
        const outTaskArray = [];
        for (let i = 0; i < checkedTasksStringArray.length; i++) {
            outTaskArray.push(new Task(null, checkedTasksStringArray[i], isTaskTypeAssigned(checkedTasksStringArray[i])));
        }

        // API request params built differently (e.g. not as a MedicalService object)
        // for compliance wrt other modules
        var requestParams = {
            id          : currentServiceInfo.id,
            nome        : newMedicalServiceName.toUpperCase(),
            mansioni    : outTaskArray
        }

        try {
          serviceAPI.updateMedicalService(requestParams);
        } catch (err) {

          panic()
          return
        }

        // build service infos for view update
        var viewUpdateServiceInfo = new MedicalService (
            currentServiceInfo.id,
            newMedicalServiceName.toUpperCase(),
            outTaskArray
            );
        updateServicesList(viewUpdateServiceInfo);

        // reset fields
        setNewMedicalServiceName("");
        setCheckedTasksStringArray([]);

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
                        disabled={newMedicalServiceName==="" || checkedTasksStringArray.length===0}
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
