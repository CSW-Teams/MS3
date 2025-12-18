import React from "react"
import {LoginAPI} from "../../API/LoginAPI";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {t} from "i18next";
import {panic} from "../../components/common/Panic";
import RoleSelectionDialog from "../../components/common/DialogRolePicker";
import {UserAPI} from "../../API/UserAPI";
import TurnstileWidget from "../../components/common/TurnstileWidget";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography
} from "@mui/material";
import Alert from "@mui/material/Alert";

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
export default class LoginView extends React.Component {
  constructor(props) {
    super(props);

    // React Ref to the Cloudflare Turnstile widget
    this.turnstileRef = React.createRef();

    // Initial state with empty fields for email and password
    this.state = {
      email: "", password: "",

      open: false, // Dialog box open/close state
      systemActorsAvailable: [], // Available system actors for the user

      // new states for captcha
      turnstileToken: null,    // token from Cloudflare
      captchaVisible: false,   // if true shows widget

      // two factor flow
      twoFactorDialogOpen: false,
      twoFactorChallenge: null,
      twoFactorMessage: "",
      otpInput: "",
      isRecoveryCode: false,
      lockoutInfo: null,
      otpSubmitting: false,
    }

    // Binding the handleSubmit method to the class instance
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  // Called when the user verifies the captcha to set the token received from the widget.
  // This method is called by the TurnstileWidget component
  handleVerify = (token) => {
    // No null check to use this function to reset the state token on token expiration.
    this.setState({turnstileToken: token});
  }

  // Opens the dialog box
  handleDialogOpen = () => {
    this.setState({open: true});
  };

  // Closes the dialog box and handles user role selection
  handleDialogClose = async (role) => {
    this.setState({open: false});

    // If no role is passed, clear the stored user data
    // User clicked out of the dialogue
    if (role === undefined) {
      localStorage.removeItem("name")
      localStorage.removeItem("lastname")
      localStorage.removeItem("tenant")
      localStorage.removeItem("jwt")

      toast.error(`${t('Login Failed:')} ${t("No role selected for the login")}.`, TOAST_OPTIONS);
      return
    }

    // Store the selected role in localStorage and navigate to another page
    localStorage.setItem("actor", role);

    let id;

    try {
      id = await (new UserAPI().getSingleUserTenantId(this.state.email));

      // Store user tenant id in localStorage
      localStorage.setItem("id", id);

      // Navigate to the 'pianificazione-globale' page
      this.props.history.push({
        pathname: '/pianificazione-globale',
      });

      // Reload the page after navigation
      window.location.reload();
    } catch (err) {
      panic()
    }
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

  handleCompleteLogin = async (user) => {
    if (!user || !user.jwt) {
      toast.error(`${t('Authentication Failed')} ${t('Missing authentication token')}.`, TOAST_OPTIONS);
      return;
    }

    localStorage.setItem("name", user.name);
    localStorage.setItem("lastname", user.lastname);
    localStorage.setItem("tenant", user.tenant);
    localStorage.setItem("jwt", user.jwt);

    if (user.systemActors && user.systemActors.length === 1) {
      this.handleDialogClose(user.systemActors[0]);
      return;
    }

    this.setState({systemActorsAvailable: user.systemActors || []});
    this.handleDialogOpen();
  }

  handleTwoFactorChallenge = (data, status) => {
    const challenge = data?.challenge || data?.challengeToken || data?.twoFactorChallenge;
    const lockoutInfo = status === 429 ? {
      message: data?.message,
      retryAfterSeconds: data?.retryAfterSeconds
    } : null;

    this.setState({
      twoFactorDialogOpen: true,
      twoFactorChallenge: challenge || null,
      twoFactorMessage: data?.message || t('Two-factor authentication required.'),
      otpInput: "",
      isRecoveryCode: false,
      lockoutInfo,
    });

    if (status === 429) {
      toast.warn(`${t('Authentication Failed')}: ${t(data?.message || 'Too many attempts. Please wait.')}`, TOAST_OPTIONS);
      return;
    }

    toast.info(t('Enter the code from your authenticator app to continue.'), TOAST_OPTIONS);
  }

  handleOtpDialogClose = () => {
    this.setState({
      twoFactorDialogOpen: false,
      twoFactorChallenge: null,
      twoFactorMessage: "",
      otpInput: "",
      isRecoveryCode: false,
      lockoutInfo: null,
    });
  }

  handleOtpInputChange = (event) => {
    this.setState({otpInput: event.target.value});
  }

  handleOtpToggleMode = () => {
    this.setState((prevState) => ({
      isRecoveryCode: !prevState.isRecoveryCode,
      otpInput: ""
    }));
  }

  handleOtpSubmit = async () => {
    if (!this.state.otpInput) {
      toast.warn(t('Please enter a code to continue.'), TOAST_OPTIONS);
      return;
    }

    if (!this.state.twoFactorChallenge) {
      toast.error(t('Authentication Failed'), TOAST_OPTIONS);
      return;
    }

    this.setState({otpSubmitting: true});

    try {
      const loginAPI = new LoginAPI();
      const response = await loginAPI.postLoginOtp(
        this.state.twoFactorChallenge,
        this.state.otpInput,
        this.state.isRecoveryCode
      );

      let data = {};
      try {
        data = await response.clone().json();
      } catch (err) {
        data = {};
      }

      if (response.ok && data.jwt) {
        this.setState({twoFactorDialogOpen: false, lockoutInfo: null});
        await this.handleCompleteLogin(data);
        return;
      }

      if (data?.requiresTwoFactor) {
        const lockoutInfo = response.status === 429 ? {
          message: data?.message,
          retryAfterSeconds: data?.retryAfterSeconds
        } : null;

        this.setState({
          twoFactorMessage: data?.message || t('Invalid code. Please try again.'),
          lockoutInfo
        });

        const toastMessage = data?.message || t('Invalid two-factor code.');
        if (response.status === 429) {
          toast.warn(`${t('Authentication Failed')}: ${t(toastMessage)}`, TOAST_OPTIONS);
        } else {
          toast.error(`${t('Authentication Failed')} ${t(toastMessage)}`, TOAST_OPTIONS);
        }
        return;
      }

      toast.error(`${t('Authentication Failed')} ${t(data?.message || '')}`, TOAST_OPTIONS);
    } catch (err) {
      console.error("OTP verification error:", err);
      toast.error(`${t('Authentication Failed. Server not online')}`, TOAST_OPTIONS);
    } finally {
      this.setState({otpSubmitting: false});
    }
  }

  /*
   * Handles the form submission for authentication.
   * If authentication is successful, the user is redirected to their profile,
   * otherwise an error message is shown.
   */
  async handleSubmit(e) {
    e.preventDefault();

    // Se il captcha è visibile ma l'utente non l'ha ancora risolto (token null)
    if (this.state.captchaVisible && !this.state.turnstileToken) {
      toast.warn(t("Please complete the security check."), TOAST_OPTIONS);
      return;
    }

    let loginAPI = new LoginAPI();
    let httpResponse;

    try {
      const payload = {
        email: this.state.email,
        password: this.state.password,
        turnstileToken: this.state.turnstileToken // Sends token along the credentials
      };
      // Attempt to perform login
      httpResponse = await loginAPI.postLogin(payload);
    } catch (err) {
      // Intercept network errors (backend:8080 unreachable, etc.)
      console.error("Fetch error:", err);
      toast.error(`${t('Authentication Failed. Server not online')}`, TOAST_OPTIONS);
      return;
    }

    let responseData = {};
    try {
      responseData = await httpResponse.clone().json();
    } catch (err) {
      responseData = {};
    }

    if (httpResponse.ok) {
      if (responseData?.requiresTwoFactor && !responseData?.jwt) {
        this.handleTwoFactorChallenge(responseData, httpResponse.status);
        return;
      }

      await this.handleCompleteLogin(responseData);
      return;
    }

    if (responseData?.requiresTwoFactor) {
      this.handleTwoFactorChallenge(responseData, httpResponse.status);
      return;
    }

    if (httpResponse.status === 401 || httpResponse.status === 400) {
      this.setState({
        captchaVisible: true, turnstileToken: null
      }, () => {
        // Reset the widget iff it's already mounted.
        if (this.turnstileRef.current) {
          this.turnstileRef.current.resetWidget();
        }
      });
      toast.warn(`${t('Authentication Failed')}: ${t("Security check required")}.`, TOAST_OPTIONS);
      return;
    }

    const message = responseData?.message || "";
    toast.error(`${t('Authentication Failed')} ${t(message)}`, TOAST_OPTIONS);
  }

  render() {
    return (
      <div className="Auth-form-container">

        <RoleSelectionDialog open={this.state.open}
                             onClose={this.handleDialogClose}
                             systemActors={this.state.systemActorsAvailable}/>

        <Dialog open={this.state.twoFactorDialogOpen}
                onClose={this.handleOtpDialogClose}
                disableEnforceFocus>
          <DialogTitle>{t('Two-Factor Authentication')}</DialogTitle>
          <DialogContent>
            <Typography variant="body1" gutterBottom>
              {this.state.twoFactorMessage || t('Enter the authentication code to continue.')}
            </Typography>

            {this.state.lockoutInfo && (
              <Alert severity="warning" sx={{mb: 2}}>
                {this.state.lockoutInfo.message || t('Too many failed attempts. Please wait before retrying.')}
                {this.state.lockoutInfo.retryAfterSeconds ? ` ${t('Try again in')} ${this.state.lockoutInfo.retryAfterSeconds} ${t('seconds')}.` : ''}
              </Alert>
            )}

            <TextField
              fullWidth
              autoFocus
              margin="dense"
              label={this.state.isRecoveryCode ? t('Recovery code') : t('Authenticator code')}
              type="text"
              value={this.state.otpInput}
              onChange={this.handleOtpInputChange}
              disabled={!!this.state.lockoutInfo}
            />

            <Button onClick={this.handleOtpToggleMode} size="small" sx={{mt: 1}}>
              {this.state.isRecoveryCode ? t('Use authenticator code instead') : t('Use a recovery code')}
            </Button>
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleOtpDialogClose}>{t('Cancel')}</Button>
            <Button onClick={this.handleOtpSubmit}
                    disabled={this.state.otpSubmitting || !!this.state.lockoutInfo}
                    variant="contained">
              {t('Verify')}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Contenitore con layout flessibile */}
        <div className="Auth-page-content" style={{
          width: '100vw',       // Occupa tutta la larghezza dello schermo
          height: '100vh',      // Occupa tutta l'altezza dello schermo
          display: 'flex', flexDirection: 'row', alignItems: 'center', // Centrare orizzontalmente
          justifyContent: 'center', // Centrare verticalmente
          gap: '20px', // Spazio tra i due blocchi
        }}>
          <form className="Auth-form"
                style={{width: '100%', maxWidth: '440px'}}>
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

              {/* ----- CAPTCHA CONTAINER ----- */}
              {this.state.captchaVisible && (
                <TurnstileWidget
                  className="d-grid gap-2 mt-3"
                  ref={this.turnstileRef} // Ref to the widget
                  siteKey={process.env.REACT_APP_TURNSTILE_KEY}
                  onVerify={this.handleVerify} // Callback to set the token after captcha is solved
                />
              )}

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
          {process.env.NODE_ENV === "development" && (<div
              className="Auth-form-content"
              style={{width: '100%', maxWidth: '800px'}}>
              <h3 className="Auth-form-title">Development shortcut</h3>

              <table style={{width: '100%', borderCollapse: 'collapse'}}>
                {/* TITLE */}
                <thead>
                <tr>
                  <th style={{padding: '10px', textAlign: 'center'}}>Tenant</th>
                  <th style={{padding: '10px', textAlign: 'center'}}>Role</th>
                  <th style={{padding: '10px', textAlign: 'center'}}>Mail</th>
                  <th style={{padding: '10px', textAlign: 'center'}}>Seniority
                  </th>
                </tr>
                </thead>
                <tbody>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>A</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>Dottore
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>giuliacantone.tenanta@gmail.com
                  </td>
                  <td>Specialista Junior</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "giuliacantone.tenanta@gmail.com"});
                        this.setState({password: "passw"});
                      }}>
                      Insert
                    </button>
                  </td>
                </tr>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>B</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>Dottore
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>domenicoverde.tenantb@gmail.com
                  </td>
                  <td>Specialista Senior</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "domenicoverde.tenantb@gmail.com"});
                        this.setState({password: "passw"});
                      }}>Insert
                    </button>
                  </td>
                </tr>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>A</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>Dottore,
                    Planner
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>giovannicantone.tenanta@gmail.com
                  </td>
                  <td>Specialista Senior</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "giovannicantone.tenanta@gmail.com"});
                        this.setState({password: "passw"});
                      }}>Insert
                    </button>
                  </td>
                </tr>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>B</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>Dottore
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>giuliofarnasini.tenantb@gmail.com
                  </td>
                  <td>Strutturato</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "giuliofarnasini.tenantb@gmail.com"});
                        this.setState({password: "passw2"});
                      }}>Insert
                    </button>
                  </td>
                </tr>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>A</td>
                  <td
                    style={{padding: '10px', textAlign: 'center'}}>Configuratore
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>salvatimartina97.tenanta@gmail.com
                  </td>
                  <td>Specialista Junior</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "salvatimartina97.tenanta@gmail.com"});
                        this.setState({password: "passw"});
                      }}>Insert
                    </button>
                  </td>
                </tr>
                <tr style={{borderBottom: '1px solid black'}}>
                  <td style={{textAlign: 'center'}}>B</td>
                  <td
                    style={{padding: '10px', textAlign: 'center'}}>Dottore,
                    Configuratore, Planner
                  </td>
                  <td style={{
                    padding: '10px', textAlign: 'center'
                  }}>fullpermessi.tenantb@gmail.com
                  </td>
                  <td>Strutturato</td>
                  <td style={{padding: '10px', textAlign: 'center'}}>
                    <button
                      style={{border: '1px solid black'}}
                      onClick={() => {
                        this.setState({email: "fullpermessi.tenantb@gmail.com"});
                        this.setState({password: "passw2"});
                      }}>Insert
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>)}
        </div>
      </div>)
  }
}
