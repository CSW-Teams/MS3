import React from 'react';
import {
  MDBInput,
  MDBRow,
  MDBCol,
  MDBContainer,
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBBtn
} from 'mdb-react-ui-kit';

import {toast, ToastContainer} from "react-toastify";
import {VincoloAPI} from "../../API/VincoliAPI";
import { t } from "i18next";
import {panic} from "../../components/common/Panic";

export default class ConfigurazioneVincoli extends React.Component{

  constructor(props){
    super(props);
    this.state = {
      numGiorniPeriodo:"",
      maxOrePeriodo:"",
      horizonTurnoNotturno:"",
      numMaxOreConsecutivePerTutti:"",
      categoriaOver62:"",
      categoriaDonneIncinta:"",
      numMaxOreConsecutiveOver62:"",
      numMaxOreConsecutiveDonneIncinta:"",

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
      numGiorniPeriodo: conf.periodDaysNo,
      maxOrePeriodo: conf.periodMaxTime,
      horizonTurnoNotturno: conf.horizonNightShift,
      numMaxOreConsecutivePerTutti: conf.maxConsecutiveTimeForEveryone,
      numMaxOreConsecutiveOver62: over62.maxOre,
      numMaxOreConsecutiveDonneIncinta: donnaIncinta.maxOre,
      categoriaOver62:over62.categoria,
      categoriaDonneIncinta:donnaIncinta.categoria,
    })
  }

  async handleSalvataggio(){
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
      <section>
        <MDBContainer className="py-5">
          <MDBCard alignment='center'>
            <MDBCardBody style={{height: '80vh'}}>
              <MDBCardTitle>{t("Constraint parameters management")}</MDBCardTitle>
              <MDBRow className='g-3' style={{paddingTop: '10px'}}>
                <MDBCol>{t("Continuous shifts horizon")}
                </MDBCol>
                <MDBCol size='sm'>
                <MDBInput
                  wrapperClass='col-auto'
                  type='text'
                  name='horizonTurnoNotturno'
                  label={t("Number of non-assignable hours before and after the night shift")}
                  id='formTextExample2'
                  aria-describedby='textExample2'
                  value={this.state.horizonTurnoNotturno}
                  onChange={this.handleInputChange}
                />
                </MDBCol>
              </MDBRow>
              <MDBRow style={{paddingTop: '10px'}}>
                <MDBCol>{t("Max number of hours in a period")}
                </MDBCol>
                <MDBCol>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='text'
                    label={t("Period in days")}
                    name='numGiorniPeriodo'
                    id='formTextExample2'
                    aria-describedby='textExample2'
                    value={this.state.numGiorniPeriodo}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
                <MDBCol>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='text'
                    label={t("Max number of hours in the period")}
                    id='formTextExample2'
                    name='maxOrePeriodo'
                    aria-describedby='textExample2'
                    value={this.state.maxOrePeriodo}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow style={{paddingTop: '10px'}}>
                <MDBCol>{t("Max consecutive hours")}
                </MDBCol>
                <MDBCol>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='text'
                    label={t("Number of consecutive hours")}
                    id='formTextExample2'
                    name='numMaxOreConsecutivePerTutti'
                    aria-describedby='textExample2'
                    value={this.state.numMaxOreConsecutivePerTutti}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow style={{paddingTop: '10px'}}>
                <MDBCol>{t("Max hours for Over 62")}
                </MDBCol>
                <MDBCol>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='text'
                    label={t("Number of consecutive hours")}
                    name='numMaxOreConsecutiveOver62'
                    id='formTextExample2'
                    aria-describedby='textExample2'
                    value={this.state.numMaxOreConsecutiveOver62}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow style={{paddingTop: '10px'}}>
                <MDBCol>{t("Max hours for pregnant women")}
                </MDBCol>
                <MDBCol>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='text'
                    label={t("Number of consecutive hours")}
                    name='numMaxOreConsecutiveDonneIncinta'
                    id='formTextExample2'
                    aria-describedby='textExample2'
                    value={this.state.numMaxOreConsecutiveDonneIncinta}
                    onChange={this.handleInputChange}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBCol style={{paddingTop: '10px'}}>
                  <MDBBtn onClick={this.handleSalvataggio}>{t("Save")}</MDBBtn>
                </MDBCol>
              </MDBRow>

            </MDBCardBody>
          </MDBCard>
        </MDBContainer>

      </section>
    )
  }


}
