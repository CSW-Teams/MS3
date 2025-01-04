import React, {useEffect, useState} from "react";
import {
  AppBar,
  Autocomplete,
  Box,
  Button,
  Checkbox,
  Collapse,
  Dialog,
  DialogContent,
  Fab,
  Grid,
  IconButton,
  MobileStepper,
  Radio,
  RadioGroup,
  TextField,
  Toolbar,
  Typography
} from "@mui/material";
import {t} from "i18next";
import CloseIcon from "@mui/icons-material/Close";
import CheckboxGroup from "./CheckboxGroup";
import {panic} from "./Panic";
import {KeyboardArrowLeft, KeyboardArrowRight} from "@material-ui/icons";
import {TurnoAPI} from "../../API/TurnoAPI";
import AddIcon from '@mui/icons-material/Add';
import FormControl from "@mui/material/FormControl";
import FormControlLabel from "@mui/material/FormControlLabel";
import _ from 'lodash';

const defaultServiceValues = {
  step: 0,
  medicalServiceName: "",
  selectedMansions: [],

  shiftList: []
}

const defaultNewShiftFormValues = {
  timeSlot: "",
  startTime: "08:00",
  shiftDuration: "06:00",
  seniorityValues: {},
  selectedDays: []
}

const shift = {
  "timeSlot": "",
  "startHour": 0,
  "startMinute": 0,
  "durationMinutes": 0,
  "daysOfWeek": [],
  "medicalService": {"label": ""},
  "quantityshiftseniority": []
};

