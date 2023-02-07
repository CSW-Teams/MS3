import React from "react"
import {LoginAPI} from "../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from "react-router-dom";


export default class LoginView extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      username: "",
      password: "",
    }
    this.handleSubmit= this.handleSubmit.bind(this);
  }


  handleChange(e) {
    // Aggiorna lo stato in base al cambiamento degli input
    const val = e.target.value;
    this.setState({
      [e.target.name] : val
    });
  }

  async handleSubmit(e) {
    e.preventDefault();

    // Manda una HTTP Post al backend
    let loginAPI = new LoginAPI();
    let httpResponse = await loginAPI.postLogin(this.state);

    /* Se l'autenticazione ha esito positivo, l'utente viene
       reindirizzato sul suo profilo, altrimenti viene mostrato
       un messaggio di errore.
     */
    let responseStatusClass = Math.floor(httpResponse.status / 100)

    switch (responseStatusClass) {
      case 2:
        // Success - Redirect e salvataggio dati di sessione
        const utente = await httpResponse.json();
        this.props.history.push({
          pathname: '/pianificazione-globale',
          state: utente,
        })
        localStorage.setItem("id", utente.id)
        localStorage.setItem("nome", utente.nome)
        localStorage.setItem("cognome", utente.cognome)

        break;
      default:
        toast.error('Autenticazione Fallita. Riprova inserendo le credenziali corrette.', {
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
      <div className="Auth-form-container">
        <form className="Auth-form">
          <div className="Auth-form-content">
            <h3 className="Auth-form-title">M3S Login</h3>
            <div className="form-group mt-3">
              <label>Indirizzo Email</label>
              <input
                name = "username"
                type="email"
                className="form-control mt-1"
                placeholder="Inserisci l'indirizzo email"
                value={this.state.username}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>Password</label>
              <input
                name="password"
                type="password"
                className="form-control mt-1"
                placeholder="Inserisci la password"
                value={this.state.password}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="d-grid gap-2 mt-3">
              <button onClick={this.handleSubmit} type="submit" className="btn btn-primary">
                Login
              </button>
            </div>
            <div className="form-check gap-2 mt-3">
              <input class="form-check-input" type="checkbox" value=""></input>
              Ricordami
            </div>
            <p className="forgot-password text-center mt-2">
              <a href="">Password Dimenticata?</a>
            </p>
          </div>
        </form>
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
      </div>
    )
  }

}
