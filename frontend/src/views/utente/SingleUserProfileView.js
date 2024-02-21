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
import DoctorConditionAdditionDrawer
  from "../../components/common/BottomViewAddCondition";
import AddSpecialization
  from "../../components/common/BottomViewAddSpecialization";
import AddSystemActor
    from "../../components/common/BottomViewAddSystemActor";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import {toast} from "react-toastify";
import {Button} from "@material-ui/core";
import {SingleUserProfileAPI} from "../../API/SingleUserProfileAPI";
import {t} from "i18next";
import {panic} from "../../components/common/Panic";

/**
 * Class needed to fromat correctly the attributes permanentConditions and temporaryConditions
 * coming from JSON file obtained by API call
 */
export class ConditionsToShow {

  constructor(label,id,startDate, endDate,isPermanent){
    this.label = label;
    this.conditionID = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isPermanent = isPermanent;
  }
}



export default class SingleUserProfileView extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      userID : -1,
      name: "",
      lastname: "",
      seniority: "",
      email: "",
      birthday: "",
      systemActors: [],
      specializations: [],
      conditions:[],
      isPlanner :false,
      specializationList:[],
      conditionsList:[],
      allSystemActors:[],
      userToView:-1
    }

  }

  async componentDidMount() {
    let id
    let loggedID
    let loggedUser
    let user
    let singleUserProfileAPI
    let specializations
    let conditionsToShow
    let isPlanner
    let allSavedSystemActors
    let allSavedSystemActorsInItalian
    let allSavedConditions
    try {
      id = this.props.match.params.idUser*1;
      loggedID = localStorage.getItem("id");
      user = await(new UserAPI().getSingleUserProfileDetails(id));
      loggedUser = await(new UserAPI().getSingleUserProfileDetails(loggedID));
      singleUserProfileAPI = new SingleUserProfileAPI();
      specializations = await singleUserProfileAPI.getSpecializations();
      conditionsToShow = [];
      isPlanner = false;
      allSavedSystemActors = await singleUserProfileAPI.getSystemActors();
      allSavedConditions = await singleUserProfileAPI.getAllConditionSaved();
    } catch (err) {

      panic()
      return
    }

    /* Used when going to your own profile*/
    /*if(this.props.match.params.idUser === undefined){
      id = loggedID;
    }*/


    for(var i = 0;i < loggedUser.systemActors.length;i++){
      if(loggedUser.systemActors[i] === "PLANNER"){
        isPlanner = true;
      }
    }



    for(var i = 0;i<user.permanentConditions.length;i++){
      conditionsToShow[i] = new ConditionsToShow(user.permanentConditions[i].label, user.permanentConditions[i].conditionID,"", "",true);
    }

    for(var i = 0 ;i<user.temporaryConditions.length;i++){
      conditionsToShow[i + user.permanentConditions.length] = new ConditionsToShow(
                              user.temporaryConditions[i].label,
                              user.temporaryConditions[i].conditionID,
                              new Date(user.temporaryConditions[i].startDate*1000),
                              new Date (user.temporaryConditions[i].startDate*1000),
                              false );
    }

    this.setState({
      userID : user.id,
      name: user.name,
      lastname: user.lastname,
      seniority: user.seniority,
      email: user.email,
      birthday: user.birthday,
      systemActors : user.systemActors,
      specializations : user.specializations,
      conditions: conditionsToShow,
      isPlanner: isPlanner,
      specializationList: specializations,
      conditionsList: allSavedConditions,
      allSystemActors:allSavedSystemActors
    });


  }

  updateConditionList = (newConditions) => {
      toast.success(t('Conditions modified successfully'), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      this.setState({conditions:newConditions});
    };

    updateSpecializationList = (newSpecializations) => {
        toast.success(t('Specializations modified successfully'), {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
        });
        this.setState({specializations:newSpecializations});
    };

  updateSystemActorsList = (newSystemActor) => {
    toast.success(t('System Roles modified successfully'), {
      position: "top-center",
      autoClose: 5000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
    });
    this.setState({systemActors:newSystemActor});
  };

  async handleDeleteSpecialization(doctorID, specialization) {
    let singleUserProfileAPI = new SingleUserProfileAPI();
    let responseStatus
    try {
      responseStatus = await singleUserProfileAPI.deleteSpecialization(doctorID,  specialization);
    } catch (err) {

      panic()
      return
    }

    if (responseStatus === 200) {
      toast.success(t('Specialization deleted successfully'), {
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
      toast.error(t('Error during removal'), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }else{
      toast.error(t('Something went wrong, try again later'), {
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

  async handleDeleteSystemActor(doctorID, systemActor) {
    let singleUserProfileAPI = new SingleUserProfileAPI();
    let responseStatus
    try {
      responseStatus = await singleUserProfileAPI.deleteSystemActor(doctorID,  systemActor);
    } catch (err) {

      panic()
      return
    }

    if (responseStatus === 200) {
      toast.success(t('System actor deleted successfully'), {
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
      toast.error(t('Error during removal'), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }else{
      toast.error(t('Something went wrong, try again later'), {
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

  funcToSetUserToView(){
    console.log(this.state.userID)
  }

  async handleDeleteCondition(doctorID, conditionID,condition,startDate) {
    let singleUserProfileAPI = new SingleUserProfileAPI();
    let responseStatus;

    try {
      if(startDate === "" || startDate === null){
        responseStatus = await singleUserProfileAPI.deletePermanentCondition(doctorID,conditionID,condition);
      }else{
        responseStatus = await singleUserProfileAPI.deleteTemporaryCondition(doctorID,conditionID,condition);
      }
    } catch (err) {
      panic()
      return
    }

    if (responseStatus === 200) {
      toast.success(t('Condition deleted successfully'), {
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
      toast.error(t('Error during removal'), {
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

    function renderDoctorCondition() {
      return <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>{t('User Categories')}</MDBCardTitle>
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
              {this.state.conditions.map((data) => {
                const options = {
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
                    {this.state.isPlanner && <td><IconButton aria-label="delete" onClick={() => this.handleDeleteCondition(this.state.userID,data.conditionID,data.label,stardDateToShow)}> {/* start date is passed to check whether a condition is permanent or temporary*/}
                      <DeleteIcon />
                    </IconButton></td>}
                  </tr>
                );
              })
              }
            </MDBTableBody>
          </MDBTable>
          {this.state.isPlanner &&
            <DoctorConditionAdditionDrawer updateFunction = {this.updateConditionList}  doctorID={this.state.userID} conditions={this.state.conditions} currentConditionsList={this.state.conditionsList} />
          }
        </MDBCardBody>
      </MDBCard>;
    }


    function renderDoctorSpecializations() {
      return <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>{t('Specializations')}</MDBCardTitle>
          <MDBTable align="middle">

            <MDBTableBody>
              {this.state.specializations.map( (data) => {
                return (
                  <tr>
                    <td>{formatStringUpperLower(data)}</td>
                    {this.state.isPlanner && <td><IconButton aria-label="delete" onClick={() => this.handleDeleteSpecialization(this.state.userID,data)}>
                      <DeleteIcon />
                    </IconButton></td>}
                  </tr>
                );
              })
              }
            </MDBTableBody>
          </MDBTable>
          {this.state.isPlanner &&
              <AddSpecialization updateFunction = {this.updateSpecializationList}  doctorID={this.state.userID} specializations={this.state.specializationList} updatedSpecializationList={this.state.specializations}/>
          }
        </MDBCardBody>
      </MDBCard>;
    }


    function renderSystemActor() {
      return <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>{t('System Roles')}</MDBCardTitle>
          <MDBTable align="middle">

            <MDBTableBody>
              {this.state.systemActors.map( (data) => {
                return (
                  <tr>
                    <td>{formatStringUpperLower(t(data))}</td>
                    {this.state.isPlanner && <td><IconButton aria-label="delete" onClick={() => this.handleDeleteSystemActor(
                      this.state.userID,data
                    )}>
                      <DeleteIcon />
                    </IconButton></td>}
                  </tr>
                );
              })
              }
            </MDBTableBody>
          </MDBTable>
          {this.state.isPlanner &&
              <AddSystemActor updateFunction = {this.updateSystemActorsList}  userID={this.state.userID} systemActors={this.state.allSystemActors} updateSystemActorList={this.state.systemActors}/>
          }
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
            <MDBCardText>{t('Specialization')}</MDBCardText>
          </MDBCol>
          <MDBCol sm="9">
            <MDBCardText
              className="text-muted">{showMultipleDataInSingleLine(specializations)}</MDBCardText>
          </MDBCol>
        </MDBRow>);
    }

    console.log(this.state.isPlanner)
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
                    <p className="text-muted mb-4">{t(this.state.seniority)}</p>
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
                            {t('User Information')}
                          </MDBCardText>
                        </div>
                        { this.state.isPlanner && <div style={{marginLeft: "auto"}}>
                                <Button className="overlay" href={"/modify-single-user-profile?userID="+this.state.userID.toString()}onClick={this.funcToSetUserToView}><i className="fas fa-edit fa-lg">
                                </i></Button>
                            </div>
                        }
                      </div>
                    </MDBCardTitle>

                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('Name')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText className="text-muted">
                          {this.state.name}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('Lastname')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.lastname}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('Email Address')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.email}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('Birthdate')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{this.state.birthday}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('Seniority')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText
                          className="text-muted">{t(this.state.seniority)}</MDBCardText>
                      </MDBCol>
                    </MDBRow>
                    {(this.state.seniority==="STRUCTURED") && showSpecializations(this.state.specializations)}
                    <MDBRow>
                      <MDBCol sm="3">
                        <MDBCardText>{t('System Roles')}</MDBCardText>
                      </MDBCol>
                      <MDBCol sm="9">
                        <MDBCardText className="text-muted">
                          {this.state.systemActors.map((systemActor, index) => (
                            <span key={index}>
                              {t(systemActor)}
                              {index < this.state.systemActors.length - 1 && ', '}
                            </span>
                          ))}
                        </MDBCardText>
                      </MDBCol>
                    </MDBRow>
                  </MDBCardBody>
                </MDBCard>
              </MDBCol>
            </MDBRow>
            {this.state.isPlanner &&
              <MDBRow>
                <MDBCol>
                  {renderDoctorSpecializations.call(this)}
                </MDBCol>
                <MDBCol>
                  {renderSystemActor.call(this)}
                </MDBCol>
              </MDBRow>
            }
            <MDBRow style={{marginTop:"1%"}}>
              <MDBCol>
                {renderDoctorCondition.call(this)}
              </MDBCol>
            </MDBRow>
          </MDBContainer>
        </section>
      );
    }
}
