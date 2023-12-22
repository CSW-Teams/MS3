import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';


export default class LoginView extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      email: "",
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
    let responseStatusClass = Math.floor(httpResponse.status / 100) // Grazie Fede

    switch (responseStatusClass) {
      case 2:
        // Success - Redirect e salvataggio dati di sessione
        const user = await httpResponse.json();

        localStorage.setItem("id", user.id)
        localStorage.setItem("name", user.name)
        localStorage.setItem("lastname", user.lastname)
        localStorage.setItem("actor", user.actor)

        this.props.history.push({
          pathname: '/pianificazione-globale',
          state: user,
        })

        window.location.reload();

        break;
      default:
        const errorMessage = await httpResponse.text();
        toast.error(`Autenticazione Fallita. ${errorMessage}.`, {
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
            <h3 className="Auth-form-title">Login</h3>
            <div className="form-group mt-3">
              <label>Indirizzo Email</label>
              <input
                name = "email"
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
              <button onClick={this.handleSubmit} type="submit"
                      className="btn btn-primary">
                Login
              </button>
            </div>
            <div className="d-grid gap-2 mt-3">
              <label>Accedi come:</label>
              <select
                name="role"
                className="form-select mt-1"
                value={this.state.role}
                onChange={e => this.handleChange(e)}
              >
                {/* Placeholder. It should be a call on the backend */}
                <option value="DOCTOR">Dottore</option>
                <option value="CONFIGURATOR">Configuratore</option>
                <option value="PLANNER">Pianificatore</option>
              </select>
            </div>
            <div className="form-check gap-2 mt-3">
              <input className="form-check-input" type="checkbox" value=""></input>
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
