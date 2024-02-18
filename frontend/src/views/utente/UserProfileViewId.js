import React from 'react';
import {UserAPI} from "../../API/UserAPI";
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
import AggiungiCategoria from "../../components/common/BottomViewAggiungiTurnazione"
import AggiungiCategoriaStato from "../../components/common/BottomViewAddCondition"
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import {toast} from "react-toastify";
import {t} from "i18next";

/**
 * Deprecated Class
 */
export default class UserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      nome: "",
      cognome: "",
      ruolo: "",
      email: "",
      attore : "",
      categorie: "",
      dataNascita: "",
      categorie_utente: [],
      specializzazioni_utente:[],
      turnazioni_utente:[],
    }

  }
  async componentDidMount() {
    let id = this.props.match.params.idUser;
    let attore = localStorage.getItem("attore");
    let utente = await(new UserAPI().getUserDetails(id));
    let categorie_utente = await(new CategoriaUtenteAPI().getCategoriaUtente(id))
    let specializzazioni_utente = await(new CategoriaUtenteAPI().getSpecializzazioniUtente(id))
    let turnazioni_utente = await(new CategoriaUtenteAPI().getTurnazioniUtente(id))

    this.setState({
      attore : attore,
      nome: utente.name,
      cognome: utente.lastname,
      ruolo: utente.role,
      email: utente.email,
      dataNascita: utente.birthday,
      categorie_utente : categorie_utente,
      specializzazioni_utente:specializzazioni_utente,
      turnazioni_utente: turnazioni_utente,
    })
  }

  async handleDeleteRotazione(idRotazione, key) {
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    responseStatus = await categoriaUtenteApi.deleteRotazione(idRotazione, this.props.match.params.idUser);

    if (responseStatus === 200) {
      toast.success(t('Rotation deleted successfully'), {
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
      toast.error(t('Error during deletion'), {
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
      toast.success(t('Rotation deleted successfully'), {
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
      toast.error(t('Error during deletion'), {
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
              <MDBCardTitle>{t('Rotations')}
                <AggiungiCategoria onPostAssegnazione = {()=>{this.componentDidMount() ;}} ></AggiungiCategoria>
              </MDBCardTitle>
              <MDBTable align="middle">
                <MDBTableHead>
                  <tr>
                    <th scope='col'>{t('Rotation')}</th>
                    <th scope='col'>{t('Start Date')}</th>
                    <th scope='col'>{t('End Date')}</th>
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
              <MDBCardText>{t('Address')}</MDBCardText>
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
            <MDBCardTitle>{t('User Category')}
              <AggiungiCategoriaStato onPostAssegnazione={() => {
                this.componentDidMount();
              }}></AggiungiCategoriaStato>
            </MDBCardTitle>
            <MDBTable align="middle">
              <MDBTableHead>
                <tr>
                  <th scope='col'>{t('Category')}</th>
                  <th scope='col'>{t('Start Date')}</th>
                  <th scope='col'>{t('End Date')}</th>
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
                </MDBCardBody>
              </MDBCard>
            </MDBCol>
            <MDBCol lg="8">
              <MDBCard className="mb-4">
                <MDBCardBody>
                  <MDBCardTitle>{t('User Information')}
                  </MDBCardTitle>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>{t('Name')}</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText
                        className="text-muted">{this.state.nome}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>{t('Surname')}</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText
                        className="text-muted">{this.state.cognome}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>{t('Email Address')}</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText
                        className="text-muted">{this.state.email}   <IconButton href={`mailto:${this.state.email}`}><i className="fa fa-envelope"
                                                                                   aria-hidden="true"></i></IconButton> </MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>{t('Birthdate')}</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText
                        className="text-muted">{this.state.dataNascita}</MDBCardText>
                    </MDBCol>
                  </MDBRow>
                  <MDBRow>
                    <MDBCol sm="3">
                      <MDBCardText>{t('Role')}</MDBCardText>
                    </MDBCol>
                    <MDBCol sm="9">
                      <MDBCardText
                        className="text-muted">{t(this.state.ruolo)}</MDBCardText>
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
