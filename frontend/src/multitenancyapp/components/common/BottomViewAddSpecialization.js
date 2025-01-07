import React, {useState} from "react";
import {
  AppBar,
  Autocomplete,
  Box,
  Button,
  Drawer,
  IconButton,
  TextField,
  Toolbar,
  Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {t} from "i18next";
import {SingleUserProfileAPI} from "../../API/SingleUserProfileAPI";
import {panic} from "./Panic";

export default function DoctorSpecializationAdditionDrawer(props) {
  const [open, setOpen] = React.useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  // List of specializations already assigned to the user
  const currentSpecializationList = props.updatedSpecializationList;

  // List of all available specializations
  const fullSpecializationList = Object.values(props.specializations)

  const [addedSpecializations, setAddedSpecializations] = useState([]);
  const handleAddSpecializations = async (updateFunction, doctorID, specializations, checkedSpecializationsCheckBoxes) => {
    let singleUserProfileAPI = new SingleUserProfileAPI();

    const updatedSpecializations = [...specializations, ...checkedSpecializationsCheckBoxes];

    try {
      await singleUserProfileAPI.addSpecializations(doctorID, updatedSpecializations);
      setOpen(false);
      updateFunction(updatedSpecializations);
      setAddedSpecializations([]);
    } catch (err) {
      console.error("Errore:", err);
      panic();
    }
  }

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
      <i className="fa fa-plus" aria-hidden="true"></i>
    </Button>

    <Drawer anchor="bottom" open={open} onClose={handleClose}>
      <AppBar position="static" color="transparent">
        <Toolbar>
          <Box sx={{display: 'flex', flexGrow: 1, justifyContent: 'center'}}>
            <Typography variant="h5" component="div"
                        sx={{marginLeft: '20px'}}>
              {t('Add Specialization')}
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
        <Typography variant="h6" sx={{mb: 1}}>
          {t('Select specializations to assign:')}
        </Typography>

        <Autocomplete
          multiple
          options={fullSpecializationList}
          getOptionDisabled={(option) => currentSpecializationList.includes(option)}
          filterSelectedOptions
          sx={{mb: 6, width: 400}}
          onChange={(event, newValue) => setAddedSpecializations(newValue)}
          renderInput={(params) => (
            <TextField
              {...params}
              label={t("Specializations")}
              placeholder={t("Add new specialization")}
            />
          )}
        />

        <Button
          variant="contained"
          sx={{mb: 3}}
          disabled={addedSpecializations.length === 0}
          onClick={() => handleAddSpecializations(props.updateFunction, props.doctorID, currentSpecializationList, addedSpecializations)}
        >
          {t('Save')}
        </Button>
      </div>
    </Drawer>
  </>);

}
