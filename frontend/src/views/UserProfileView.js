import React from 'react';
import {UtenteAPI} from "../API/UtenteAPI";
import {
  MDBCard,
  MDBCardBody,
  MDBCardImage,
  MDBCardText,
  MDBCol,
  MDBContainer,
  MDBRow,
  MDBTable,
  MDBTableHead,
  MDBTableBody,
  MDBCardTitle,
} from "mdb-react-ui-kit";
import {CategoriaUtenteAPI} from "../API/CategoriaUtenteAPI";
import {Button} from "@material-ui/core";

export default class UserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      nome: "",
      cognome: "",
      ruolo: "",
      email: "",
      categorie: "",
      dataNascita: "",
      categorie_utente: [],
      specializzazioni_utente: [],
      turnazioni_utente:[],
    }
  }

  async componentDidMount() {
    let utente = await(new UtenteAPI().getUserDetails(1));
    let categorie_utente = await(new CategoriaUtenteAPI().getCategoriaUtente(1))
    let specializzazioni_utente = await(new CategoriaUtenteAPI().getSpecializzazioniUtente(1))
    let turnazioni_utente =  await(new CategoriaUtenteAPI().getTurnazioniUtente(1));
    this.setState({
      nome: utente.nome,
      cognome: utente.cognome,
      ruolo: utente.ruoloEnum,
      email: utente.email,
      dataNascita: utente.dataNascita,
      categorie_utente : categorie_utente,
      specializzazioni_utente : specializzazioni_utente,
      turnazioni_utente:turnazioni_utente,
    })
  }

  render() {

    function getTurnazioni() {
      if(this.state.ruolo!=="STRUTTURATO")
       return <MDBCol>
        <MDBCard>
          <MDBCardBody className="text-center">
            <MDBCardTitle>Turnazioni
              <Button size="small"><i className="fas fa-edit fa-lg"> </i></Button>
            </MDBCardTitle>
            <MDBTable align="middle">
              <MDBTableHead>
                <tr>
                  <th scope='col'>Turnazione</th>
                  <th scope='col'>Inizio validità</th>
                  <th scope='col'>Fine validità</th>
                </tr>
              </MDBTableHead>
              <MDBTableBody>
                {this.state.specializzazioni_utente.map((data, key) => {
                  return (
                    <tr key={key}>
                      <td>{data.categoria}</td>
                      <td>{data.inizio}</td>
                      <td>{data.fine}</td>
                    </tr>
                  )
                })}
              </MDBTableBody>
            </MDBTable>
          </MDBCardBody>
        </MDBCard>
      </MDBCol>;
    }


    function getSpecializzazioneStrutturato() {
      if(this.state.ruolo==="STRUTTURATO")
        return <MDBRow>
          <MDBCol sm="3">
            <MDBCardText>Specializzazione</MDBCardText>
          </MDBCol>
          <MDBCol sm="9">
            <MDBCardText className="text-muted">  <MDBTableBody>
              {this.state.specializzazioni_utente.map((data, key) => {
                return (
                  <tr key={key}>
                    <td>{data.categoria}</td>
                  </tr>
                )
              })}
            </MDBTableBody></MDBCardText>
          </MDBCol>
        </MDBRow>;
    }


    return (
        <section style={{backgroundColor: '#eee'}}>
          <MDBContainer className="py-5">
            <MDBRow>
              <MDBCol lg="4">
                <MDBCard className="mb-4">
                  <MDBCardBody className="text-center">
                    <MDBCardImage
                      src="https://erad.com/wp-content/uploads/choice-icons-physician-portal.png"
                      alt="avatar"
                      className="rounded-circle"
                      style={{width: '150px'}}
                      fluid/>
                    <p className="text-muted mb-1">{this.state.nome + " " + this.state.cognome}</p>
                    <p className="text-muted mb-4">{this.state.ruolo}</p>
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
              <MDBCol lg="8">
                <MDBCard className="mb-4">
                  <MDBCardBody>
                    <MDBCardTitle>Informazioni utente</MDBCardTitle>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Nome</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.nome}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Cognome</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.cognome}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Email</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.email}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Data di Nascita</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.dataNascita}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Ruolo</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.ruolo}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    {getSpecializzazioneStrutturato.call(this)}
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
            </MDBRow>
            <MDBRow>
              {getTurnazioni.call(this)}
              <MDBCol>
                <MDBCard>
                  <MDBCardBody className="text-center">
                    <MDBCardTitle>Categorie utente</MDBCardTitle>
                    <MDBTable align="middle">
                      <MDBTableHead>
                        <tr>
                          <th scope='col'>Categoria</th>
                          <th scope='col'>Inizio validità</th>
                          <th scope='col'>Fine validità</th>
                        </tr>
                      </MDBTableHead>
                      <MDBTableBody>
                        {this.state.categorie_utente.map((data, key) => {
                          if (data.categoria === "OVER_62")
                            data.fine = "//"
                          return (
                            <tr key={key}>
                              <td>{data.categoria}</td>
                              <td>{data.inizio}</td>
                              <td>{data.fine}</td>
                            </tr>
                          )
                        })}
                      </MDBTableBody>
                    </MDBTable>
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
            </MDBRow>
          </MDBContainer>
        </section>
      );
    }
}
