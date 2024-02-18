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
  MDBCardTitle,
} from "mdb-react-ui-kit";
import {CategoriaUtenteAPI} from "../../API/CategoriaUtenteAPI";
import AggiungiCategoriaStato
  from "../../components/common/BottomViewAddCondition";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import AggiungiCategoria
  from "../../components/common/BottomViewAggiungiTurnazione";
import {toast} from "react-toastify";
import { t } from "i18next";
import {panic} from "../../components/common/Panic";

/*
* Deprecated class
*/
export default class UserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      idUser : 1,
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
    let id
    let utente
    let categorie_utente
    let specializzazioni_utente
    let turnazioni_utente
    try {
      id = localStorage.getItem("id");
      utente = await(new UserAPI().getUserDetails(id));
      categorie_utente = await(new CategoriaUtenteAPI().getCategoriaUtente(id))
      specializzazioni_utente = await(new CategoriaUtenteAPI().getSpecializzazioniUtente(id))
      turnazioni_utente =  await(new CategoriaUtenteAPI().getTurnazioniUtente(id));
    } catch (err) {

      panic()
      return
    }
    this.setState({
      idUser : id,
      nome: utente.name,
      cognome: utente.lastname,
      ruolo: utente.role,
      email: utente.email,
      dataNascita: utente.birthday,
      categorie_utente : categorie_utente,
      specializzazioni_utente : specializzazioni_utente,
      turnazioni_utente: turnazioni_utente,
    })
  }

  async handleDeleteRotazioneLoggedUser(idRotazione, key) {
    console.log(idRotazione + key)
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    try {
      responseStatus = await categoriaUtenteApi.deleteRotazione(idRotazione,  this.state.idUser);
    } catch (err) {

      panic()
      return
    }
    console.log(responseStatus)

    if (responseStatus === 200) {
      toast.success(t("Rotation deleted successfully"), {
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
      toast.error(t("Error during deletion"), {
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

  async handlerDeleteCategoriaStatoLoggedUser(idRotazione, key) {
    console.log(idRotazione + key )
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    try {
      responseStatus = await categoriaUtenteApi.deleteStato(idRotazione, this.state.idUser);
    } catch (err) {

      panic()
      return
    }
    console.log(responseStatus)

    if (responseStatus === 200) {
      toast.success(t("Rotation deleted successfully"), {
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
      toast.error(t( "Error during deletion"), {
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
              <MDBCardTitle>{t("Rotations")}<AggiungiCategoria onPostAssegnazione = {()=>{this.componentDidMount()}}/> </MDBCardTitle>
              <MDBTable align="middle">
                <MDBTableHead>
                  <tr>
                    <th scope='col'>{t("Rotation")}</th>
                    <th scope='col'>{t("Start Date")}</th>
                    <th scope='col'>{t("End Date")}</th>
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
                        <td><IconButton aria-label="delete" onClick={() => this.handleDeleteRotazioneLoggedUser(data.categoriaUtenteId, key)}>
                          <DeleteIcon />
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

    function getCategoriaStatoUtente() {
      return <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>{t("User Category")}<AggiungiCategoriaStato onPostAssegnazione = {()=>{this.componentDidMount() ;}} />
          </MDBCardTitle>
          <MDBTable align="middle">
            <MDBTableHead>
              <tr>
                <th scope='col'>{t("Category")}</th>
                <th scope='col'>{t("Start Date")}</th>
                <th scope='col'>{t("End Date")}</th>
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
                    <td><IconButton aria-label="delete" onClick={() => this.handlerDeleteCategoriaStatoLoggedUser(data.categoriaUtenteId, key)}>
                      <DeleteIcon />
                    </IconButton></td>
                  </tr>
                )
              })}
            </MDBTableBody>
          </MDBTable>
        </MDBCardBody>
      </MDBCard>;
    }


    function getSpecializzazioneStrutturato() {
        return <MDBRow>
          <MDBCol sm="3">
            <MDBCardText>{t("Specializations")}</MDBCardText>
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
                    <MDBCardTitle>{t("User Information")}</MDBCardTitle>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t("Name")}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.nome}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t("Surname")}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.cognome}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t("Email Address")}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.email}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t("Birthdate")}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.dataNascita}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t("Role")}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.ruolo}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    {(this.state.ruolo==="STRUCTURED") && getSpecializzazioneStrutturato.call(this)}
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
            </MDBRow>
            <MDBRow>
              { (this.state.ruolo!=="STRUCTURED") && getTurnazioniSpecializzando.call(this)}
              <MDBCol>
                {getCategoriaStatoUtente.call(this)}
              </MDBCol>
            </MDBRow>
          </MDBContainer>
        </section>
      );
    }
}
