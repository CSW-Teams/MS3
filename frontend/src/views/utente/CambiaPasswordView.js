import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle, MDBCol,
  MDBContainer, MDBInput, MDBRow, MDBTypography
} from "mdb-react-ui-kit";
import {t} from "i18next";
import {panic} from "../../components/common/Panic";
import routes from "../../routes";
import link from "react-router-dom/Link";

// sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}



export default class CambiaPasswordView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      userId: localStorage.getItem("id"),
      oldPassword: "",
      newPassword: "",
    }
    this.handleSubmit = this.handleSubmit.bind(this);
  }


  handleChange(e) {
    // Aggiorna lo stato in base al cambiamento degli input
    const val = e.target.value;
    this.setState({
      [e.target.name]: val
    });
  }



  async handleSubmit(e) {
    e.preventDefault();

    // Manda una HTTP Post su api/password/
    let loginAPI = new LoginAPI();
    let httpResponse
    try {
      httpResponse = await loginAPI.postPassword(this.state);
    } catch (err) {

      panic()
      return
    }

    /* Se la modifica della password ha esito positivo, viene
       mostrato un toast... altrimenti viene mostrato
       un altro toast :)
     */
    let responseStatusClass = Math.floor(httpResponse.status / 100)

    switch (responseStatusClass) {
      case 2:
        // Success
        toast.success(t('Password updated successfully'), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        sleep(1000).then(() => {
          window.location.href = "/pianificazione-globale"
        });
        break;
      default:
        // Failure
        toast.error(t('Invalid Password'), {
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
    return (
      <section>
        <MDBContainer className="py-5">
          <MDBCard alignment='center'>
            <MDBCardBody style={{height: '70vh'}}>
              <MDBCardTitle>{t('Change your password')}</MDBCardTitle>
              <MDBRow className='g-3' style={{paddingTop: '10px'}}>
                <MDBCol>{t('Current Password')}
                </MDBCol>
                <MDBCol size='sm'>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='password'
                    name='oldPassword'
                    placeholder={t('Insert your password here')}
                    value={this.state.oldPassword}
                    onChange={e => this.handleChange(e)}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow className='g-3' style={{paddingTop: '10px'}}>
                <MDBCol>Nuova Password:
                </MDBCol>
                <MDBCol size='sm'>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='password'
                    name='newPassword'
                    placeholder={t('Insert your new password here')}
                    value={this.state.newPassword}
                    onChange={e => this.handleChange(e)}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBCol style={{paddingTop: '10px'}}>
                  <MDBTypography>{
                    t('Remember to use lowercase, uppercase characters, numbers and special characters')}</MDBTypography>
                </MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBCol style={{paddingTop: '0px'}}>
                  <button onClick={this.handleSubmit} type="submit" className="btn btn-primary">{t('Save')}</button>
                </MDBCol>
              </MDBRow>
            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
      </section>
    )
  }
}
