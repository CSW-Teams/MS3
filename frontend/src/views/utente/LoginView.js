import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {t} from "i18next";
import {panic, PanicContext} from "../../components/common/Panic";

export default class LoginView extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      email: "",
      password: "",
      systemActor: "DOCTOR"
    }
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(e) {
    const val = e.target.value;
    const name = e.target.name;

    // Verifica se l'evento è scatenato da un input di testo o da un elemento select
    if (name === "systemActor") {
      this.setState({
        systemActor: val
      });
    } else {
      this.setState({
        [name]: val
      });
    }
  }

  async handleSubmit(e) {
    e.preventDefault();

    // Manda una HTTP Post al backend
    let loginAPI = new LoginAPI();
    let httpResponse
    try {
      httpResponse = await loginAPI.postLogin(this.state);
    } catch (err) {

      panic()
      return
    }

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
        localStorage.setItem("actor", user.systemActor)
        localStorage.setItem("jwt", user.jwt)

        this.props.history.push({
          pathname: '/pianificazione-globale',
          state: user,
        })

        window.location.reload();

        break;
      case 5:
        toast.error(`${t('Authentication Failed. Server not online')}`, {
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
      default:
        const errorMessage = await httpResponse.text();
        toast.error(`${t('Authentication Failed')} ${t(errorMessage)}.`, {
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
              <label>{t('Email Address')}</label>
              <input
                name="email"
                type="email"
                className="form-control mt-1"
                placeholder={t('Enter email address')}
                value={this.state.email}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t('Password')}</label>
              <input
                name="password"
                type="password"
                className="form-control mt-1"
                placeholder={t('Enter password')}
                value={this.state.password}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="d-grid gap-2 mt-3">
              <button onClick={this.handleSubmit} type="submit"
                      className="btn btn-primary">
                {t('Login')}
              </button>
            </div>
            <div className="d-grid gap-2 mt-3">
              <label>{t('Login as:')}</label>
              <select
                name="systemActor"
                className="form-select mt-1"
                value={this.state.systemActor}
                onChange={e => this.handleChange(e)}
              >
                {/* TODO Placeholder. It should be a call on the backend */}
                <option value="DOCTOR">{t('Doctor')}</option>
                <option value="CONFIGURATOR">{t('Configurator')}</option>
                <option value="PLANNER">{t('Planner')}</option>
              </select>
            </div>
            <div className="form-check gap-2 mt-3">
              <input className="form-check-input" type="checkbox"
                     value=""></input>
              {t('Remember me')}
            </div>
            <p className="forgot-password text-center mt-2">
              <a href="">{t('Forgot Password?')}</a>
            </p>
          </div>
        </form>

        {/* Shortcut on login page for the development team
          * This table is shown only in development environment
        */}
        {process.env.NODE_ENV === "development" && (
          <div
            className="Auth-form-content">
            <h3 className="Auth-form-title">Development shortcut</h3>

            <table style={{width: '100%', borderCollapse: 'collapse'}}>
              {/* TITLE */}
              <thead>
              <tr>
                <th style={{padding: '10px', textAlign: 'center'}}>Role</th>
                <th style={{padding: '10px', textAlign: 'center'}}>Mail</th>
                <th style={{padding: '10px', textAlign: 'center'}}>Seniority
                </th>
              </tr>
              </thead>
              <tbody>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{padding: '10px', textAlign: 'center'}}>Dottore</td>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>giuliacantone@gmail.com
                </td>
                <td>Specialista Junior</td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "giuliacantone@gmail.com"});
                      this.setState({password: "passw"});
                      this.setState({systemActor: "DOCTOR"});
                    }}>
                    Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{padding: '10px', textAlign: 'center'}}>Dottore</td>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>domenicoverde@gmail.com
                </td>
                <td>Specialista Senior</td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "domenicoverde@gmail.com"});
                      this.setState({password: "passw"});
                      this.setState({systemActor: "DOCTOR"});
                    }}>Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{padding: '10px', textAlign: 'center'}}>Dottore</td>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>giovannicantone@gmail.com
                </td>
                <td>Strutturato</td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "giovannicantone@gmail.com"});
                      this.setState({password: "passw"});
                      this.setState({systemActor: "DOCTOR"});
                    }}>Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{padding: '10px', textAlign: 'center'}}>Planner</td>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>giovannicantone@gmail.com
                </td>
                <td>Strutturato</td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "giovannicantone@gmail.com"});
                      this.setState({password: "passw"});
                      this.setState({systemActor: "PLANNER"});
                    }}>Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td
                  style={{padding: '10px', textAlign: 'center'}}>Configuratore
                </td>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>salvatimartina97@gmail.com
                </td>
                <td>Specialista Serior</td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "salvatimartina97@gmail.com"});
                      this.setState({password: "passw"});
                      this.setState({systemActor: "CONFIGURATOR"});
                    }}>Insert
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        )}


      </div>
    )
  }

}
