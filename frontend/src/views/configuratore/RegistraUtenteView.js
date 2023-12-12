import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { withRouter } from 'react-router-dom';

export default class LoginView extends React.Component {

  passaAllAltraVista = () => {
    const { history } = this.props;
    history.push('/info-utenti');
  };


  constructor(props){
    super(props);
    this.state = {
      nome: "",
      cognome: "",
      dataNascita: "",
      codiceFiscale: "",
      ruolo: "",
      email: "",
      password: "",
      attore: "",
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
    let httpResponse = await loginAPI.postRegistration(this.state);

    /* Se la registrazione del nuovo doctor ha esito positivo, viene
           mostrato un toast... altrimenti viene mostrato
           un altro toast :)
         */
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
        this.passaAllAltraVista();

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
    return (
      <div className="Auth-form-container">
        <form className="Auth-form">
          <div className="Auth-form-content">
            <h3 className="Auth-form-title">Registra un nuovo doctor</h3>
            <div className="form-group mt-3">
              <label>Nome</label>
              <input
                name="nome"
                type="text"
                className="form-control mt-1"
                placeholder="Inserisci il nome"
                value={this.state.nome}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>Cognome</label>
              <input
                name="cognome"
                type="text"
                className="form-control mt-1"
                placeholder="Inserisci il cognome"
                value={this.state.cognome}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>Codice fiscale</label>
              <input
                name="codiceFiscale"
                type="text"
                className="form-control mt-1"
                placeholder="Inserisci il codice fiscale"
                value={this.state.codiceFiscale}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>Data di Nascita</label>
              <input
                name="dataNascita"
                type="date"
                className="form-control mt-1"
                placeholder="Inserisci la data di nascita"
                value={this.state.dataNascita}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>Indirizzo Email</label>
              <input
                name="email"
                type="email"
                className="form-control mt-1"
                placeholder="Inserisci l'indirizzo email"
                value={this.state.email}
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
            <div className="form-group mt-3">
              <label>Ruolo</label>
              <div>
                <input
                  type="radio"
                  id="strutturato"
                  name="ruolo"
                  value="STRUTTURATO"
                  checked={this.state.ruolo === "STRUTTURATO"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="strutturato">Strutturato</label>
              </div>
              <div>
                <input
                  type="radio"
                  id="specializzando"
                  name="ruolo"
                  value="SPECIALIZZANDO"
                  checked={this.state.ruolo === "SPECIALIZZANDO"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="specializzando">Specializzando</label>
              </div>
            </div>
            <div className="form-group mt-3">
              <label>Attore</label>
              <div>
                <input
                  type="radio"
                  id="configuratore"
                  name="attore"
                  value="CONFIGURATORE"
                  checked={this.state.attore === "CONFIGURATORE"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="configuratore">Configuratore</label>
              </div>
              <div>
                <input
                  type="radio"
                  id="doctor"
                  name="attore"
                  value="UTENTE"
                  checked={this.state.attore === "UTENTE"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="doctor">Utente</label>
              </div>
              <div>
                <input
                  type="radio"
                  id="pianificatore"
                  name="attore"
                  value="PIANIFICATORE"
                  checked={this.state.attore === "PIANIFICATORE"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="pianificatore">Pianificatore</label>
              </div>
            </div>
            <div className="d-grid gap-2 mt-3">
              <button onClick={this.handleSubmit} type="submit" className="btn btn-primary">
                Registra
              </button>
            </div>
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
