import React from 'react';
import {
  MDBInput,
  MDBRow,
  MDBCol,
  MDBContainer,
  MDBCard,
  MDBCardBody,
  MDBCardTitle
} from 'mdb-react-ui-kit';

import {toast, ToastContainer} from "react-toastify";
import {VincoloAPI} from "../../API/VincoliAPI";
import { t } from "i18next";
import {panic} from "../../components/common/Panic";
import {Button} from "@mui/material";

export default class ConfigurazioneVincoli extends React.Component{

  constructor(props){
    super(props);
    this.state = {
      horizonNightShift:"",
      periodDaysNo:"",
      periodMaxTime:"",
      maxConsecutiveTimeForEveryone:"",
      maxConsecutiveTimeForOver62:"",
      maxConsecutiveTimeForPregnant:"",
      categoriaOver62:"",
      categoriaDonneIncinta:"",

    }

    this.componentDidMount = this.componentDidMount.bind(this);
    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSalvataggio = this.handleSalvataggio.bind(this);
  }

  async componentDidMount() {
    let conf
    try {
      conf = await(new VincoloAPI().getConfigurazioneVincoli())
    } catch (err) {
      panic()
      return
    }
    let over62={};
    let donnaIncinta={};
    for(let i=0;i<conf.configVincMaxPerConsPerCategoria.length;i++){
      if(conf.configVincMaxPerConsPerCategoria[i].constrainedCondition.type=='INCINTA'){
        donnaIncinta.categoria=conf.configVincMaxPerConsPerCategoria[i].constrainedCondition;
        donnaIncinta.maxOre=conf.configVincMaxPerConsPerCategoria[i].numMaxOreConsecutive;
      }else{
        over62.categoria=conf.configVincMaxPerConsPerCategoria[i].constrainedCondition;
        over62.maxOre=conf.configVincMaxPerConsPerCategoria[i].numMaxOreConsecutive;
      }
    }
    this.setState({
      horizonNightShift: conf.horizonNightShift,
      periodDaysNo: conf.periodDaysNo,
      periodMaxTime: conf.periodMaxTime,
      maxConsecutiveTimeForEveryone: conf.maxConsecutiveTimeForEveryone,
      maxConsecutiveTimeForOver62: over62.maxOre,
      maxConsecutiveTimeForPregnant: donnaIncinta.maxOre,
      categoriaOver62:over62.categoria,
      categoriaDonneIncinta:donnaIncinta.categoria,
    })
  }

  async handleSalvataggio() {
    let vincoliApi = new VincoloAPI()
    let conf = {}
    conf = this.state

    let response
    try {
      response = await vincoliApi.setConfigurazioneVincoli(conf)
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

  handleInputChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;

    this.setState({
      [name]: value
    });
  }

  render() {
    return (
        <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">
          <MDBCard alignment="center">
            <MDBCardBody >
              <MDBCardTitle style={{
                marginBottom: '30px'}}>
                {t("Constraint parameters management")}
              </MDBCardTitle>

              {/* Continuous shifts horizon */}
              <MDBRow className="align-items-center"
                      style={{paddingBottom: '10px'}}>
                <MDBCol size="6" className="text-start">
                  {t("Number of non-assignable hours before and after the night shift")}
                </MDBCol>
                <MDBCol size="6">
                  <MDBInput
                    type="text"
                    name="horizonNightShift"
                    value={this.state.horizonNightShift}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <hr style={{
                borderTop: '3px solid #000',
                marginTop: '5px',
                marginBottom: '13px'
              }}/>

              {/* Max number of hours in a period */}
              <MDBRow className="align-items-center"
                      style={{paddingBottom: '10px'}}>
                <MDBCol size="6" className="text-start">
                  {t("Max number of hours in a period")}
                </MDBCol>
                <MDBCol size="6">
                  <MDBInput
                    type="text"
                    label={t("Period in days")}
                    name="periodDaysNo"
                    value={this.state.periodDaysNo}
                    onChange={this.handleInputChange}
                  />
                  <MDBInput
                    type="text"
                    label={t("Max number of hours in the period")}
                    name="periodMaxTime"
                    value={this.state.periodMaxTime}
                    onChange={this.handleInputChange}
                    style={{marginTop: '10px'}} // Spaziatura tra i campi
                  />
                </MDBCol>
              </MDBRow>
              <hr style={{
                borderTop: '3px solid #000',
                marginTop: '5px',
                marginBottom: '13px'
              }}/>

              {/* Max consecutive hours */}
              <MDBRow className="align-items-center"
                      style={{paddingBottom: '10px'}}>
                <MDBCol size="6" className="text-start">
                  {t("Max consecutive hours")}
                </MDBCol>
                <MDBCol size="6">
                  <MDBInput
                    type="text"
                    name="maxConsecutiveTimeForEveryone"
                    value={this.state.maxConsecutiveTimeForEveryone}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <hr style={{
                borderTop: '3px solid #000',
                marginTop: '5px',
                marginBottom: '13px'
              }}/>

              {/* Max hours for Over 62 */}
              <MDBRow className="align-items-center"
                      style={{paddingBottom: '10px'}}>
                <MDBCol size="6" className="text-start">
                  {t("Max hours for Over 62")}
                </MDBCol>
                <MDBCol size="6">
                  <MDBInput
                    type="text"
                    name="maxConsecutiveTimeForOver62"
                    value={this.state.maxConsecutiveTimeForOver62}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <hr style={{
                borderTop: '3px solid #000',
                marginTop: '5px',
                marginBottom: '13px'
              }}/>

              {/* Max hours for pregnant women */}
              <MDBRow className="align-items-center"
                      style={{paddingBottom: '10px'}}>
                <MDBCol size="6" className="text-start">
                  {t("Max hours for pregnant women")}
                </MDBCol>
                <MDBCol size="6">
                  <MDBInput
                    type="text"
                    name="maxConsecutiveTimeForPregnant"
                    value={this.state.maxConsecutiveTimeForPregnant}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>

              {/* Save Button */}
              <MDBRow>
                <MDBCol className="text-end" style={{paddingTop: '20px'}}>
                  <Button
                    variant="contained"
                    onClick={this.handleSalvataggio}
                  >
                    {t('Save')}
                  </Button>
                </MDBCol>
              </MDBRow>
            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
    );
  }

}
