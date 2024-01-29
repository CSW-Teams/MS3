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
        <MDBCol>{value.category}</MDBCol>
        <MDBCol>{value.location}</MDBCol>
        <MDBCol>
          <Button onClick={() => {

              let asyncF = async () => {
                let data = {
                  id : value.id,
                  isRecurrent: false
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
          <MDBCol>Nome</MDBCol>
          <MDBCol>Inizio</MDBCol>
          <MDBCol>Fine</MDBCol>
          <MDBCol>Tipo</MDBCol>
          <MDBCol>Località</MDBCol>
          <MDBCol>Cancella</MDBCol>
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
        <MDBCol>{value.category}</MDBCol>
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
          <MDBCol>Nome</MDBCol>
          <MDBCol>Inizio</MDBCol>
          <MDBCol>Fine</MDBCol>
          <MDBCol>Tipo</MDBCol>
          <MDBCol>Località</MDBCol>
          <MDBCol>Cancella</MDBCol>
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
      <MDBCardTitle>Festività Normali</MDBCardTitle>
      <MDBRow>
        <NormalHolidays holidays={normalHolidays} setHolidays={setNormalHolidays}/>
      </MDBRow>
      <MDBCardTitle>Festività Ricorrenti</MDBCardTitle>
      <MDBRow>
        <RecurrentHolidays holidays={recurrentHolidays} setHolidays={setRecurrentHolidays}/>
      </MDBRow>
    </>
  )
}
