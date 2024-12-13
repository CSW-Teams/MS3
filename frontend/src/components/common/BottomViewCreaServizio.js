import * as React from 'react';
import {useState} from 'react';
import {
  AppBar,
  Autocomplete,
  Box,
  Button,
  Drawer,
  IconButton,
  Toolbar,
  Typography
} from '@mui/material'
import {toast} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import CloseIcon from "@mui/icons-material/Close";
import {ServizioAPI} from "../../API/ServizioAPI";
import {MedicalService} from "../../entity/MedicalService";
import {Task} from "../../entity/Task";
import {t} from "i18next";
import {panic} from "./Panic";
import CheckboxGroup from "./CheckboxGroup";
import TextField from "@mui/material/TextField";

toast.configure();

const MedicalServiceCreationDrawer = ({
                                        tasks,
                                        services,
                                        updateServicesList
                                      }) => {
  /* Handle toast state */
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  /* Handle checkbox */
  const serviceAPI = new ServizioAPI();
  const names = Object.values(tasks).flat();
  const [checkedTasksStringArray, setCheckedTasksStringArray] = useState([]);
  const handleCheckboxChange = (newTask) => {
    if (!checkedTasksStringArray.includes(newTask))
      setCheckedTasksStringArray(prev => [...prev, newTask]);
    else
      setCheckedTasksStringArray(checkedTasksStringArray.filter(item => item !== newTask));
  };

  /* Handle text field on new service creation */
  const [medicalServiceName, setMedicalServiceName] = useState("");
  // This loading by default is Lazy. To speedup application loading, { returnObjects: true } must be removed and lazy loading must be managed
  const servicesOptions = t("HospitalServices", { returnObjects: true })
  const hint = React.useRef('');
  const updateHint = (value) => {
    // Get only the options that begins with typed keys

    const matchingOption = servicesOptions.find((option) => option.toLowerCase().startsWith(value.toLowerCase()));
    // Set hint value; use "" if matchingOption is undefined or null
    hint.current = matchingOption || "";

  };

  function alphaSort(array) {
    return array.sort((a, b) => a.localeCompare(b));
  }

  const postNewRequest = () => {
    //check if exists
    const servicesNames = services.map(services => services.name)
    const matches = servicesNames.filter(service => service.toUpperCase() === (medicalServiceName.toUpperCase()))
    if (matches.length === 0) {
      handleClose();
      alphaSort(checkedTasksStringArray);

      // API request params built differently (e.g. not as a MedicalService object)
      // for compliance wrt other modules
      var requestParams = {
        name: medicalServiceName.toUpperCase(),
        taskTypes: checkedTasksStringArray
      }

      try {
        serviceAPI.createMedicalService(requestParams);
      } catch (err) {

        panic()
        return
      }

      // build params for view update
      const outTaskArray = [];
      for (let i = 0; i < checkedTasksStringArray.length; i++) {
        outTaskArray.push(new Task(null, checkedTasksStringArray[i], false));
      }

      // build service infos for view update
      var viewUpdateServiceInfo = new MedicalService(null, medicalServiceName.toUpperCase(), outTaskArray);
      updateServicesList(viewUpdateServiceInfo);

      // reset fields
      setMedicalServiceName("");
      setCheckedTasksStringArray([]);

      toast.success(t('Service Created Successfully'));
    } else {
      toast.error(t("Service already exists"));
    }
  };

  return (<>
    <Button
      onClick={handleOpen}
      style={{
        'display': 'block',
        'margin-left': 'auto',
        'margin-right': 'auto',
        'margin-top': '1%',
        'margin-bottom': '1%'
      }}
    >
      {t('Create new Service')}
    </Button>

    <Drawer anchor="bottom" open={open} onClose={handleClose}>
      <AppBar position="static" color="transparent">
        <Toolbar>
          <Box sx={{display: 'flex', flexGrow: 1, justifyContent: 'center'}}>
            <Typography variant="h5" component="div"
                        sx={{marginLeft: '20px'}}>
              {t('Create new Service')}
            </Typography>
          </Box>
          <IconButton color="inherit" onClick={handleClose}>
            <CloseIcon/>
          </IconButton>
        </Toolbar>
      </AppBar>

      <div style={{
        display: 'flex',
        alignItems: 'center',
        flexDirection: "column",
        justifyContent: "center",
        padding: '20px',
        marginTop: "10px",
        marginBottom: "10px"
      }}>
        <Autocomplete
          inputValue={medicalServiceName}
          options={servicesOptions}   // Suggested options in the panel
          sx={{minWidth: 250, maxWidth: 400, width: 'auto'}}
          onChange={(event, newValue) => {
            setMedicalServiceName(newValue ? newValue : '');
          }}
          onClose={() => {
            hint.current = '';
          }}
          onKeyDown={(event) => {
            if (event.key === 'Tab' && hint.current) {
              if (hint.current) {
                setMedicalServiceName(hint.current);
                event.preventDefault();
              }
            }
          }}
          renderInput={(params) => {
            return (<Box sx={{position: 'relative', textAlign: 'left'}}>
              {/* Show hint */}
              <Typography
                sx={{
                  position: 'absolute',
                  opacity: 0.5,
                  left: 14,
                  top: 16,
                  overflow: 'hidden',
                  whiteSpace: 'nowrap',
                  width: 'calc(100% - 75px)', // Adjust based on padding of TextField
                }}
              >
                {hint.current}
              </Typography>
              <TextField
                {...params}
                label={t('Service Name')}
                onChange={(event) => {
                  let input = event.target.value;
                  if (input) input = input.charAt(0).toUpperCase() + input.slice(1).toLowerCase();
                  setMedicalServiceName(input);
                  updateHint(input);
                }}
              />
            </Box>)
          }}
        />

        {/* Text "Select task:" */}
        <Typography variant="h6" sx={{marginTop: '24px'}}>
          {t("Select tasks:")}
        </Typography>

        <CheckboxGroup
          options={names}
          onChange={handleCheckboxChange}
          disabled={false}
        />

        {/* Save button */}
        <Button
          color="success"
          variant="contained"
          sx={{mt: 4}}
          onClick={postNewRequest}
          disabled={medicalServiceName === "" || checkedTasksStringArray.length === 0}
        >
          {t('Save')}
        </Button>
      </div>
    </Drawer>
  </>);
};

export default MedicalServiceCreationDrawer;
