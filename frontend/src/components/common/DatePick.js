import DatePicker, {DateObject} from "react-multi-date-picker";
import Icon from "react-multi-date-picker/components/icon";
import DatePanel from "react-multi-date-picker/plugins/date_panel";
import ToolBar from "react-multi-date-picker/plugins/toolbar";
import Button from "@mui/material/Button";
import React, {useRef, useState} from "react";
import gregorian from "react-date-object/calendars/gregorian";
import gregorian_en from "react-date-object/locales/gregorian_en";
import {DesiderateAPI} from "../../API/DesiderataAPI"
import {CategoriaUtenteAPI} from "../../API/CategoriaUtenteAPI";

const months = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"]
const weeksName= ["Lun","Mar","Mer","Gio","Ven","Sab","Dom"]

export default function DatePick() {
  let id = localStorage.getItem("id");

  const datePickerRef = useRef()
  let current = new DateObject({ calendar: gregorian, locale: gregorian_en });
  const [date, setDate] = useState(current);
  const [open, setOpen] = useState(true);

  async function saveDesiderate() {
    console.log(date)
    setOpen(false)
    let desiderate = await(new DesiderateAPI().salvaDesiderate(date,id))


    datePickerRef.current.closeCalendar()

  }

  return ( <Button>
      <DatePicker
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
        maxDate={new Date().setDate(15)}
        onChange={setDate}
        value={date}
        calendarPosition="top-right"
      /></Button>)
}
