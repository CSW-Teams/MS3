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
import CloseIcon from "@mui/icons-material/Close";
import {KeyboardArrowLeft, KeyboardArrowRight} from "@material-ui/icons";
import {t} from "i18next";
import CheckboxGroup from "./CheckboxGroup";
import {toast} from 'react-toastify';
import {panic} from "./Panic";
import {TurnoAPI} from "../../API/TurnoAPI";
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import NewShiftForm from "./NewShiftForm";
import ShiftItemBox from "./ShiftItemBox";
import {ServiceAPI} from "../../API/ServiceAPI";
import {MedicalService} from "../../entity/MedicalService";
import {Task} from "../../entity/Task";

const defaultServiceValues = {
  step: 0,
  medicalServiceName: "",
  selectedMansions: [],

  shiftList: []
}

const showToast = (message, type = 'success') => {
  toast[type](message, {
    position: "top-center",
    autoClose: 5000,
    hideProgressBar: true,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
    theme: "colored",
  });
};

const DialogCreateNewService = ({
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
  const [seniorityNameList, setSeniorityNameList] = useState([]);
  const [daysOfWeek, setDaysOfWeek] = useState([]);
  const [timeSlotList, setTimeSlotList] = useState([]);
  useEffect(() => {
    let shiftAPI = new TurnoAPI();

    shiftAPI.getShiftContraints()
      .then(response => {
        setSeniorityNameList(response.seniority);
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
  const nextStep = () => {
    setStep((prev) => {
      if (prev !== 0 && openCollapseShiftForm) handleCollapseShiftFormToggle();

      return prev + 1;
    });
  }
  const prevStep = () => {
    setStep((prev) => {
      if (prev !== 0 && openCollapseShiftForm) handleCollapseShiftFormToggle();

      return prev - 1;
    });
  }

  /* Handle text field on new service creation */
  const [medicalServiceName, setMedicalServiceName] = useState("");
  // This loading by default is Lazy. To speedup application loading, { returnObjects: true } must be removed and lazy loading must be managed
  let serviceOptions = t("HospitalServices", {returnObjects: true})
  // Remove from serviceOptions all services that already exist
  serviceOptions = (serviceOptions || []).filter(option => option && !services.map(service => service.name).includes(option.toUpperCase()));
  const hint = React.useRef('');
  const updateHint = (value) => {
    // Get only the options that begins with typed keys
    const matchingOption = serviceOptions.find((option) => option.toLowerCase().startsWith(value.toLowerCase()));

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
  *   "id": Date.now()
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
  *       "taskName": "CLINIC",
  *       "seniority": "STRUCTURED",
  *       "quantity": 1
  *     },
  *     {
  *       "taskName": "CLINIC",
  *       "seniority": "SPECIALIST_JUNIOR",
  *       "quantity": 1
  *     },
  *     {
  *       "taskName": "CLINIC",
  *       "seniority": "SPECIALIST_SENIOR",
  *       "quantity": 1
  *     }
  *   ],
  *   "additionalConstraints": []
  * }
  * */
  const [shiftList, setShiftList] = useState([]);
  const filteredShiftList = shiftList.filter(shift => shift.quantityShiftSeniority.some(item => item.taskName === selectedMansions[step - 1]));

  /* Handle delete of a shift into dialog */
  const handleDeleteShift = (shiftToDelete: number) => {
    const updatedShiftList = shiftList.filter(shift => {
      return !(shift.id === shiftToDelete)
    });

    setShiftList(updatedShiftList);
  };

  /* Handle Collapse NewForm box */
  const [openCollapseShiftForm, setOpenCollapseShiftForm] = useState(false);
  const handleCollapseShiftFormToggle = () => {
    setOpenCollapseShiftForm(!openCollapseShiftForm);
  };

  /* Handle add shift for a mansion */
  const handleSubmitShiftForm = (newShift) => {
    const {
      timeSlot,
      startHour,
      startMinute,
      durationMinutes,
      daysOfWeek: newShiftDaysOfWeek
    } = newShift;

    // Function for converting the day and time into a minute index
    function getMinuteIndex(day, hour, minute) {
      const dayIndex = daysOfWeek.indexOf(day); // Trova il giorno della settimana
      return (dayIndex * 24 * 60) + (+hour * 60) + +minute; // Restituisce l'indice minuto della settimana
    }

    let weekArray = Array(24 * 7 * 60).fill(0);
    newShiftDaysOfWeek.forEach((day) => {
      const arrayStart = getMinuteIndex(day, startHour, startMinute);
      const arrayEnd = arrayStart + durationMinutes;

      // Controlliamo se l'indice finale è all'interno del limite
      for (let i = arrayStart; i < arrayEnd; i++) weekArray[i % (7 * 24 * 60)] = 1; // Segna il periodo come occupato
    });

    // Verification function for time overlap and timeSlot conflicts
    const hasConflict = filteredShiftList.some(shift => {
      // Controlla se ci sono giorni in comune tra il nuovo turno e il turno esistente
      const commonDays = shift.daysOfWeek.filter(day => newShiftDaysOfWeek.includes(day));
      if (commonDays.length > 0 && shift.timeSlot === timeSlot) {
        console.error(`Conflitto di timeSlot (${timeSlot}) nei giorni: ${commonDays.join(", ")}`);
        return true; // Stesso timeSlot nei giorni in comune: conflitto
      }

      let retValue = false;

      shift.daysOfWeek.forEach(day => {
        const arrayStart = getMinuteIndex(day, shift.startHour, shift.startMinute);
        const arrayEnd = arrayStart + shift.durationMinutes;

        // Controlla se ci sono turni che si sovrappongono
        for (let i = arrayStart; i < arrayEnd; i++) {
          if (weekArray[i % (7 * 24 * 60)] === 1) {
            console.error("Conflitto di orari con il turno esistente.");

            retValue = true;
            break;
          }
        }
      })

      return retValue;
    });

    // If there is a conflict, warn the user and abort
    if (hasConflict) {
      showToast(
        "Il nuovo turno si sovrappone o utilizza lo stesso timeSlot di un turno esistente. Modifica i dettagli e riprova.",
        "error"
      );
    } else {
      // If there are no conflicts, add the turn to the list
      setShiftList(prevList => [...prevList, newShift]);
      showToast("Turno aggiunto con successo!");
    }

    return hasConflict;
  };

  /* Handle upload of the new service */
  const handleServiceCreationFinish = async () => {
    //check if exists
    const servicesNames = services.map(services => services.name)
    const matches = servicesNames.filter(service => service.toUpperCase() === (medicalServiceName.toUpperCase()))
    if (matches.length !== 0) {
      toast.error(t("Service already exists"));

      return;
    }

    const shiftListNoId = shiftList.map(({id, ...rest}) => rest)

    let requestParam = {
      name: medicalServiceName,
      taskTypes: selectedMansions,
      shifts: shiftListNoId
    };

    const serviceAPI = new ServiceAPI();
    // serviceAPI.alphaSort(selectedMansions)

    try {
      let response = serviceAPI.createMedicalService(requestParam).then(response => {
        console.log(response);

        return response;
      });

      if (response.ok) return;
    } catch (err) {

      panic()
      return
    }

    // build params for view update
    const outTaskArray = [];
    for (let i = 0; i < selectedMansions.length; i++) {
      outTaskArray.push(new Task(null, selectedMansions[i], false));
    }

    // build service infos for view update
    let viewUpdateServiceInfo = new MedicalService(null, medicalServiceName.toUpperCase(), outTaskArray);
    updateServicesList(viewUpdateServiceInfo);

    toast.success(t('Service Created Successfully'));

    handleCloseDialog();
  }

  return (<>
    <Button
      variant="contained"
      onClick={handleOpenDialog}
      style={{
        'display': 'block',
        'margin-left': 'auto',
        'margin-right': 'auto',
        'margin-top': '1%',
        'margin-bottom': '2%'
      }}
    >
      {t('Create new Service')}
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
              {step === 0 ? t('Create new Service') : t(selectedMansions[step - 1])}
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
            options={serviceOptions}
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
          <Typography variant="h6"
                      visibility={filteredShiftList.length === 0 ? 'visible' : 'hidden'}
                      sx={{ marginBottom: '12px' }}
          >
            {t("Add a new shift for the task")} {t(selectedMansions[step - 1])}
          </Typography>

          {filteredShiftList.map((shift, index) => (
            <ShiftItemBox key={index} shiftData={shift}
                          onDelete={handleDeleteShift}/>
          ))}

          <NewShiftForm
            openCollapseShiftForm={openCollapseShiftForm}
            handleCollapseShiftFormToggle={handleCollapseShiftFormToggle}
            handleSubmitShiftForm={handleSubmitShiftForm}
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
            {openCollapseShiftForm ? <RemoveIcon/> : <AddIcon/>}
          </Fab>

        </div>)}
      </DialogContent>

      <MobileStepper
        variant="dots"
        steps={selectedMansions.length + 1}
        position="static"
        activeStep={step}
        sx={{width: '100%'}}
        nextButton={
          (step === selectedMansions.length && selectedMansions.length !== 0) ?
            (
              <Button
                variant="contained"
                size="small"
                disabled={filteredShiftList.length === 0}
                onClick={handleServiceCreationFinish}
              >
                {t("Create")}
              </Button>
            ) : (
              <Button
                size="small"
                disabled={
                  step === selectedMansions.length ||
                  medicalServiceName === "" ||
                  (filteredShiftList.length === 0 && step !== 0)
                }
                onClick={nextStep}
              >
                {t("Next")} <KeyboardArrowRight/>
              </Button>
            )
        }
        backButton={
          <Button
            size="small"
            sx={{visibility: step === 0 ? 'hidden' : 'visible'}}
            disabled={step === 0}
            onClick={prevStep}
          >
            <KeyboardArrowLeft/> {t("Back")}
          </Button>
        }
      />
    </Dialog>
  </>);
};

export default DialogCreateNewService;
