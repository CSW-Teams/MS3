import DatePicker, {DateObject} from "react-multi-date-picker";
import Icon from "react-multi-date-picker/components/icon";
import DatePanel from "react-multi-date-picker/plugins/date_panel";
import ToolBar from "react-multi-date-picker/plugins/toolbar";
import Button from "@mui/material/Button";
import React, {useRef, useState} from "react";
import gregorian from "react-date-object/calendars/gregorian";
import gregorian_en from "react-date-object/locales/gregorian_en";
import {DesiderateAPI} from "../../API/DesiderataAPI"
import {toast, ToastContainer} from "react-toastify";

const months = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"]
const weeksName= ["Dom","Lun","Mar","Mer","Gio","Ven","Sab"]

export default function DatePick(props) {
  let id = localStorage.getItem("id");
  let desiderataApi = new DesiderateAPI();

  const datePickerRef = useRef()
  const [date, setDate] = useState([]);
  const [open, setOpen] = useState(true);

  async function saveDesiderate() {
    setOpen(false)
    let response = await(desiderataApi.salvaDesiderate(date,id))
    let responseStatus  = response.status
    props.onSelectdate();
    datePickerRef.current.closeCalendar()
    console.log(responseStatus)

    if (responseStatus === 202) {
      toast.success('Desiderate caricate con successo', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (responseStatus === 400) {
      toast.error('Errore nel caricamento delle desiderate', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }
    setDate([])
    }

  return (
    <Button>

      <DatePicker
        mapDays={({ date }) => {
          let desiderataPresente = false
          for(let i=0; i < props.desiderate.length; i++){
            if(date.format("DD/M/YYYY") === props.desiderate[i].data){
              desiderataPresente = true
              break
            }
          }
          if (desiderataPresente) return {
            disabled: true,
            style: { color: "#ccc" },
            onClick: () => toast.error('Desiderata già inserita', {
              position: "top-center",
              autoClose: 5000,
              hideProgressBar: true,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
              theme: "colored",
            })
          }
        }}
        ref={datePickerRef}
        open={open}
        className="teal"
        render={<Icon/>}
        plugins={[
          <DatePanel
            markFocused
            sort="date"
            header={"Date"}
          />,
          <ToolBar
            position="bottom"
            names={{
              today: "oggi",
              deselect: "deseleziona",
              close: "chiudi"
            }}
          />,
          <Button position="bottom" onClick={saveDesiderate} >Conferma preferenze</Button>
        ]}
        multiple
        containerStyle={{ width: "100%" }}
        style={{
          width: "100%",
          height: "26px",
          boxSizing: "border-box"
        }}
        months={months}
        weekDays={weeksName}
        currentDate={true}
        numberOfMonths={1}
        minDate={new Date()}
        //maxDate={} TO DO : massima data per lo scheduler
        onChange={setDate}
        value={date}
        calendarPosition="top-right"
      /></Button>)
}
