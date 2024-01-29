import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer,
  MDBRow
} from "mdb-react-ui-kit";
import React, {useEffect, useState} from "react";
import {toast, ToastContainer} from "react-toastify";
import InserisciFestivitaForm from "../../components/common/InserisciFestivitaForm";
import FestivitaInseriteList from "../../components/common/FestivitaInseriteList";
import {HolidaysAPI} from "../../API/HolidaysAPI";

export default function InserisciFestivita() {

  let [normalHolidays, setNormalHolidays] = useState([])
  let [recurrentHolidays, setRecurrentHolidays] = useState([])

  useEffect(() => {

    const asyncFetch = async () => {

      let [response, content] = await new HolidaysAPI().getCustomHolidays()
      if (response !== 200) {
        window.location.reload()
      }

      setNormalHolidays(content.normalHolidays)
      setRecurrentHolidays(content.recurrentHolidays)
    }

    asyncFetch().then(() => {})

  }, []);


  return (
    <section style={{backgroundColor: '#eee'}}>
      <MDBContainer className="py-5" style={{height: '85vh',}}>
        <MDBCard alignment='center'>
          <MDBCardBody>
            <MDBCardTitle>Inserisci una nuova festività</MDBCardTitle>
            <MDBRow>
              <InserisciFestivitaForm normalHolidays={normalHolidays} setNormalHolidays={setNormalHolidays}
                                      recurrentHolidays={recurrentHolidays} setRecurrentHolidays={setRecurrentHolidays}/>
            </MDBRow>
            <MDBRow>
              <FestivitaInseriteList normalHolidays={normalHolidays} setNormalHolidays={setNormalHolidays}
                                     recurrentHolidays={recurrentHolidays} setRecurrentHolidays={setRecurrentHolidays}/>
            </MDBRow>
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
      <ToastContainer/>
    </section>
  )
}
