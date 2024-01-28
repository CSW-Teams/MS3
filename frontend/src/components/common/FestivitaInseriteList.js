import {
  MDBCardTitle,
  MDBCol,
  MDBRow,
  MDBTable,
  MDBTableBody, MDBTableHead
} from "mdb-react-ui-kit";
import {Button} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";

function NormalHolidays({holidays, setHolidays}) {

  let formattedHolidays = holidays.map((value) => {
    return(
      <MDBRow>
        <MDBCol>{value.name}</MDBCol>
        <MDBCol>{value.start}</MDBCol>
        <MDBCol>{value.end}</MDBCol>
        <MDBCol>{value.kind}</MDBCol>
        <MDBCol>{value.description}</MDBCol>
        <MDBCol>
          <Button onClick={() => {
            setHolidays(holidays.filter((value1) => {
              return value1 !== value
            }))
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
          <MDBCol>Descrizione</MDBCol>
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
        <MDBCol>{value.start}</MDBCol>
        <MDBCol>{value.end}</MDBCol>
        <MDBCol>{value.kind}</MDBCol>
        <MDBCol>{value.description}</MDBCol>
        <MDBCol>
          <Button onClick={() => {
            setHolidays(holidays.filter((value1) => {
              return value1 !== value
            }))
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
          <MDBCol>Descrizione</MDBCol>
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
