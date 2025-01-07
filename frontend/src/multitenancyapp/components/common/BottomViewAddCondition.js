import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import ConditionMultipleSelect from './ConditionMultipleSelect';

import Button from '@mui/material/Button';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {t} from "i18next";
import {SingleUserProfileAPI} from "../../API/SingleUserProfileAPI";


export default function DoctorConditionAdditionDrawer(props) {

  const [open, setOpen] = React.useState(false);
  const [permanent, setPermanent] = React.useState(false);

  const [startDate,setStartDate] = React.useState("");
  const [endDate,setEndDate] = React.useState("");
  const [condition,setCondition] = React.useState([]);
  const [state, setState] = React.useState({bottom: false});



  const conditionsList = Object.values(props.conditions);
  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare la data.
  //Viene passata al componente <BasicDatePicker>
  const handleStartDate = (startDate) => {
    setStartDate(startDate);
  }

  const handleEndDate = (endDate) => {
    setEndDate(endDate);
  }

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare il turno.
  //Viene passata al componente <ConcreteShiftMultipleSelect>
  const handleConditon = (c) => {
    for(var i =0;i<props.currentConditionsList.length;i++){
      if(c === props.currentConditionsList[i].label){
        setCondition([c,props.currentConditionsList[i].permanent]);
        setPermanent(props.currentConditionsList[i].permanent)
        break;
      }
    }

  }

  //Funzione che apre la schermata secondaria che permette di creare un associazione.
  //Viene passata come callback al componente <Drawer>
  const toggleDrawer = (anchor, open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({ ...state, [anchor]: open });
  };



  /**
   * Utils function to show correctly formatted string (E.g. House, Lesson, ecc.. not like HOUSE or house
   * @param stringToFormat The string we want to format
   * @returns {string} The formatted string
   */
  function formatStringUpperLower(stringToFormat){
    return stringToFormat.toString().substring(0,1).toUpperCase() + stringToFormat.toString().substring(1,stringToFormat.toString().length).toLowerCase();
  }

  const handleAddCondition= async (anchor, updateFunction, doctorID) => {
    let singleUserProfileAPI = new SingleUserProfileAPI();
    let newCondition = {};
    let today = new Date();
    let startDateAsDate = new Date(startDate);

    newCondition["label"] = condition[0];
    newCondition["permanent"] = condition[1];
    /* Send start and end date to the backend only for temporary conditions */
    if(newCondition["permanent"] === false){
        newCondition["startDate"] = startDate;
        newCondition["endDate"] = endDate;
    }else{
        newCondition["startDate"] = "";
        newCondition["endDate"] = "";
    }

    if (startDate > endDate) {
      toast.error(t("End date must be after start date"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (today.getDay() > startDateAsDate.getDay() || today.getMonth() > startDateAsDate.getMonth() || today.getFullYear() > startDateAsDate.getFullYear()) {
      toast.error(t("Start date must be after at least today"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (condition.length === 0 || String(condition[0]) === "") {
      toast.error(t("Condition field must not be null"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (newCondition["permanent"] === false && (startDate === "" || endDate === "")) {
      toast.error(t("Date must be selected"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else {
      let conditionID = await singleUserProfileAPI.addCondition(doctorID, newCondition);

      newCondition["conditionID"] = conditionID;

      if(newCondition["permanent"] === false){
          const options = {
              timeZone: 'Europe/Berlin',
              weekday: 'long',
              day: "numeric",
              month: 'long',
              year: 'numeric',
          };
          var startDateToShow = String(startDate);
          startDateToShow = new Date(startDateToShow.substring(0, 3) + startDateToShow.substring(7, 11) + startDateToShow.substring(4, 7) + startDateToShow.substring(11, startDateToShow.length));
          startDateToShow = startDateToShow.toLocaleString('it-IT', options);
          var endDateToShow = String(endDate);
          endDateToShow = new Date(endDateToShow.substring(0, 3) + endDateToShow.substring(7, 11) + endDateToShow.substring(4, 7) + endDateToShow.substring(11, endDateToShow.length));
          endDateToShow = endDateToShow.toLocaleString('it-IT', options);


          startDateToShow = formatStringUpperLower(startDateToShow);
          endDateToShow = formatStringUpperLower(endDateToShow);

          let numericList = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];
          var minValue = startDateToShow.length;

          for (var i = 0; i < numericList.length; i++) {
              if (startDateToShow.indexOf(numericList[i]) !== -1 && minValue > startDateToShow.indexOf(numericList[i])) {
                  minValue = startDateToShow.indexOf(numericList[i]);
              }
          }

          var indexOfSecondSpace = startDateToShow.substring(minValue, startDateToShow.length).indexOf(' ') + minValue + 1;
          startDateToShow = startDateToShow.substring(0, indexOfSecondSpace) + startDateToShow.substring(indexOfSecondSpace, indexOfSecondSpace + 1).toUpperCase() + startDateToShow.substring(indexOfSecondSpace + 1, startDateToShow.length);

          minValue = endDateToShow.length;

          for (var i = 0; i < numericList.length; i++) {
              if (endDateToShow.indexOf(numericList[i]) !== -1 && minValue > endDateToShow.indexOf(numericList[i])) {
                  minValue = endDateToShow.indexOf(numericList[i]);
              }
          }


          indexOfSecondSpace = endDateToShow.substring(minValue, endDateToShow.length).indexOf(' ') + minValue + 1;
          endDateToShow = endDateToShow.substring(0, indexOfSecondSpace) + endDateToShow.substring(indexOfSecondSpace, indexOfSecondSpace + 1).toUpperCase() + endDateToShow.substring(indexOfSecondSpace + 1, endDateToShow.length);

          newCondition["startDate"] = startDateToShow;
          newCondition["endDate"] = endDateToShow;
      }else{
          newCondition["startDate"] = "";
          newCondition["endDate"] = "";
      }






      setOpen(false);
      conditionsList.push(newCondition);
      props.updateFunction(conditionsList);
      setState({...state, [anchor]: open});
    }

  }



  return (
    <div>
      <React.Fragment key= 'bottom'>
        <Button onClick={toggleDrawer('bottom', true)} style={{
          'display': 'block',
          'margin-left': 'auto',
          'margin-right': 'auto',
          'margin-top':'1%',
          'margin-bottom':'-1%'
        }} > <i className="fa fa-plus" aria-hidden="true"></i></Button>
        <Drawer anchor='bottom' open={state["bottom"]} onClose={toggleDrawer("bottom",false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '65vh',
          }}>
            <Stack spacing={1}>
                <label>{t('Start Date')}</label>
                <BasicDatePicker  onSelectData={handleStartDate} disabled={permanent}></BasicDatePicker>
              <label>{t('End Date')}</label>
              <BasicDatePicker  onSelectData={handleEndDate} disabled={permanent}></BasicDatePicker>
              <label>{t('User Status')}</label>
              <ConditionMultipleSelect onSelectCondition = {handleConditon} conditionsList={conditionsList} currentConditionsList={props.currentConditionsList}></ConditionMultipleSelect>
              <Button
                  variant="contained"
                  onClick={ () => handleAddCondition("bottom",props.updateFunction, props.doctorID) }
              >
                {t('Save')}
              </Button>
            </Stack>
          </div>
        </Drawer>
      </React.Fragment>
      <ToastContainer
        position="top-center"
        autoClose={5000}
        hideProgressBar={true}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </div>

  );
}
