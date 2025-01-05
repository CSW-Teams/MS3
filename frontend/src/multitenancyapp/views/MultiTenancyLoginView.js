import React from "react"
import {MultiTenancyLoginAPI} from "../API/MultiTenancyLoginAPI";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {t} from "i18next";
import {panic} from "../../components/common/Panic";
import HospitalSelectionDialog from "../components/common/TenantPickerDialog";

// Toast notification options for error/success messages
const TOAST_OPTIONS = {
  position: "top-center",
  autoClose: 5000,
  hideProgressBar: true,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  progress: undefined,
  theme: "colored",
};

// Define a LoginView component using React class-based component
export default class MultiTenancyLoginView extends React.Component {
  constructor(props) {
    super(props);

    // Initial state with empty fields for email and password
    this.state = {
      email: "",
      password: "",

      open: false, // Dialog box open/close state
      systemHospitalsAvailable: [] // Available system hospitals for the user
    }

    // Binding the handleSubmit method to the class instance
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  // Opens the dialog box
  handleDialogOpen = () => {
    this.setState({open: true});
  };

  // Closes the dialog box and handles user hospital selection
  handleDialogClose = (hospital) => {
    this.setState({open: false});

    // If no hospital is passed, clear the stored user data
    // User clicked out of the dialogue
    if (hospital === undefined) {
      localStorage.removeItem("id")
      localStorage.removeItem("name")
      localStorage.removeItem("lastname")
      localStorage.removeItem("jwt")

      toast.error(`${t('Login Failed:')} ${"No hospital selected for the login"}.`, TOAST_OPTIONS);

      return
    }

    // Store the selected hospital in localStorage and navigate to another page
    localStorage.setItem("tenant", hospital);

    // Navigate to the 'utenti-tenant' page
    this.props.history.push({
      pathname: '/multitenancy/info-utenti',
    });

    // Reload the page after navigation
    window.location.reload();
  };

  // Handles input changes for email and password fields
  handleInputChange(e) {
    const val = e.target.value;
    const name = e.target.name;

    // Updates the state with the changed value
    this.setState({
      [name]: val
    });
  }

  /*
   * Handles the form submission for authentication.
   * If authentication is successful, the user is redirected to their profile,
   * otherwise an error message is shown.
   */
  async handleSubmit(e) {
    e.preventDefault();

    // Function to handle successful login response
    const handleSuccess = async (response) => {
      const user = await response.json();

      // Store user data in localStorage
      localStorage.setItem("id", user.id);
      localStorage.setItem("name", user.name);
      localStorage.setItem("lastname", user.lastname);
      localStorage.setItem("jwt", user.jwt);

      // If only one system hospital is available, close the dialog and proceed
      if (user.systemHospitals.length === 1) {
        this.handleDialogClose(user.systemHospitals[0]);
        return;
      }

      // If multiple system actors are available, display the dialog for role selection
      this.setState({ systemHospitalsAvailable: user.systemHospitals });
      this.handleDialogOpen();
    };

    // Function to handle server errors (5xx responses)
    const handleServerError = () => {
      toast.error(`${t('Authentication Failed. Server not online')}`, TOAST_OPTIONS);
    };

    // Mapping HTTP status classes to appropriate handler functions
    const HTTP_STATUS_HANDLERS = {
      2: handleSuccess,  // Handles success responses (2xx)
      5: handleServerError,  // Handles server error responses (5xx)
    };

    // Default function to handle errors
    const handleDefaultError = async (response) => {
      const errorMessage = await response.text();
      toast.error(`${t('Authentication Failed')} ${t(errorMessage)}.`, TOAST_OPTIONS);
    };

    let multiTenancyLoginAPI = new MultiTenancyLoginAPI();
    let httpResponse;

    try {
      // Attempt to perform login
      httpResponse = await multiTenancyLoginAPI.postLogin(this.state);
    } catch (err) {
      // If an error occurs during the login request, handle it (panic state)
      panic();
      return;
    }

    // Calculate the HTTP status class (e.g., 2xx, 5xx)
    const statusClass = Math.floor(httpResponse.status / 100);

    // If the status class has an associated handler, call it, otherwise use the default error handler
    const handler = HTTP_STATUS_HANDLERS[statusClass] || handleDefaultError;
    await handler(httpResponse);
  }


  render() {
    return (
      <div className="Auth-form-container">

        <HospitalSelectionDialog open={this.state.open}
                             onClose={this.handleDialogClose}
                             hospitals={this.state.systemHospitalsAvailable}/>

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
                onChange={e => this.handleInputChange(e)}
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
                onChange={e => this.handleInputChange(e)}
              />
            </div>

            <div className="d-grid gap-2 mt-3">
              <button onClick={this.handleSubmit} type="submit"
                      className="btn btn-primary">
                {t('Login')}
              </button>
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
                <th style={{padding: '10px', textAlign: 'center'}}>Mail</th>
                <th style={{padding: '10px', textAlign: 'center'}}>Tenant
                </th>
              </tr>
              </thead>
              <tbody>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>giuliacantone@gmail.com
                </td>
                <td style={{
                  textAlign: 'center'
                }}>A
                </td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "giuliacantone@gmail.com"});
                      this.setState({password: "passw"});
                    }}>
                    Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>domenicoverde@gmail.com
                </td>
                <td style={{
                  textAlign: 'center'
                }}>B
                </td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "domenicoverde@gmail.com"});
                      this.setState({password: "passw"});
                    }}>Insert
                  </button>
                </td>
              </tr>
              <tr style={{borderBottom: '1px solid black'}}>
                <td style={{
                  padding: '10px',
                  textAlign: 'center'
                }}>giovannicantone@gmail.com
                </td>
                <td style={{
                  textAlign: 'center'
                }}>A, B
                </td>
                <td style={{padding: '10px', textAlign: 'center'}}>
                  <button
                    style={{border: '1px solid black'}}
                    onClick={() => {
                      this.setState({email: "giovannicantone@gmail.com"});
                      this.setState({password: "passw"});
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
