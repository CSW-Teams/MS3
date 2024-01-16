import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer, MDBRow
} from "mdb-react-ui-kit";
import TextField from "@mui/material/TextField";
import {Button} from "@mui/material";
import {UserAPI} from "../../API/UserAPI";
import {ConditionsToShow} from "./SingleUserProfileView";


export default class ModifyUserProfileView extends React.Component {

  goBack = () => {
    const { history } = this.props;
    history.push('/single-user-profile');
  };


  constructor(props){
    super(props);
    this.state = {
      name: "",
      lastname: "",
      birthday: "",
      taxCode: "",
      seniority: "",
      email: "",
      password: "",
      systemActors: [],
      errorState: false,
    }
    this.handleSubmit= this.handleSubmit.bind(this);
  }


  async componentDidMount() {
    let id = localStorage.getItem("id");
    let user = await(new UserAPI().getSingleUserProfileDetails(id));
    let systemActorsUserItalianTranslation = [];
    let conditionsToShow = [];
    let formattedSpecialization = [];
    var i;

    for(i = 0;i < user.systemActors.length;i++){
      systemActorsUserItalianTranslation[i] =
        (user.systemActors[i] === "PLANNER" ? "Pianificatore" :
          (user.systemActors[i] === "DOCTOR" ? "Dottore" : "Configuratore"));
    }

    function formatStringUpperLower(stringToFormat){
      return stringToFormat.toString().substring(0,1).toUpperCase() + stringToFormat.toString().substring(1,stringToFormat.toString().length).toLowerCase();
    }


    for(i = 0;i < user.specializations.length;i++){
      formattedSpecialization[i] = formatStringUpperLower(user.specializations[i]);
    }

    for(i = 0;i<user.permanentConditions.length;i++){
      conditionsToShow[i] = new ConditionsToShow(user.permanentConditions[i], "", "");
    }

    for(i = 0 ;i<user.temporaryConditions.length;i++){
      conditionsToShow[i + user.permanentConditions.length] = new ConditionsToShow(user.temporaryConditions[i].label,
        new Date(user.temporaryConditions[i].startDate*1000),
        new Date (user.temporaryConditions[i].startDate*1000));
    }

    function showMultipleDataInSingleLine(data){
      var formattedText = "";
      if(data.length > 0){
        formattedText = formattedText + formatStringUpperLower(data[0]);
      }

      for(var i = 1; i<data.length;i++){
        formattedText = formattedText + ", " + formatStringUpperLower(data[i]);
      }
      return formattedText;
    }

    this.setState({
      userID : id,
      name: user.name,
      lastname: user.lastname,
      seniority: (user.seniority === "SPECIALIST_JUNIOR" ? "Medico specializzando junior" :
        (user.seniority === "SPECIALIST_SENIOR") ? "Medico specializzando senior" : "Medico strutturato"),
      email: user.email,
      birthday: user.birthday,
      systemActors : showMultipleDataInSingleLine(systemActorsUserItalianTranslation),
      specializations : showMultipleDataInSingleLine(formattedSpecialization),
      conditions: conditionsToShow
    })
    console.log(this.state.specializations);
    console.log(this.state.systemActors);


  }




  async handleSubmit(e) {

    e.preventDefault();

    // Manda una HTTP Post al backend
    let loginAPI = new LoginAPI();

    const data = {
      name: this.state.name,
    }

    if (!this.state.systemActors.includes("DOCTOR")) {
      delete this.state.seniority;
    }

    let httpResponse = await loginAPI.postRegistration(this.state);



    let responseStatusClass = Math.floor(httpResponse.status / 100) // Grazie Fede

    switch (responseStatusClass) {
      case 2:
        // Success
        toast.success('Utente registrato con successo!', {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        this.goBack();

        break;
      default:
        toast.error('Registrazione Fallita. Riprova inserendo i dati corretti.', {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        break;
    }
  }

  render() {
    let conditionButton;
    if (typeof this.state.specializations != "undefined") {
      conditionButton =
        <div style={{paddingBottom: "20px", alignSelf:"center"}}>
        <Button
          variant="contained"
          disabled={false}
        >
          Visualizza Condizioni
        </Button>
      </div>;
    } else {
      conditionButton = <div></div>;
    }
    return (
      <section style={{backgroundColor: '#eee'}}>
        <MDBContainer className="py-5" style={{maxWidth:"50%"}}>
          <MDBCard alignment='center'>
            <MDBCardBody>
              <MDBCardTitle>Modifica Profilo</MDBCardTitle>
              <MDBRow>
                <div style={{
                  display: "block",
                  justifyContent: "",
                  paddingTop: 20
                }}>
                  <TextField
                    disabled
                    label="Nome"
                    fullWidth
                    value={this.state.name}
                    style={{marginBlock:10}}>
                  </TextField>
                  <TextField
                    disabled
                    label="Cognome"
                    value={this.state.lastname}
                    fullWidth
                    style={{marginBlock:10}}>
                  </TextField>
                  <TextField
                    required
                    label="Email"
                    fullWidth
                    value={this.state.email}
                    onChange={(event) => {

                    }}
                    style={{marginBlock:10}}>

                  </TextField>
                  <TextField
                    disabled
                    label="Data di Nascità"
                    fullWidth
                    value={this.state.birthday}
                    style={{marginBlock:10}}>
                  </TextField>
                  <TextField
                    disabled
                    label="Anzianità"
                    fullWidth
                    value={this.state.seniority}
                    style={{marginBlock:10}}>
                  </TextField>
                  <TextField
                    disabled
                    label="Ruoli nel Sistema"
                    fullWidth
                    value={this.state.systemActors}
                    style={{marginBlock:10}}>
                  </TextField>
                  <TextField
                    label="Specializzazioni"
                    fullWidth
                    value={this.state.specializations}
                    style={{marginBlock:10}}>
                  </TextField>
                  {conditionButton}
                </div>
                <div style={{paddingBottom: "20px", alignSelf:"center"}}>
                  <Button
                    variant="contained"
                    disabled={false}
                  >
                    Salva Modifiche
                  </Button>
                </div>
              </MDBRow>
            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
        <ToastContainer/>
      </section>
    )
  }


}
