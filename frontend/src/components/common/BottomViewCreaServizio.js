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
import {MedicalService} from "../../entity/MedicalService";
import {Task} from "../../entity/Task";
import { t } from "i18next";

toast.configure();

const MedicalServiceCreationDrawer = ({tasks, services, updateServicesList}) => {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const [checkedTasksStringArray, setCheckedTasksStringArray] = useState([]);
    const [newMedicalServiceName, setNewMedicalServiceName] = useState("");

    const serviceAPI = new ServizioAPI();
    const names = Object.values(tasks);

    const handleCheckboxChange = (newTask) => {
        if (!checkedTasksStringArray.includes(newTask)) {
            setCheckedTasksStringArray(prev => [...prev, newTask]);
        } else {
            setCheckedTasksStringArray(checkedTasksStringArray.filter(item => item !== newTask));
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
            alphaSort(checkedTasksStringArray);

            // API request params built differently (e.g. not as a MedicalService object)
            // for compliance wrt other modules
            var requestParams = {
                name        : newMedicalServiceName.toUpperCase(),
                taskTypes   : checkedTasksStringArray
            }

            try {
              serviceAPI.createMedicalService(requestParams);
            } catch (err) {

              toast(t('Connection Error, please try again later'), {
                position: 'top-center',
                autoClose: 1500,
                style : {background : "red", color : "white"}
              })
              return
            }

            // build params for view update
            const outTaskArray = [];
            for (let i = 0; i < checkedTasksStringArray.length; i++) {
                outTaskArray.push(new Task(null, checkedTasksStringArray[i], false));
            }

            // build service infos for view update
            var viewUpdateServiceInfo = new MedicalService (
                null,
                newMedicalServiceName.toUpperCase(),
                outTaskArray
                );
            updateServicesList(viewUpdateServiceInfo);

            // reset fields
            setNewMedicalServiceName("");
            setCheckedTasksStringArray([]);

            toast.success(t('Service Created Successfully'));
            } else {
            toast.error(t("Service already exists"));
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
          {t('Create new Service')}
        </Button>
        <Drawer anchor="bottom" open={open} onClose={handleClose}>
            <AppBar position="static" color="transparent">
                <Toolbar>
                    <Box sx={{ display: 'flex', flexGrow: 1, justifyContent: 'center' }}>
                        <Typography variant="h5" component="div" sx={{ marginLeft: '20px' }}>
                          {t('Create new Service')}
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
                        autoFocus = "true"
                        label     ={t('Service Name')}
                        required  = "true"
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
                      {t("Select tasks:")}
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
                        disabled={newMedicalServiceName==="" || checkedTasksStringArray.length===0}
                        onClick={postNewRequest}
                        >
                      {t('Save')}
                    </Button>
                </Box>
            </div>
        </Drawer>
        </>
    );
};
export default MedicalServiceCreationDrawer;
