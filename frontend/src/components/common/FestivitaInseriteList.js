import {
  MDBCardTitle,
  MDBCol,
  MDBRow,
  MDBTable,
  MDBTableBody, MDBTableHead
} from "mdb-react-ui-kit";
import {Button} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import {HolidaysAPI} from "../../API/HolidaysAPI";
import {toast} from "react-toastify";
import {useEffect} from "react";
import { t } from "i18next";

function translateCategory(category) {
  switch (category) {
    case "Civil":
      return t("Civil")
    case "Religious":
      return t("Religious")
    case "Secular":
      return t("Secular")
    case "National":
      return t("National")
    case "Corporate":
      return t("Corporate")
  }
}

function epochDayToString(epochDay) {
  let date = new Date(epochDay * 86400000)

  return date.getDate() + "/" + (date.getMonth()+1) + "/" + date.getFullYear()
}

function NormalHolidays({holidays, setHolidays}) {

  let formattedHolidays = holidays.map((value) => {
    return(
      <MDBRow>
        <MDBCol>{value.name}</MDBCol>
        <MDBCol>{epochDayToString(value.startDateEpochDay)}</MDBCol>
        <MDBCol>{epochDayToString(value.endDateEpochDay)}</MDBCol>
        <MDBCol>{translateCategory(value.category)}</MDBCol>
        <MDBCol>{value.location}</MDBCol>
        <MDBCol>
          <Button onClick={() => {

              let asyncF = async () => {
                let data = {
                  id : value.id,
                  isRecurrent: false
                }

                let response
                try {
                  response = await (new HolidaysAPI().deleteCustomHoliday(data))
                } catch (err) {

                  toast(t('Connection Error, please try again later'), {
                    position: 'top-center',
                    autoClose: 1500,
                    style : {background : "red", color : "white"}
                  })
                  return
                }

                if (response === 200) {
                  setHolidays(holidays.filter((value1) => {
                    return value1 !== value
                  }))
                } else {
                  toast("Errore nella cancellazione della festività", {
                    position : "top-center",
                    autoClose: 1500,
                    style : {background : "red", color : "white"}
                  })
                }
              }

              asyncF().then(()=>{})

          }} startIcon={<DeleteIcon/>}/>
        </MDBCol>
      </MDBRow>
    )
  })

  return (
    <MDBTable>
      <MDBTableBody>
        <MDBRow>
          <MDBCol>{t("ReadHolidayName")}</MDBCol>
          <MDBCol>{t("ReadHolidayStart")}</MDBCol>
          <MDBCol>{t("ReadHolidayEnd")}</MDBCol>
          <MDBCol>{t("ReadHolidayCategory")}</MDBCol>
          <MDBCol>{t("ReadHolidayPlace")}</MDBCol>
          <MDBCol>{t("ReadDeleteHoliday")}</MDBCol>
        </MDBRow>
        {formattedHolidays}
      </MDBTableBody>
    </MDBTable>
  )
}

function RecurrentHolidays({holidays, setHolidays}) {

  let formattedHolidays = holidays.map((value) => {
    return(
      <MDBRow>
        <MDBCol>{value.name}</MDBCol>
        <MDBCol>{value.startDay + "/" + value.startMonth}</MDBCol>
        <MDBCol>{value.endDay + "/" + value.endMonth}</MDBCol>
        <MDBCol>{translateCategory(value.category)}</MDBCol>
        <MDBCol>{value.location}</MDBCol>
        <MDBCol>
          <Button onClick={async () => {

            let data = {
              id : value.id,
              isRecurrent: true
            }

            let response = await (new HolidaysAPI().deleteCustomHoliday(data))

            if (response === 200) {
              setHolidays(holidays.filter((value1) => {
                return value1 !== value
              }))
            } else {
              toast("Errore nella cancellazione della festività", {
                position : "top-center",
                autoClose: 1500,
                style : {background : "red", color : "white"}
              })
            }
          }} startIcon={<DeleteIcon/>}/>
        </MDBCol>
      </MDBRow>
    )
  })

  return (
    <MDBTable>
      <MDBTableHead>
        <MDBRow>
          <MDBCol>{t("ReadHolidayName")}</MDBCol>
          <MDBCol>{t("ReadHolidayStart")}</MDBCol>
          <MDBCol>{t("ReadHolidayEnd")}</MDBCol>
          <MDBCol>{t("ReadHolidayCategory")}</MDBCol>
          <MDBCol>{t("ReadHolidayPlace")}</MDBCol>
          <MDBCol>{t("ReadDeleteHoliday")}</MDBCol>
        </MDBRow>
      </MDBTableHead>
      <MDBTableBody>
        {formattedHolidays}
      </MDBTableBody>
    </MDBTable>
  )
}

export default function FestivitaInseriteList({normalHolidays, setNormalHolidays, recurrentHolidays, setRecurrentHolidays}) {

  return(
    <>
      <MDBCardTitle>{t("Normal Holidays")}</MDBCardTitle>
      <MDBRow>
        <NormalHolidays holidays={normalHolidays} setHolidays={setNormalHolidays}/>
      </MDBRow>
      <MDBCardTitle>{t("Recurrent Holidays")}</MDBCardTitle>
      <MDBRow>
        <RecurrentHolidays holidays={recurrentHolidays} setHolidays={setRecurrentHolidays}/>
      </MDBRow>
    </>
  )
}
