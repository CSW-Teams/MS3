import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { withRouter } from 'react-router-dom';
import { t } from "i18next";
import {panic} from "../../components/common/Panic";

export default class LoginView extends React.Component {

  goBack = () => {
    const { history } = this.props;
    history.push('/info-utenti');
  };


  constructor(props){
    super(props);
    this.state = {
      name: "",
      lastname: "",
      birthday: "",
      taxCode: "",
      seniority: "",
      email: "",
      password: "",
      systemActors: [],
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

  handleCheckboxChange(e) {
    const selectedActor = e.target.value;

    if (this.state.systemActors.includes(selectedActor)) {
      const newActors = this.state.systemActors.filter(actor => actor !== selectedActor);
      this.setState({systemActors: newActors});
    } else {
      const newActors = [...this.state.systemActors, selectedActor];
      this.setState({ systemActors: newActors });
    }
  }


  async handleSubmit(e) {

    e.preventDefault();

    // Manda una HTTP Post al backend
    let loginAPI = new LoginAPI();

    const data = {
      name: this.state.name,
    }

    if (!this.state.systemActors.includes("DOCTOR")) {
      delete this.state.seniority;
    }

    let httpResponse
    try {
      httpResponse = await loginAPI.postRegistration(this.state);
    } catch (err) {

      panic()
      return
    }

    /* Se la registrazione del nuovo utente ha esito positivo, viene
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
        this.goBack();

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
            <h3 className="Auth-form-title">{t("Register new user")}</h3>
            <div className="form-group mt-3">
              <label>{t("Name")}</label>
              <input
                name="name"
                type="text"
                className="form-control mt-1"
                placeholder={t("Insert name")}
                value={this.state.name}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Surname")}</label>
              <input
                name="lastname"
                type="text"
                className="form-control mt-1"
                placeholder={t("Insert surname")}
                value={this.state.lastname}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Tax Code")}</label>
              <input
                name="taxCode"
                type="text"
                className="form-control mt-1"
                placeholder={t("Insert tax code")}
                value={this.state.taxCode}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Birthdate")}</label>
              <input
                name="birthday"
                type="date"
                className="form-control mt-1"
                placeholder={t("Insert birthdate")}
                value={this.state.birthday}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Email Address")}</label>
              <input
                name="email"
                type="email"
                className="form-control mt-1"
                placeholder={t("Insert email address")}
                value={this.state.email}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Password")}</label>
              <input
                name="password"
                type="password"
                className="form-control mt-1"
                placeholder={t("Insert password")}
                value={this.state.password}
                onChange={e => this.handleChange(e)}
              />
            </div>
            <div className="form-group mt-3">
              <label>{t("Role")}</label>
              <div>
                <input
                  type="radio"
                  id="structured"
                  name="seniority"
                  value="STRUCTURED"
                  checked={this.state.seniority === "STRUCTURED"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="strutturato">{t("STRUCTURED")}</label>
              </div>
              <div>
                <input
                  type="radio"
                  id="specialist_junior"
                  name="seniority"
                  value="SPECIALIST_JUNIOR"
                  checked={this.state.seniority === "SPECIALIST_JUNIOR"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="specializzando">{t("SPECIALIST_JUNIOR")}</label>
              </div>
              <div>
                <input
                  type="radio"
                  id="specialist_senior"
                  name="seniority"
                  value="SPECIALIST_SENIOR"
                  checked={this.state.seniority === "SPECIALIST_SENIOR"}
                  onChange={e => this.handleChange(e)}
                />
                <label htmlFor="specializzando">{t("SPECIALIST_SENIOR")}</label>
              </div>
            </div>
            <div className="form-group mt-3">
              <label>{t("Actor")}</label>
              <div>
                <input
                  type="checkbox"
                  id="configurator"
                  name="attore"
                  value="CONFIGURATOR"
                  checked={this.state.systemActors.includes("CONFIGURATOR")}
                  onChange={e => this.handleCheckboxChange(e)}
                />
                <label htmlFor="configuratore">{t("CONFIGURATOR")}</label>
              </div>
              <div>
                <input
                  type="checkbox"
                  id="doctor"
                  name="attore"
                  value="DOCTOR"
                  checked={this.state.systemActors.includes("DOCTOR")}
                  onChange={e => this.handleCheckboxChange(e)}
                />
                <label htmlFor="doctor">{t("DOCTOR")}</label>
              </div>
              <div>
                <input
                  type="checkbox"
                  id="planner"
                  name="attore"
                  value="PLANNER"
                  checked={this.state.systemActors.includes("PLANNER")}
                  onChange={e => this.handleCheckboxChange(e)}
                />
                <label htmlFor="pianificatore">{t("PLANNER")}</label>
              </div>
            </div>
            <div className="d-grid gap-2 mt-3">
              <button onClick={this.handleSubmit} type="submit" className="btn btn-primary">
                {t("Save")}
              </button>
            </div>
          </div>
        </form>
      </div>
    )
  }

}
