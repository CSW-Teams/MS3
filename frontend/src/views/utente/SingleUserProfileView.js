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
  from "../../components/common/BottomViewAggiungiCategoriaStat";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import AggiungiCategoria
  from "../../components/common/BottomViewAggiungiTurnazione";
import {toast} from "react-toastify";
import {Button} from "@material-ui/core";

/**
 * Class needed to fromat correctly the attributes permanentConditions and temporaryConditions
 * coming from JSON file obtained by API call
 */
export class ConditionsToShow {

  constructor(label, startDate, endDate){
    this.label = label;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}



export default class SingleUserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      userID : 1,
      name: "",
      lastname: "",
      seniority: "",
      email: "",
      birthday: "",
      systemActors: [],
      specializations: [],
      conditions:[],
    }
  }

  async componentDidMount() {
    let id = localStorage.getItem("id");
    let user = await(new UserAPI().getSingleUserProfileDetails(id));
    let systemActorsUserItalianTranslation = [];
    let conditionsToShow = [];

    for(var i = 0;i < user.systemActors.length;i++){
      systemActorsUserItalianTranslation[i] =
        (user.systemActors[i] === "PLANNER" ? "Pianificatore" :
          (user.systemActors[i] === "DOCTOR" ? "Dottore" : "Configuratore"));
    }

    for(var i = 0;i<user.permanentConditions.length;i++){
      conditionsToShow[i] = new ConditionsToShow(user.permanentConditions[i], "", "");
    }

    for(var i = 0 ;i<user.temporaryConditions.length;i++){
      conditionsToShow[i + user.permanentConditions.length] = new ConditionsToShow(user.temporaryConditions[i].label,
                              new Date(user.temporaryConditions[i].startDate*1000),
                              new Date (user.temporaryConditions[i].startDate*1000));
    }

    this.setState({
      userID : id,
      name: user.name,
      lastname: user.lastname,
      seniority: (user.seniority === "SPECIALIST_JUNIOR" ? "Medico specializzando junior" :
        (user.seniority === "SPECIALIST_SENIOR") ? "Medico specializzando senior" : "Medico strutturato"),
      email: user.email,
      birthday: user.birthday,
      systemActors : systemActorsUserItalianTranslation,
      specializations : user.specializations,
      conditions: conditionsToShow
    })
  }

  async handleDeleteRotazioneLoggedUser(idRotazione, key) {
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    responseStatus = await categoriaUtenteApi.deleteRotazione(idRotazione,  this.state.userID);

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

  async handlerDeleteCategoriaStatoLoggedUser(idRotazione, key) {
    console.log(idRotazione + key )
    let categoriaUtenteApi = new CategoriaUtenteAPI();
    let responseStatus;
    responseStatus = await categoriaUtenteApi.deleteStato(idRotazione, this.state.userID);
    console.log(responseStatus)

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

    /*function getTurnazioniSpecializzando() {
        return <MDBCol>
          <MDBCard>
            <MDBCardBody className="text-center">
              <MDBCardTitle>Rotazioni <AggiungiCategoria onPostAssegnazione = {()=>{this.componentDidMount()}}/> </MDBCardTitle>
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
    }*/

    function getDoctorCondition() {
      return <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>Categorie utente</MDBCardTitle>
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
              {this.state.conditions.map( (data) => {
                const options = {
                  timeZone: 'Europe/Berlin',
                  weekday: 'long',
                  day: "numeric",
                  month: 'long',
                  year: 'numeric',
                };
                var stardDateToShow = data.startDate.toLocaleString('it-IT', options);
                var endDateToShow = data.endDate.toLocaleString('it-IT', options);

                // Parse Strings in result to get well formatted ones
                stardDateToShow = formatStringUpperLower(stardDateToShow);
                endDateToShow = formatStringUpperLower(endDateToShow);

                let numericList = ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"];
                var minValue = stardDateToShow.length;

                // Find the index of the first number
                for(var i = 0;i<numericList.length;i++){
                  if(stardDateToShow.indexOf(numericList[i]) !== -1 && minValue > stardDateToShow.indexOf(numericList[i])){
                    minValue = stardDateToShow.indexOf(numericList[i]);
                  }
                }

                //Find the index of the space after the first number
                var indexOfSecondSpace = stardDateToShow.substring(minValue,stardDateToShow.length).indexOf(' ') + minValue + 1;
                stardDateToShow = stardDateToShow.substring(0,indexOfSecondSpace) + stardDateToShow.substring(indexOfSecondSpace,indexOfSecondSpace+1).toUpperCase() + stardDateToShow.substring(indexOfSecondSpace+1,stardDateToShow.length);

                // Same process for the end date
                minValue = endDateToShow.length;

                // Find the index of the first number
                for(var i = 0;i<numericList.length;i++){
                  if(endDateToShow.indexOf(numericList[i]) !== -1 && minValue > endDateToShow.indexOf(numericList[i])){
                    minValue = endDateToShow.indexOf(numericList[i]);
                  }
                }

                //Find the index of the space after the first number
                indexOfSecondSpace = endDateToShow.substring(minValue,endDateToShow.length).indexOf(' ') + minValue + 1;
                endDateToShow = endDateToShow.substring(0,indexOfSecondSpace) + endDateToShow.substring(indexOfSecondSpace,indexOfSecondSpace+1).toUpperCase() + endDateToShow.substring(indexOfSecondSpace+1,endDateToShow.length);

                return (
                  <tr>
                    <td>{formatStringUpperLower(data.label)}</td>
                    <td>{stardDateToShow}</td>
                    <td>{endDateToShow}</td>
                    <td><IconButton aria-label="delete" onClick={() => this.handlerDeleteCategoriaStatoLoggedUser(this.state.systemActors)}>
                      <DeleteIcon />
                    </IconButton></td>
                  </tr>
                );
              })
              }
            </MDBTableBody>
          </MDBTable>
          <AggiungiCategoriaStato onPostAssegnazione = {()=>{this.componentDidMount() ;}} />
        </MDBCardBody>
      </MDBCard>;
    }


    /**
     * Utils function to show correctly formatted string (E.g. House, Lesson, ecc.. not like HOUSE or house
     * @param stringToFormat The string we want to format
     * @returns {string} The formatted string
     */
    function formatStringUpperLower(stringToFormat){
      return stringToFormat.toString().substring(0,1).toUpperCase() + stringToFormat.toString().substring(1,stringToFormat.toString().length).toLowerCase();
    }

    function showMultipleDataInSingleLine(data){
      var formattedText = "";
      if(data.length > 0){
        formattedText = formattedText + formatStringUpperLower(data[0]);
      }

      for(var i = 1; i<data.length;i++){
        formattedText = formattedText + ", " + formatStringUpperLower(data[i]);
      }
      return (<p>{formattedText}</p>);
    }

    function showSpecializations(specializations){
      return (
        <MDBRow>
          <MDBCol sm="3">
            <MDBCardText>Specializzazioni</MDBCardText>
          </MDBCol>
        <MDBCol sm="9">
          <MDBCardText
            className="text-muted">{showMultipleDataInSingleLine(specializations)}</MDBCardText>
          </MDBCol>
        </MDBRow>);
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
                    <p className="text-muted mb-1">{this.state.name + " " + this.state.lastname}</p>
                    <p className="text-muted mb-4">{this.state.seniority}</p>
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
              <MDBCol lg="8">
                <MDBCard className="mb-4">
                  <MDBCardBody>
                    <MDBCardTitle>
                      <div style={{display:"flex", justifyContent:"space-between"}}>
                        <div>
                          <MDBCardText>
                            Informazioni utente
                          </MDBCardText>
                        </div>
                        <div style={{marginLeft: "auto"}}>
                            <Button className="overlay" href={"/modify-single-user-profile"}><i className="fas fa-edit fa-lg"> </i></Button>
                        </div>
                      </div>
                    </MDBCardTitle>

                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Nome</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText className="text-muted">
                          {this.state.name}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Cognome</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.lastname}</MDBCardText>
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
                          className="text-muted">{this.state.birthday}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Anzianità</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.seniority}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    {(this.state.seniority==="Medico strutturato") && showSpecializations(this.state.specializations)}
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>Ruoli nel sistema</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{showMultipleDataInSingleLine(this.state.systemActors)}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
            </MDBRow>
            <MDBRow>
              {/* (this.state.seniority!=="Medico strutturato") && getTurnazioniSpecializzando.call(this)*/}
              <MDBCol>
                {getDoctorCondition.call(this)}
              </MDBCol>
            </MDBRow>
          </MDBContainer>
        </section>
      );
    }
}
