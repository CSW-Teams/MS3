import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer,
  MDBRow
} from "mdb-react-ui-kit";
import React from "react";
import {ToastContainer} from "react-toastify";
import InserisciFestivitaForm from "../../components/common/InserisciFestivitaForm";

export default function InserisciFestivita() {

  return (
    <section style={{backgroundColor: '#eee'}}>
      <MDBContainer className="py-5" style={{height: '85vh',}}>
        <MDBCard alignment='center'>
          <MDBCardBody>
            <MDBCardTitle>Inserisci una nuova festività</MDBCardTitle>
            <MDBRow>
              <InserisciFestivitaForm/>
            </MDBRow>
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
      <ToastContainer/>
    </section>
  )
}
