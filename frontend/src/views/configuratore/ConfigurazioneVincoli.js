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
    let conf = await(new VincoloAPI().getConfigurazioneVincoli())
    this.setState({
      numGiorniPeriodo: conf.numGiorniPeriodo,
      maxOrePeriodo: conf.maxOrePeriodo,
      horizonTurnoNotturno: conf.horizonTurnoNotturno,
      numMaxOreConsecutivePerTutti: conf.numMaxOreConsecutivePerTutti,
      numMaxOreConsecutiveOver62: conf.configVincoloMaxPeriodoConsecutivoPerCategoria[0].numMaxOreConsecutive,
      numMaxOreConsecutiveDonneIncinta: conf.configVincoloMaxPeriodoConsecutivoPerCategoria[1].numMaxOreConsecutive,
      categoriaOver62:conf.configVincoloMaxPeriodoConsecutivoPerCategoria[0].categoriaVincolata,
      categoriaDonneIncinta:conf.configVincoloMaxPeriodoConsecutivoPerCategoria[1].categoriaVincolata,
    })
  }

  async handleSalvataggio(){
    let vincoliApi = new VincoloAPI()
    let conf = {}
    conf = this.state

    let response = await vincoliApi.setConfigurazioneVincoli(conf)
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
    console.log(value)
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
        <ToastContainer
          position="top-center"
          autoClose={5000}
          hideProgressBar={true}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />

      </section>
    )
  }


}
