import React from 'react';
import {UtenteAPI} from "../../API/UtenteAPI";
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
   MDBCardTitle
} from "mdb-react-ui-kit";
import {CategoriaUtenteAPI} from "../../API/CategoriaUtenteAPI";
import {Button} from "@material-ui/core";
import AggiungiCategoria from "../../components/common/BottomViewAggiungiTurnazione"
import AggiungiCategoriaStato from "../../components/common/BottomViewAggiungiCategoriaStat"
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import {toast} from "react-toastify";

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
      specializzazioni_utente:[],
      turnazioni_utente:[],
    }

  }
  async componentDidMount() {
    let id = this.props.match.params.idUser
    let attore = localStorage.getItem("attore")
    let utente = await(new UtenteAPI().getUserDetails(id));
    let categorie_utente = await(new CategoriaUtenteAPI().getCategoriaUtente(id))
    let specializzazioni_utente = await(new CategoriaUtenteAPI().getSpecializzazioniUtente(id))
    let turnazioni_utente = await(new CategoriaUtenteAPI().getTurnazioniUtente(id))

    this.setState({
      attore : attore,
      nome: utente.nome,
      cognome: utente.cognome,
      ruolo: utente.ruoloEnum,
      email: utente.email,
      dataNascita: utente.dataNascita,
      categorie_utente : categorie_utente,
      specializzazioni_utente:specializzazioni_utente,
      turnazioni_utente:turnazioni_utente,
    })
  }

  async handleDeleteRotazione(idRotazione, key) {
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    responseStatus = await categoriaUtenteApi.deleteRotazione(idRotazione, this.props.match.params.idUser);

    if (responseStatus === 200) {
      toast.success('Rotazione cancellata con successo', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      this.componentDidMount()
    } else if (responseStatus === 400) {
      toast.error('Errore nella cancellazione', {
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
  }

  async handlerDeleteCategoriaStato(idRotazione, key) {
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    responseStatus = await categoriaUtenteApi.deleteStato(idRotazione, this.props.match.params.idUser );

    if (responseStatus === 200) {
      toast.success('Rotazione cancellata con successo', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      this.componentDidMount()
    } else if (responseStatus === 400) {
      toast.error('Errore nella cancellazione', {
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
  }

  render() {

    function getTurnazioniSpecializzando() {
        return <MDBCol>
          <MDBCard>
            <MDBCardBody className="text-center">
              <MDBCardTitle>Rotazioni
                <AggiungiCategoria onPostAssegnazione = {()=>{this.componentDidMount() ;}} ></AggiungiCategoria>
              </MDBCardTitle>
              <MDBTable align="middle">
                <MDBTableHead>
                  <tr>
                    <th scope='col'>Rotazione</th>
                    <th scope='col'>Inizio validità</th>
                    <th scope='col'>Fine validità</th>
                    <th scope='col'></th>
                  </tr>
                </MDBTableHead>
                <MDBTableBody>
                  {this.state.turnazioni_utente.map((data, key) => {
                    return (
                      <tr key={key}>
                        <td>{data.categoria}</td>
                        <td>{data.inizio}</td>
                        <td>{data.fine}</td>
                        <td><IconButton aria-label="delete" onClick={() => this.handleDeleteRotazione(data.categoriaUtenteId, key)}><DeleteIcon />
                        </IconButton></td>
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
          return <MDBRow>
            <MDBCol sm="3">
              <MDBCardText>Indirizzo</MDBCardText>
            </MDBCol>
            <MDBCol sm="9">
              <MDBCardText className="text-muted">
                <MDBTableBody>
                {this.state.specializzazioni_utente.map((data, key) => {
                  return (
                    <tr key={key}>
                      <td>{data.categoria}</td>
                    </tr>
                  )
                })}
              </MDBTableBody>
              </MDBCardText>
            </MDBCol>
          </MDBRow>;
    }

    function getCategoriaStatoUtente() {
        return <MDBCard>
          <MDBCardBody className="text-center">
            <MDBCardTitle>Categorie utente
              <AggiungiCategoriaStato onPostAssegnazione={() => {
                this.componentDidMount();
              }}></AggiungiCategoriaStato>
            </MDBCardTitle>
            <MDBTable align="middle">
              <MDBTableHead>
                <tr>
                  <th scope='col'>Categoria</th>
                  <th scope='col'>Inizio validità</th>
                  <th scope='col'>Fine validità</th>
                  <th scope='col'></th>
                </tr>
              </MDBTableHead>
              <MDBTableBody>
                {this.state.categorie_utente.map((data, key) => {
                  return (
                    <tr key={key}>
                      <td>{data.categoria}</td>
                      <td>{data.inizio}</td>
                      <td>{data.fine}</td>
                      <td><IconButton aria-label="delete"
                                      onClick={() => this.handlerDeleteCategoriaStato(data.categoriaUtenteId, key)}>
                        <DeleteIcon/>
                      </IconButton></td>
                    </tr>
                  )
                })}
              </MDBTableBody>
            </MDBTable>
          </MDBCardBody>
        </MDBCard>;
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
                  <p
                    className="text-muted mb-1">{this.state.nome + " " + this.state.cognome}</p>
                  <p className="text-muted mb-4">{this.state.ruolo}</p>
                </MDBCardBody>
              </MDBCard>
            </MDBCol>
            <MDBCol lg="8">
              <MDBCard className="mb-4">
                <MDBCardBody>
                  <MDBCardTitle>Informazioni utente
                  </MDBCardTitle>
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
                        className="text-muted">{this.state.email}   <IconButton href={`mailto:${this.state.email}`}><i className="fa fa-envelope"
                                                                                   aria-hidden="true"></i></IconButton> </MDBCardText>
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
                  { this.state.ruolo==="STRUTTURATO" && getSpecializzazioneStrutturato.call(this)}
                </MDBCardBody>
              </MDBCard>
            </MDBCol>
          </MDBRow>
          <MDBRow>
            {this.state.attore!=="UTENTE" && this.state.ruolo!=="STRUTTURATO" && getTurnazioniSpecializzando.call(this)}
            <MDBCol>
              {this.state.attore!=="UTENTE" && getCategoriaStatoUtente.call(this)}
            </MDBCol>
          </MDBRow>
        </MDBContainer>
      </section>
    );
  }
}
