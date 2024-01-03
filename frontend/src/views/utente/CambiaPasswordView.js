import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBBtn,
  MDBCard,
  MDBCardBody,
  MDBCardTitle, MDBCol,
  MDBContainer, MDBInput, MDBRow, MDBTypography
} from "mdb-react-ui-kit";


export default class CambiaPasswordView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      id: localStorage.getItem("id"),
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
    let httpResponse = await loginAPI.postPassword(this.state);

    /* Se la modifica della password ha esito positivo, viene
       mostrato un toast... altrimenti viene mostrato
       un altro toast :)
     */
    let responseStatusClass = Math.floor(httpResponse.status / 100)

    switch (responseStatusClass) {
      case 2:
        // Success
        toast.success('Password modificata con successo!', {
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

        break;
      default:
        // Failure
        toast.error('Errore nel cambio della password. Assicurati che la password attuale sia corretta e che la nuova password rispetti le policy di sicurezza definite dal configuratore.', {
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
              <MDBCardTitle>Benvenuto in cambia la tua Password!</MDBCardTitle>
              <MDBRow className='g-3' style={{paddingTop: '10px'}}>
                <MDBCol>Password Attuale:
                </MDBCol>
                <MDBCol size='sm'>
                  <MDBInput
                    wrapperClass='col-auto'
                    type='password'
                    name='oldPassword'
                    placeholder="Inserisci qui la tua Password..."
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
                    placeholder="Inserisci qui la tua nuova password..."
                    value={this.state.newPassword}
                    onChange={e => this.handleChange(e)}
                  />
                </MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBCol style={{paddingTop: '10px'}}>
                  <MDBTypography><mark>Attenzione!</mark> Ricordati di inserire caratteri minuscoli, maiuscoli, numeri e caratteri speciali.</MDBTypography>
                </MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBCol style={{paddingTop: '0px'}}>
                  <button onClick={this.handleSubmit} type="submit" className="btn btn-primary">Salva</button>
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

