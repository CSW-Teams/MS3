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
import {t} from "i18next";
import {panic} from "../../components/common/Panic";
import {ModifyUserProfileAPI} from "../../API/ModifyUserProfileAPI";


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
      id: localStorage.getItem("id"),
    }
    this.handleSubmit= this.handleSubmit.bind(this);
    this.saveProfileUpdates = this.saveProfileUpdates.bind(this);
  }

  async componentDidMount() {
    let user;
    let id;
    try {
      const urlParams = new URLSearchParams(window.location.search);
      id = urlParams.get('userID');
      if(id==null){
        panic()
        return;
      }
      user = await(new UserAPI().getSingleUserProfileDetails(id));
    } catch (err) {
      panic()
      return
    }
    let systemActorsUser = user.systemActors;
    let conditionsToShow = [];
    let formattedSpecialization = [];
    var i;

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
      seniority: t(user.seniority),
      email: user.email,
      birthday: user.birthday,
      systemActors : showMultipleDataInSingleLine(systemActorsUser),
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

    let httpResponse
    try {
      httpResponse = await loginAPI.postRegistration(this.state);
    } catch (err) {

      panic()
      return
    }


    let responseStatusClass = Math.floor(httpResponse.status / 100) // Grazie Fede

    switch (responseStatusClass) {
      case 2:
        // Success
        toast.success(t('User registered successfully'), {
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
        toast.error(t('Registration Failed'), {
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

  async saveProfileUpdates() {
    let modifyUserProfileAPI = new ModifyUserProfileAPI()
    let conf = {}
    conf = this.state

    let response
    try {
      response = await modifyUserProfileAPI.setUpdatedProfileInfos(conf)
    } catch (err) {
      panic()
      return
    }
    if (response.status === 202) {
      toast.success(t("Configuration saved successfully"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (response.status === 400) {
      toast.error(t("Error saving the configuration"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else {
      // todo: how to behave in case the response status is not 200?
      toast.error("STILL TO DECIDE FAILURE MESSAGE", {
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
    let conditionButton;
    if (typeof this.state.specializations != "undefined") {
      conditionButton =
        <div style={{paddingBottom: "20px", alignSelf:"center"}}>
        <Button
          variant="contained"
          disabled={false}
        >
          {t('View Conditions')}
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
              <MDBCardTitle>{t('Modify Profile')}</MDBCardTitle>
              <MDBRow>
                <div style={{
                  display: "block",
                  justifyContent: "",
                  paddingTop: 20
                }}>
                  <TextField
                    label={t('Name')}
                    fullWidth
                    value={this.state.name}
                    style={{marginBlock:10}}
                    onChange={(e) => this.setState({ name: e.target.value })}>
                  </TextField>
                  <TextField
                    label={t('Surname')}
                    value={this.state.lastname}
                    fullWidth
                    style={{marginBlock:10}}
                    onChange={(e) => this.setState({ lastname: e.target.value })}>
                  </TextField>
                  <TextField
                    label={t("Email Address")}
                    fullWidth
                    value={this.state.email}
                    style={{marginBlock:10}}
                    onChange={(e) => this.setState({ email: e.target.value })}>
                  </TextField>
                  <TextField
                    label={t('Birthdate')}
                    fullWidth
                    value={this.state.birthday}
                    style={{marginBlock:10}}
                    onChange={(e) => this.setState({ birthday: e.target.value })}>
                  </TextField>
                  <TextField
                    disabled
                    label={t('Seniority')}
                    fullWidth
                    value={this.state.seniority}
                    style={{marginBlock:10}}
                    onChange={(e) => this.setState({ seniority: e.target.value })}>
                  </TextField>
                  {/*<TextField
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
                  {conditionButton}*/}
                </div>
                <div style={{paddingBottom: "20px", alignSelf:"center"}}>
                  <Button
                    variant="contained"
                    onClick={this.saveProfileUpdates}
                  >
                    {t('Save Changes')}
                  </Button>
                </div>
              </MDBRow>
            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
      </section>
    )
  }


}
