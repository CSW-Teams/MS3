import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBCol, MDBContainer,
  MDBRow
} from "mdb-react-ui-kit";
import React, {useState} from "react";
import {Calendar} from "react-multi-date-picker";
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import {Button} from "@mui/material";
import {toast, ToastContainer} from "react-toastify";
import {HolidaysAPI} from "../../API/HolidaysAPI";

const monthsToNum = {
  "January" : 0,
  "February" : 1,
  "March" : 2,
  "April" : 3,
  "May" : 4,
  "June" : 5,
  "July" : 6,
  "August" : 7,
  "September" : 8,
  "October" : 9,
  "November" : 10,
  "December" : 11,
} ;

function checkDataIsCorrect(holiday) {

  if(holiday.recurrent) {
    if(monthsToNum[holiday.endMonth] < monthsToNum[holiday.startMonth]) {
      toast("La festività deve cominciare prim della sua fine!", {
        position : "top-center",
        autoClose: 1500,
        style : {background : "red", color : "white"}
      }) ;
      return false ;
    }
    if(monthsToNum[holiday.endMonth] === monthsToNum[holiday.startMonth]) {
      if(holiday.startDay > holiday.endDay) {
        toast("La festività deve cominciare prima della sua fine!", {
          position : "top-center",
          autoClose: 1500,
          style : {background : "red", color : "white"}
        }) ;
        return false ;
      }
    }
  }

  if(holiday.name === '') {
    toast("Inserire un nome per la festività!", {
      position : "top-center",
      autoClose: 1500,
      style : {background : "red", color : "white"}
    }) ;
    return false ;
  }

  if(holiday.kind === '') {
    toast("Inserire la tipologia di festività!", {
      position : "top-center",
      autoClose: 1500,
      style : {background : "red", color : "white"}
    }) ;
    return false ;
  }

  return true ;
}

function DayMonthPicker({labelText, pickerState}) {

  const janDays = [] ;
  for (let i = 1; i <= 31 ; i++) {
    janDays.push(i) ;
  }
  const [day, setDay] = [pickerState.day, pickerState.setDay] ;
  let [days, setDays] = useState(janDays) ;
  const [month, setMonth] = [pickerState.month, pickerState.setMonth] ;

  function extractDaysFrom(days) {
    return (
      days.map((day) => {
        return <MenuItem value={day}>{day}</MenuItem>
      })
    )
  }

  function changeDay(event) {
    setDay(event.target.value) ;
  }

  function prepareDaysArray(monthName) {
    let newDays = [] ;
    switch (monthName) {
      case "January" :
      case "March" :
      case "May" :
      case "July" :
      case "August" :
      case "October" :
      case "December" :
        newDays = [] ;
        for (let i = 1; i <= 31 ; i++) {
          newDays.push(i) ;
        }
        setDays(newDays) ;
        break ;
      case "April" :
      case "June" :
      case "September" :
      case "November" :
        newDays = [] ;
        for (let i = 1; i <= 30 ; i++) {
          newDays.push(i) ;
        }
        setDays(newDays) ;
        break ;
      case "February" :
        newDays = [] ;
        for (let i = 1; i <= 29 ; i++) {
          newDays.push(i) ;
        }
        setDays(newDays) ;
        break ;
    }
  }

  function changeMonth(event) {
    prepareDaysArray(event.target.value) ;
    setMonth(event.target.value);
    setDay(1) ;
  }

  return (
    <div style={{display : "flex", width : 250, paddingBottom : 5, paddingTop : 5, flexDirection : "column"}}>
      <div>{labelText}</div>
      <div style={{display : "flex", flexDirection : "row"}}>
        <Select
          value={day}
          onChange={changeDay}
          fullWidth
        >
          {extractDaysFrom(days)}
        </Select>
        <Select
          value={month}
          onChange={changeMonth}
          fullWidth
        >
          <MenuItem value={"January"}>Gennaio</MenuItem>
          <MenuItem value={"February"}>Febbraio</MenuItem>
          <MenuItem value={"March"}>Marzo</MenuItem>
          <MenuItem value={"April"}>Aprile</MenuItem>
          <MenuItem value={"May"}>Maggio</MenuItem>
          <MenuItem value={"June"}>Giugno</MenuItem>
          <MenuItem value={"July"}>Luglio</MenuItem>
          <MenuItem value={"August"}>Agosto</MenuItem>
          <MenuItem value={"September"}>Settembre</MenuItem>
          <MenuItem value={"October"}>Ottobre</MenuItem>
          <MenuItem value={"November"}>Novembre</MenuItem>
          <MenuItem value={"December"}>Dicembre</MenuItem>
        </Select>
      </div>
    </div>
  )
}

function RecurrentResult({recurrent, pickerState, datesState}) {

  const startPickerState = {} ;

  startPickerState.day = pickerState.startDay ;
  startPickerState.setDay = pickerState.setStartDay ;
  startPickerState.month = pickerState.startMonth ;
  startPickerState.setMonth = pickerState.setStartMonth ;

  const endPickerState = {} ;

  endPickerState.day = pickerState.endDay ;
  endPickerState.setDay = pickerState.setEndDay ;
  endPickerState.month = pickerState.endMonth ;
  endPickerState.setMonth = pickerState.setEndMonth ;

  function updateDates(values) {
    datesState.setDates(values.map((value) => {
      return value.toDate() ;
    }))
  }

  if(recurrent) {
    return (
      <div style={{paddingTop : 20}}>
        <DayMonthPicker labelText={"Inizio festività"} pickerState={startPickerState}/>
        <DayMonthPicker labelText={"Fine festività"} pickerState={endPickerState}/>
      </div>
    )
  } else {
    return (<Calendar
      minDate={new Date()}
      range
      value={datesState.dates}
      onChange={updateDates}
    />)
  }
}

