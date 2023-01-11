import React from 'react';
import {UtenteAPI} from "../API/UtenteAPI";
import {
  MDBBtn,
  MDBCard,
  MDBCardBody, MDBCardImage, MDBCardText,
  MDBCol,
  MDBContainer,
  MDBRow
} from "mdb-react-ui-kit";

export default class UserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      nome: "",
      cognome: "",
      ruolo: "",
      email: "",
      categorie: "",
      dataNascita: ""
    }

  }
  async componentDidMount() {
    let utente = await(new UtenteAPI().getUserDetails(7));
    this.setState({
      nome: utente.nome,
      cognome: utente.cognome,
      ruolo: utente.ruoloEnum,
      email: utente.email,
      dataNascita: utente.dataNascita
    })
  }


  render() {
    return (
      <section style={{ backgroundColor: '#eee' }}>
        <MDBContainer className="py-5">
          <MDBRow>
            <MDBCol lg="4">
              <MDBCard className="mb-4">
                <MDBCardBody className="text-center">
                  <MDBCardImage
                    src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava3.webp"
                    alt="avatar"
                    className="rounded-circle"
                    style={{ width: '150px' }}
                    fluid />
                  <p className="text-muted mb-1">{this.state.nome + " " + this.state.cognome}</p>
                  <p className="text-muted mb-4">{this.state.ruolo}</p>
                </MDBCardBody>
              </MDBCard>
              <MDBCardBody>
              <MDBRow lg="4">
                <MDBCard className="mb-4">
                  <MDBCardBody className="text-center">

                  </MDBCardBody>
                </MDBCard>
              </MDBRow>
                </MDBCardBody>
            </MDBCol>
            <MDBCol lg="8">
              <MDBCard className="mb-4">
                <MDBCardBody>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>Nome e Cognome</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText className="text-muted">{this.state.nome + " " + this.state.cognome}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <hr />
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>Email</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText className="text-muted">{this.state.email}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <hr />
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>Data di Nascita</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText className="text-muted">{this.state.dataNascita}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <hr />
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>Ruolo</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText className="text-muted">{this.state.ruolo}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                </MDBCardBody>
              </MDBCard>
            </MDBCol>
          </MDBRow>
        </MDBContainer>
      </section>
    );
  }
}