const MultiStepDialog = ({
                           tasks, services, updateServicesList
                         }) => {
  /* Handle dialog state */
  const [openDialog, setOpenDialog] = useState(false);
  const handleOpenDialog = () => setOpenDialog(true);
  const handleCloseDialog = () => {
    // Reset
    setStep(defaultServiceValues.step);
    setMedicalServiceName(defaultServiceValues.medicalServiceName);
    setSelectedMansions(defaultServiceValues.selectedMansions);

    setShiftList(defaultServiceValues.shiftList);

    setTimeSlot(defaultNewShiftFormValues.timeSlot);
    setStartTime(defaultNewShiftFormValues.startTime);
    setShiftDuration(defaultNewShiftFormValues.shiftDuration);
    setSeniorityValues(defaultNewShiftFormValues.seniorityValues);
    setSelectedDays(defaultNewShiftFormValues.selectedDays);

    setOpenDialog(false);
  }

  /* Retrieve useful constants from API */
  const [seniorityNameList, setseniorityNameList] = useState([]);
  const [daysOfWeek, setDaysOfWeek] = useState([]);
  const [timeSlotList, setTimeSlotList] = useState([]);
  useEffect(() => {
    let shiftAPI = new TurnoAPI();

    shiftAPI.getShiftContraints()
      .then(response => {
        setseniorityNameList(response.seniority);
        setDaysOfWeek(response.daysOfWeek);
        setTimeSlotList(response.timeSlot);
      })
      .catch(error => {
        console.error('Error fetching seniorities:', error);
        panic()
      });
  }, []);

  /* Manage dialog navigation */
  const [step, setStep] = useState(0);
  const nextStep = () => setStep((prev) => prev + 1);
  const prevStep = () => setStep((prev) => prev - 1);

  /* Handle text field on new service creation */
  const [medicalServiceName, setMedicalServiceName] = useState("");
  // This loading by default is Lazy. To speedup application loading, { returnObjects: true } must be removed and lazy loading must be managed
  const servicesOptions = t("HospitalServices", {returnObjects: true})
  const hint = React.useRef('');
  const updateHint = (value) => {
    // Get only the options that begins with typed keys
    const matchingOption = servicesOptions.find((option) => option.toLowerCase().startsWith(value.toLowerCase()));

    // Set hint value; use "" if matchingOption is undefined or null
    hint.current = matchingOption || "";
  };

  /* Handle Collapse NewForm box */

  /* Handle dialog checkbox */
  const names = Object.values(tasks).flat();
  const [selectedMansions, setSelectedMansions] = useState([]);
  const handleCheckboxChange = (newTask) => {
    // Update selectedMansions list
    setSelectedMansions((prev) => prev.includes(newTask) ? prev.filter((item) => item !== newTask) // Removes the task if it is already selected
      : [...prev, newTask] // Adds the task if it is not selected
    );
  };

  /* Single shift composition:
  * {
  *   "timeSlot": "NIGHT",
  *   "startHour": 1,
  *   "startMinute": 0,
  *   "durationMinutes": 360,
  *   "daysOfWeek": [
  *         "WEDNESDAY",
  *         "MONDAY",
  *         "THURSDAY",
  *         "SUNDAY",
  *         "TUESDAY",
  *         "FRIDAY",
  *         "SATURDAY"
  *     ],
  *   "medicalService": {
  *     "label": "Nome_del_nuovo_Servizio_Medico"
  *   },
  *   "quantityshiftseniority": [
  *     {
  *       "task": "CLINIC",
  *       "seniority": "STRUCTURED",
  *       "quantity": 1
  *     },
  *     {
  *       "task": "CLINIC",
  *       "seniority": "SPECIALIST_JUNIOR",
  *       "quantity": 1
  *     },
  *     {
  *       "task": "CLINIC",
  *       "seniority": "SPECIALIST_SENIOR",
  *       "quantity": 1
  *     }
  *   ]
  * }
  * */
  const [shiftList, setShiftList] = useState([]);

  /* Form data input management */
  const [timeSlot, setTimeSlot] = useState("");
  const [startTime, setStartTime] = useState("08:00");
  const [shiftDuration, setShiftDuration] = useState("06:00");
  const [seniorityValues, setSeniorityValues] = useState({});
  const handleSeniorityChange = (e, seniorityName) => {
    const value = e.target.value;
    setSeniorityValues((prev) => ({
      ...prev,
      [seniorityName]: value,
    }));
  };
  const [selectedDays, setSelectedDays] = useState([]);
  const handleDayChange = (day) => {
    setSelectedDays((prevSelectedDays) => {
      if (prevSelectedDays.includes(day)) {
        return prevSelectedDays.filter((d) => d !== day);
      } else {
        return [...prevSelectedDays, day];
      }
    });
  };

  const [openCollapseShiftForm, setOpenCollapseShiftForm] = useState(false);
  const handleCollapseShiftFormToggle = () => {
    setOpenCollapseShiftForm(!openCollapseShiftForm);
  };

  // TEMP:

  const handleFormFinish = () => {
    // TODO: manage post operation

    handleCloseDialog();
  }

  const handleAddNewShift = () => {
    // TEMP: to delete
    const data = {
      timeSlot,
      startTime,
      shiftDuration,
      selectedDays,
    };

    let shiftCopy = _.cloneDeep(shift);

    shiftCopy.timeSlot = timeSlot;

    shiftCopy.daysOfWeek = selectedDays.slice();

    let [hour, minute] = startTime.split(":");
    shiftCopy.startHour = hour;
    shiftCopy.startMinute = minute;

    [hour, minute] = shiftDuration.split(":");
    shiftCopy.durationMinutes = parseInt(hour) * 60 + parseInt(minute);

    shiftCopy.medicalService.label = medicalServiceName;

    shiftCopy.quantityshiftseniority = Object.entries(seniorityValues).map(([seniority, quantity]) => ({
      task: selectedMansions[step - 1],
      seniority: seniority,
      quantity: quantity
    }));

    setShiftList(prevShiftList => [...prevShiftList, shiftCopy]);

    // Close NewShiftForms
    handleCollapseShiftFormToggle();

    // TODO; remove this console.log
    console.log("Saved data:", data);
    console.log("Saved data in shiftCopy:", shiftCopy);
  }

  return (<>
    {/* TODO: change button style */}
    <Button
      onClick={handleOpenDialog}
      style={{
        'display': 'block',
        'margin-left': 'auto',
        'margin-right': 'auto',
        'margin-top': '1%',
        'margin-bottom': '1%'
      }}
    >
      {t('Create new Service') + " (Rali Edit)"}
    </Button>

    <Dialog
      open={openDialog}
      // onClose={() => setOpen(false)} // TODO! Implement this logic? Or leave dialog on screen until end?
      fullWidth
      maxWidth="md"
      scroll={"paper"}
    >
      <AppBar position="static" color="transparent">
        <Toolbar>
          <Box
            sx={{display: 'flex', flexGrow: 1, justifyContent: 'center'}}>
            <Typography variant="h5" component="div"
                        sx={{marginLeft: '20px'}}>
              {step === 0 ? t('Create new Service') : selectedMansions[step - 1]}
            </Typography>
          </Box>
          <IconButton color="inherit" onClick={handleCloseDialog}>
            <CloseIcon/>
          </IconButton>
        </Toolbar>
      </AppBar>

      <DialogContent style={{position: 'relative', padding: '20px'}}>
        {step === 0 && (<div style={{
          display: 'flex',
          alignItems: 'center',
          flexDirection: "column",
          justifyContent: "center",
          marginTop: "10px",
        }}>
          <Autocomplete
            inputValue={medicalServiceName}
            options={servicesOptions}   // Suggested options in the panel
            sx={{minWidth: 250, maxWidth: 450, width: 'auto'}}
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
                    pointerEvents: 'none',
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
            selectedOptions={selectedMansions}
            onChange={handleCheckboxChange}
            disabled={false}
          />
        </div>)}

        {step > 0 && (<div style={{
          display: 'flex',
          alignItems: 'center',
          flexDirection: "column",
          justifyContent: "center",
          padding: '10px',
        }}>
          <Typography variant="h6">
            Add a new shift for {selectedMansions[step - 1]}
          </Typography>

          <Collapse in={openCollapseShiftForm}>
          <Box
            sx={{
              padding: "20px",
              backgroundColor: "rgba(237,237,237,0.5)",
              borderRadius: "10px",
              maxWidth: "500px",
              margin: "0 auto",
            }}
          >
            <Box display="flex" alignItems="center" sx={{gap: 2, marginBottom: '20px'}}>
              <Typography variant="h7" gutterBottom style={{marginBottom: 0}}>
                Time Slot:
              </Typography>
              <FormControl component="fieldset">
                <RadioGroup
                  row
                  value={timeSlot}
                  onChange={(e) => setTimeSlot(e.target.value)}
                >
                  {timeSlotList.map((slot, _) => (
                    <FormControlLabel
                      key={slot}
                      value={slot}
                      control={<Radio />}
                      label={slot}
                    />
                  ))}
                </RadioGroup>
              </FormControl>
            </Box>

            <Box display="flex" alignItems="center"
                 sx={{marginTop: "20px", gap: 2}}>
              <Typography variant="h7" gutterBottom>
                Shift start time:
              </Typography>
              <TextField
                type="time"
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
              />
            </Box>

            <Box display="flex" alignItems="center" sx={{ marginTop: "10px", gap: 2}}>
              <Typography variant="h7" gutterBottom>
                Shift duration:
              </Typography>
              <TextField
                type="time"
                value={shiftDuration}
                onChange={(e) => setShiftDuration(e.target.value)}
              />
            </Box>

            <Box>
              {seniorityNameList.map((seniorityName) => (
                <Box key={seniorityName} display="flex" alignItems="center" sx={{ marginTop: "10px", gap: 2 }}>
                  <Typography variant="h7" gutterBottom style={{ marginBottom: 0 }}>
                    {seniorityName}:
                  </Typography>
                  <TextField
                    type="number"
                    value={seniorityValues[seniorityName] || 0} // Imposta il valore associato, default a 0
                    onChange={(e) => handleSeniorityChange(e, seniorityName)} // Gestione del cambio di valore
                    inputProps={{ min: 0 }}
                    sx={{
                      width: 100, // Imposta una larghezza fissa
                    }}
                  />
                </Box>
              ))}
            </Box>

            <Typography variant="h7" gutterBottom sx={{marginTop: "25px"}}>
              Days of the week:
            </Typography>
            <Grid container spacing={1}>
              {daysOfWeek.map((day) => (
                <Grid item xs={6} key={day}>
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={selectedDays.includes(day)}
                        onChange={() => handleDayChange(day)}
                      />
                    }
                    label={day}
                  />
                </Grid>
              ))}
            </Grid>

            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={handleAddNewShift}
              sx={{marginTop: "20px"}}
            >
              Add shift
            </Button>
          </Box>
          </Collapse>

          <Fab
            color="primary"
            aria-label="add"
            onClick={() => handleCollapseShiftFormToggle()}
          >
            <AddIcon/>
          </Fab>

        </div>)}
      </DialogContent>

      <MobileStepper
        variant="dots"
        steps={selectedMansions.length + 1}
        position="static"
        activeStep={step}
        sx={{width: '100%'}}
        nextButton={(step === selectedMansions.length && selectedMansions.length !== 0) ? (
          <Button size="small" onClick={handleFormFinish}>
            Finish
          </Button>) : (<Button size="small" onClick={nextStep}
                                disabled={step === selectedMansions.length || medicalServiceName === ""}>
          Next <KeyboardArrowRight/>
        </Button>)}
        backButton={<Button
          size="small"
          onClick={prevStep}
          disabled={step === 0}
          sx={{visibility: step === 0 ? 'hidden' : 'visible'}}
        >
          <KeyboardArrowLeft/> Back
        </Button>}
      />
    </Dialog>
  </>);
};

export default MultiStepDialog;