function CalendarOrPicker({recurrent, setRecurrent, pickerState, datesState}) {

  return (
    <div style={{display: "flex", alignItems : "center", flexDirection : "column"}}>
      <div style={{display : "flex", flexDirection : "row", paddingBottom : 10}}>
        <div style={{paddingRight : 10}}>La festività è ricorrente</div>
        <input type={"checkbox"} name={"recurrent"}
               checked={recurrent} onChange={e => {
          setRecurrent(!recurrent)
          return !(e.target.checked)
        }}/>
      </div>
      <RecurrentResult recurrent={recurrent} pickerState={pickerState} datesState={datesState}/>
    </div>
  );
}

export default function InserisciFestivita() {

  const [kind, setKind] = React.useState('');
  const [name, setName] = useState('') ;
  const [location, setLocation] = useState('') ;
  const [recurrent, setRecurrent] = useState(false) ;

  //Recurrent state
  const [startMonth, setStartMonth] = useState('January') ;
  const [startDay, setStartDay] = useState(1) ;
  const [endMonth, setEndMonth] = useState('January') ;
  const [endDay, setEndDay] = useState(1) ;

  //Non-recurrent state
  const [dates, setDates] = useState([]) ;

  const pickerState = {} ;
  pickerState.startDay = startDay ;
  pickerState.setStartDay = setStartDay ;
  pickerState.startMonth = startMonth ;
  pickerState.setStartMonth = setStartMonth ;

  pickerState.endDay = endDay ;
  pickerState.setEndDay = setEndDay ;
  pickerState.endMonth = endMonth ;
  pickerState.setEndMonth = setEndMonth ;

  const datesState = {} ;
  datesState.dates = dates ;
  datesState.setDates = setDates ;

  async function onSaveClick() {
    const holiday = {} ;

    holiday.name = name ;
    holiday.location = location ;
    holiday.recurrent = recurrent ;
    holiday.kind = kind ;

    if(recurrent) {
      holiday.startMonth = startMonth ;
      holiday.startDay = startDay ;
      holiday.endMonth = endMonth ;
      holiday.endDay = endDay ;
    }
    else {
      if(dates.length < 2) {
        toast("Inserisci le date nel calendario!", {
          position : "top-center",
          autoClose: 1500,
          style : {background : "red", color : "white"}
        }) ;
        return ;
      }
      holiday.startMoment = dates[0]
      holiday.endMoment = dates[dates.length -1] ;
    }

    if(checkDataIsCorrect(holiday)) {
      const code = await ((new HolidaysAPI()).saveCustomHoliday(holiday)) ;

      if(code !== 202) {
        toast("Errore nel salvataggio della festività", {
          position : "top-center",
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
      }
    }
  }

  const handleChange = (event) => {
    setKind(event.target.value);
  };

  return (
    <section style={{backgroundColor: '#eee'}}>
      <MDBContainer className="py-5" style={{height: '85vh',}}>
        <MDBCard alignment='center'>
          <MDBCardBody>
            <MDBCardTitle>Inserisci una nuova festività</MDBCardTitle>
            <MDBRow>
              <div style={{display : "flex", justifyContent : "center", overflowX : "scroll", paddingTop : 20}}>
                <div style={{display : "flex", flexDirection : "column", paddingRight : "7px"}}>
                  <CalendarOrPicker recurrent={recurrent} setRecurrent={setRecurrent} pickerState={pickerState} datesState={datesState}/>
                </div>
                <div style={{
                  display: "flex",
                  flexDirection: "column",
                  minWidth : 300,
                  paddingLeft: "7px",
                  paddingTop : 30
                }}>
                  <div style={{
                    paddingBottom: "20px",
                    minWidth: 300,
                    maxWidth: 300
                  }}>
                    <TextField
                      label="Inserisci il nome della festività"
                      fullWidth
                      onChange={(event) => {
                        setName(event.target.value)
                      }}
                    />
                    <div style={{ display: "flex", alignItems: "flex-start", fontSize: 12, color: "gray"}}>Obbligatorio</div>
                  </div>
                  <div style={{
                    paddingBottom: "20px",
                    minWidth: 300,
                    maxWidth: 300
                  }}>
                    <div style={{display : "flex", alignItems : "flex-start", fontSize : 12, color : "gray"}}>Scegli la tipologia</div>
                    <Select
                      value={kind}
                      onChange={handleChange}
                      fullWidth
                    >
                      <MenuItem value={"Religious"}>Religiosa</MenuItem>
                      <MenuItem value={"Secular"}>Laica</MenuItem>
                      <MenuItem value={"Civil"}>Civile</MenuItem>
                      <MenuItem value={"National"}>Nazionale</MenuItem>
                      <MenuItem value={"Corporate"}>Aziendale</MenuItem>
                    </Select>
                    <div style={{display : "flex", alignItems : "flex-start", fontSize : 12, color : "gray"}}>Obbligatorio</div>
                  </div>
                  <div style={{paddingBottom: "20px", minWidth: 300, maxWidth: 300}}>
                    <TextField
                      id="fullWidth"
                      label="Inserisci la località della festa"
                      fullWidth
                      onChange={(event) => {setLocation(event.target.value)}}
                    />
                  </div>
                  <div style={{paddingBottom: "20px", minWidth: 300, maxWidth: 300}}>
                    <Button onClick={onSaveClick}>
                      Salva
                    </Button>
                  </div>
                </div>
              </div>
            </MDBRow>
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
      <ToastContainer/>
    </section>
  )
}
