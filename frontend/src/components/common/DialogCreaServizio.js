import React, {useEffect, useState} from "react";
import {
  AppBar,
  Autocomplete,
  Box,
  Button,
  Dialog,
  DialogContent,
  Fab,
  IconButton,
  MobileStepper,
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
import RemoveIcon from '@mui/icons-material/Remove';
import NewShiftForm from "./NewShiftForm";
import ShiftItemBox from "./ShiftItemBox";

const defaultServiceValues = {
  step: 0,
  medicalServiceName: "",
  selectedMansions: [],

  shiftList: []
}

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
  const filteredShiftList = shiftList.filter(shift =>
    shift.quantityshiftseniority.some(item => item.task === selectedMansions[step - 1])
  );

  const handleDeleteShift = (shiftToDelete: {
    timeSlot: string;
    startHour: number;
    startMinute: number;
    durationMinutes: number;
    daysOfWeek: string[];
    medicalService: { label: string };
    quantityshiftseniority: {
      task: string;
      seniority: string;
      quantity: number;
    }[];
  }) => {
    const updatedShiftList = shiftList.filter(shift => {
      const quantityShiftsMatch = shift.quantityshiftseniority.length === shiftToDelete.quantityshiftseniority.length &&
        shift.quantityshiftseniority.every((shiftItem, index) =>
          shiftItem.task === shiftToDelete.quantityshiftseniority[index].task &&
          shiftItem.seniority === shiftToDelete.quantityshiftseniority[index].seniority &&
          shiftItem.quantity === shiftToDelete.quantityshiftseniority[index].quantity
        );

      return !(shift.timeSlot === shiftToDelete.timeSlot &&
        shift.startHour === shiftToDelete.startHour &&
        shift.startMinute === shiftToDelete.startMinute &&
        shift.durationMinutes === shiftToDelete.durationMinutes &&
        shift.daysOfWeek.length === shiftToDelete.daysOfWeek.length &&
        quantityShiftsMatch
      );
    });

    setShiftList(updatedShiftList);
  };

  /* Handle Collapse NewForm box */
  const [openCollapseShiftForm, setOpenCollapseShiftForm] = useState(false);
  const handleCollapseShiftFormToggle = () => {
    setOpenCollapseShiftForm(!openCollapseShiftForm);
  };

  // TEMP
  const handleFormFinish = () => {
    // TODO: manage post operation

    console.log(shiftList)

    handleCloseDialog();
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
            options={servicesOptions}
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
                    width: 'calc(100% - 75px)',
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

          {filteredShiftList.map((shift, index) => (
            <ShiftItemBox key={index} shiftData={shift} onDelete={handleDeleteShift}/>
          ))}

          <NewShiftForm
            openCollapseShiftForm={openCollapseShiftForm}
            handleCollapseShiftFormToggle={handleCollapseShiftFormToggle}
            handleSubmitShiftForm={(newShift) => {setShiftList((prev) => [...prev, newShift])}}
            medicalServiceName={medicalServiceName}
            task={selectedMansions[step - 1]}
            timeSlotList={timeSlotList}
            seniorityNameList={seniorityNameList}
            daysOfWeek={daysOfWeek}
          />

          <Fab
            color="primary"
            aria-label="add"
            onClick={() => handleCollapseShiftFormToggle()}
          >
            {openCollapseShiftForm ? <RemoveIcon /> : <AddIcon />}
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
