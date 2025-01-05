import {
  Box,
  Button,
  Checkbox,
  Collapse,
  Grid,
  Radio,
  RadioGroup,
  TextField,
  Typography
} from "@mui/material";
import FormControl from "@mui/material/FormControl";
import FormControlLabel from "@mui/material/FormControlLabel";
import React, {useState} from "react";

const defaultNewShiftFormValues = {
  timeSlot: "",
  startTime: "08:00",
  shiftDuration: "06:00",
  selectedDays: []
}

const NewShiftForm = ({
                        openCollapseShiftForm,
                        handleCollapseShiftFormToggle,
                        handleSubmitShiftForm,
                        medicalServiceName,
                        task,
                        timeSlotList,
                        seniorityNameList,
                        daysOfWeek
                      }) => {
  /* Form data input management */
  const [timeSlot, setTimeSlot] = useState(defaultNewShiftFormValues.timeSlot);
  const [startTime, setStartTime] = useState(defaultNewShiftFormValues.startTime);
  const [shiftDuration, setShiftDuration] = useState(defaultNewShiftFormValues.shiftDuration);
  const [seniorityValues, setSeniorityValues] = useState(
    seniorityNameList.reduce((acc, seniorityName) => {
      acc[seniorityName] = 1;
      return acc;
    }, {}));
  const handleSeniorityChange = (e, seniorityName) => {
    const value = e.target.value;
    setSeniorityValues((prev) => ({
      ...prev,
      [seniorityName]: value,
    }));
  };
  const [selectedDays, setSelectedDays] = useState(defaultNewShiftFormValues.selectedDays);
  const handleDayChange = (day) => {
    setSelectedDays((prevSelectedDays) => {
      if (prevSelectedDays.includes(day)) {
        return prevSelectedDays.filter((d) => d !== day);
      } else {
        return [...prevSelectedDays, day];
      }
    });
  };

  const handleAddNewShift = () => {
    let shiftCopy = {};

    shiftCopy.timeSlot = timeSlot;

    shiftCopy.daysOfWeek = selectedDays.slice();

    let [hour, minute] = startTime.split(":");
    shiftCopy.startHour = hour;
    shiftCopy.startMinute = minute;

    [hour, minute] = shiftDuration.split(":");
    shiftCopy.durationMinutes = parseInt(hour) * 60 + parseInt(minute);

    shiftCopy.medicalService = {};
    shiftCopy.medicalService.label = medicalServiceName;

    shiftCopy.quantityshiftseniority = Object.entries(seniorityValues).map(([seniority, quantity]) => ({
      task: task,
      seniority: seniority,
      quantity: quantity
    }));

    // Close NewShiftForms
    handleCollapseShiftFormToggle();

    handleSubmitShiftForm(shiftCopy);
  }

  const handleResetShiftForm = () => {
    setTimeSlot(defaultNewShiftFormValues.timeSlot);
    setStartTime(defaultNewShiftFormValues.startTime);
    setShiftDuration(defaultNewShiftFormValues.shiftDuration);
    setSeniorityValues(seniorityNameList.reduce((acc, seniorityName) => {
      acc[seniorityName] = 1;
      return acc;
    }, {}));
    setSelectedDays(defaultNewShiftFormValues.selectedDays);

    handleCollapseShiftFormToggle();
  }

  return (
    <Collapse in={openCollapseShiftForm}>
      <Box
        sx={{
          border: '1px solid #ccc',
          borderRadius: 4,
          padding: 3,
          width: '95%',
          backgroundColor: '#f9f9f9',
          margin: '0 auto',
          position: 'relative',
          marginBottom: 2,
        }}
      >
        {/* Title for the Shift Creation Form */}
        <Typography variant="h5" gutterBottom sx={{ marginBottom: '20px', textAlign: 'center' }}>
          Create New Shift
        </Typography>

        {/* Time Slot Section */}
        <Box sx={{ marginBottom: '20px' }}>
          <Grid container alignItems="center" spacing={2}>
            <Grid item>
              <Typography variant="h7" gutterBottom sx={{ marginBottom: 0 }}>
                Time Slot:
              </Typography>
            </Grid>
            <Grid item>
              <FormControl component="fieldset">
                <RadioGroup
                  row
                  value={timeSlot}
                  onChange={(e) => setTimeSlot(e.target.value)}
                >
                  {timeSlotList.map((slot) => (
                    <FormControlLabel
                      key={slot}
                      value={slot}
                      control={<Radio />}
                      label={slot}
                    />
                  ))}
                </RadioGroup>
              </FormControl>
            </Grid>
          </Grid>
        </Box>

        {/* Shift Start Time and Duration Section */}
        <Grid container spacing={3} sx={{ marginBottom: '20px' }}>
          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="h7" gutterBottom>
              Shift start time:
            </Typography>
            <TextField
              type="time"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              fullWidth
              sx={{ maxWidth: 330 }}  // Aumentato il maxWidth
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="h7" gutterBottom>
              Shift duration:
            </Typography>
            <TextField
              type="time"
              value={shiftDuration}
              onChange={(e) => setShiftDuration(e.target.value)}
              fullWidth
              sx={{ maxWidth: 250 }}  // Aumentato il maxWidth
            />
          </Grid>
        </Grid>

        {/* Seniority Input Fields */}
        <Box sx={{ marginBottom: '20px' }}>
          <Grid container spacing={2}>
            {seniorityNameList.map((seniorityName) => (
              <Grid item xs={12} sm={4} key={seniorityName}>
                <Grid container alignItems="center" spacing={1}>
                  <Grid item>
                    <Typography variant="h7" sx={{ marginBottom: 0 }}>
                      {seniorityName}:
                    </Typography>
                  </Grid>
                  <Grid item xs>
                    <TextField
                      type="number"
                      value={seniorityValues[seniorityName] || 1}
                      onChange={(e) => handleSeniorityChange(e, seniorityName)}
                      inputProps={{ min: 1 }}
                      sx={{ width: 75 }} // Allineato a destra
                    />
                  </Grid>
                </Grid>
              </Grid>
            ))}
          </Grid>
        </Box>

        {/* Days of the Week Section */}
        <Box sx={{ marginBottom: '20px' }}>
          <Typography variant="h7" gutterBottom>
            Days of the week:
          </Typography>
          <Grid container spacing={2}>
            {daysOfWeek.map((day) => (
              <Grid item xs={6} sm={3} key={day}>  {/* 2 colonne su schermi piccoli, 4 su schermi più grandi */}
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
        </Box>

        {/* Buttons Section */}
        <Grid container spacing={2} sx={{ marginTop: '20px' }}>
          <Grid item xs={6}>
            <Button
              variant="outlined"
              color="secondary"
              fullWidth
              onClick={handleResetShiftForm}
            >
              Cancel
            </Button>
          </Grid>
          <Grid item xs={6}>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={handleAddNewShift}
            >
              Add shift
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Collapse>
  );
}

export default NewShiftForm;
