import React from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Container,
  Divider,
  Grid,
  TextField,
  Typography
} from "@mui/material";
import {TwoFactorAPI} from "../../API/TwoFactorAPI";
import {toast} from "react-toastify";
import {t} from "i18next";
import {panic} from "../../components/common/Panic";

export default class TwoFactorEnrollmentView extends React.Component {
  constructor(props) {
    super(props);
    this.twoFactorApi = new TwoFactorAPI();
    this.state = {
      loadingStatus: true,
      statusMessage: "",
      twoFactorEnabled: false,
      enrollmentRequired: false,
      enrollmentStarted: false,
      qrImage: null,
      manualKey: "",
      otpauthUrl: "",
      recoveryCodes: [],
      otpInput: "",
      disableCode: "",
      disableWithRecovery: false,
      confirmationInFlight: false,
      disableInFlight: false,
    };
  }

  async componentDidMount() {
    await this.loadStatus();
  }

  componentWillUnmount() {
    this.clearSensitiveEnrollmentState();
  }

  clearSensitiveEnrollmentState = () => {
    this.setState({
      qrImage: null,
      manualKey: "",
      otpauthUrl: "",
      recoveryCodes: [],
      otpInput: "",
    });
  }

  loadStatus = async () => {
    this.setState({loadingStatus: true});
    try {
      const response = await this.twoFactorApi.getStatus();
      let data = {};
      try {
        data = await response.clone().json();
      } catch (err) {
        data = {};
      }

      if (response.ok) {
        this.setState({
          twoFactorEnabled: !!data.enabled,
          enrollmentRequired: !!data.enrollmentRequired,
          statusMessage: data.message || "",
        });
        return;
      }

      toast.error(t(data?.message || "Unable to load two-factor status."));
    } catch (err) {
      panic();
    } finally {
      this.setState({loadingStatus: false});
    }
  }

  handleStartEnrollment = async () => {
    try {
      const response = await this.twoFactorApi.startEnrollment();
      let data = {};
      try {
        data = await response.clone().json();
      } catch (err) {
        data = {};
      }

      if (!response.ok) {
        toast.error(t(data?.message || "Unable to start enrollment."));
        return;
      }

      this.setState({
        enrollmentStarted: true,
        qrImage: data.qrImage || null,
        manualKey: data.manualKey || data.secretKey || "",
        otpauthUrl: data.otpauthUrl || "",
        recoveryCodes: data.recoveryCodes || [],
        statusMessage: data.message || "",
        otpInput: "",
      });
      toast.info(t("Scan the QR code or enter the manual key, then confirm with an authenticator code."));
    } catch (err) {
      panic();
    }
  }

  handleConfirmEnrollment = async () => {
    if (!this.state.otpInput) {
      toast.warn(t("Please enter the authenticator code to confirm."));
      return;
    }

    this.setState({confirmationInFlight: true});

    try {
      const response = await this.twoFactorApi.confirmEnrollment(this.state.otpInput);
      let data = {};
      try {
        data = await response.clone().json();
      } catch (err) {
        data = {};
      }

      if (!response.ok) {
        toast.error(t(data?.message || "Invalid code. Please try again."));
        return;
      }

      this.setState({
        twoFactorEnabled: true,
        enrollmentStarted: false,
        statusMessage: data.message || "",
      });
      this.clearSensitiveEnrollmentState();
      toast.success(t("Two-factor authentication enabled."));
    } catch (err) {
      panic();
    } finally {
      this.setState({confirmationInFlight: false});
    }
  }

  handleHideRecoveryCodes = () => {
    this.setState({recoveryCodes: []});
  }

  handleDisable = async () => {
    if (!this.state.disableCode) {
      toast.warn(t("Provide a code to disable two-factor authentication."));
      return;
    }
    this.setState({disableInFlight: true});
    try {
      const response = await this.twoFactorApi.disableTwoFactor(
        this.state.disableCode,
        this.state.disableWithRecovery
      );
      let data = {};
      try {
        data = await response.clone().json();
      } catch (err) {
        data = {};
      }

      if (!response.ok) {
        toast.error(t(data?.message || "Unable to disable two-factor authentication."));
        return;
      }

      this.setState({
        twoFactorEnabled: false,
        enrollmentRequired: false,
        disableCode: "",
        statusMessage: data.message || "",
      });
      this.clearSensitiveEnrollmentState();
      toast.success(t("Two-factor authentication disabled."));
    } catch (err) {
      panic();
    } finally {
      this.setState({disableInFlight: false});
    }
  }

  renderRecoveryCodes() {
    if (!this.state.recoveryCodes || this.state.recoveryCodes.length === 0) {
      return null;
    }

    return (
      <Box sx={{mt: 2}}>
        <Alert severity="info" sx={{mb: 2}}>
          {t('Save these recovery codes now. They are shown only once and will not be cached or logged.')}<br/>
          {t('Codes support jump-ahead use: entering code N marks all earlier codes as used.')}<br/>
          {t('The final recovery code disables two-factor protection and forces re-enrollment with a new secret.')}
        </Alert>
        <Grid container spacing={1}>
          {this.state.recoveryCodes.map((code, idx) => (
            <Grid item xs={12} sm={6} md={4} key={`${code}-${idx}`}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="subtitle2">{t('Recovery code')} #{idx + 1}</Typography>
                  <Typography variant="h6" sx={{wordBreak: 'break-all'}}>{code}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
        <Box sx={{mt: 2}}>
          <Button variant="outlined" color="secondary" onClick={this.handleHideRecoveryCodes}>
            {t('I have stored my recovery codes')}
          </Button>
        </Box>
      </Box>
    );
  }

  renderEnrollmentCard() {
    if (!this.state.enrollmentStarted) {
      return null;
    }

    return (
      <Card variant="outlined" sx={{mt: 3}}>
        <CardContent>
          <Typography variant="h6" gutterBottom>{t('Enroll your authenticator')}</Typography>
          <Typography variant="body2" gutterBottom>
            {t('Scan the QR code with your authenticator app or enter the manual key below, then provide the current code to confirm enrollment.')}
          </Typography>
          {this.state.qrImage && (
            <Box sx={{my: 2, display: 'flex', justifyContent: 'center'}}>
              <img src={this.state.qrImage} alt={t('Authenticator QR code')} style={{maxWidth: '240px'}} />
            </Box>
          )}
          <TextField
            label={t('Manual key')}
            fullWidth
            margin="normal"
            value={this.state.manualKey}
            InputProps={{readOnly: true}}
          />
          <TextField
            label={t('Authenticator code')}
            fullWidth
            margin="normal"
            value={this.state.otpInput}
            onChange={(event) => this.setState({otpInput: event.target.value})}
          />
          <Alert severity="info" sx={{mt: 1}}>
            {t('Use the code currently shown in your authenticator. Codes refresh every 30 seconds.')}
          </Alert>
          {this.renderRecoveryCodes()}
        </CardContent>
        <CardActions>
          <Button onClick={this.clearSensitiveEnrollmentState}>{t('Cancel enrollment')}</Button>
          <Button
            variant="contained"
            onClick={this.handleConfirmEnrollment}
            disabled={this.state.confirmationInFlight}
          >
            {t('Confirm and enable 2FA')}
          </Button>
        </CardActions>
      </Card>
    );
  }

  renderDisableCard() {
    if (!this.state.twoFactorEnabled) {
      return null;
    }

    return (
      <Card variant="outlined" sx={{mt: 3}}>
        <CardContent>
          <Typography variant="h6" gutterBottom>{t('Disable two-factor authentication')}</Typography>
          <Typography variant="body2" gutterBottom>
            {t('Enter an authenticator or recovery code to disable protection. Using the final recovery code will disable 2FA and rotate your enrollment secret.')}
          </Typography>
          <TextField
            label={this.state.disableWithRecovery ? t('Recovery code') : t('Authenticator code')}
            fullWidth
            margin="normal"
            value={this.state.disableCode}
            onChange={(event) => this.setState({disableCode: event.target.value})}
          />
          <Button onClick={() => this.setState({disableWithRecovery: !this.state.disableWithRecovery, disableCode: ""})}>
            {this.state.disableWithRecovery ? t('Use authenticator code instead') : t('Use a recovery code')}
          </Button>
        </CardContent>
        <CardActions>
          <Button
            variant="outlined"
            color="error"
            onClick={this.handleDisable}
            disabled={this.state.disableInFlight}
          >
            {t('Disable 2FA')}
          </Button>
        </CardActions>
      </Card>
    );
  }

  render() {
    return (
      <Container maxWidth="md" sx={{py: 4}}>
        <Typography variant="h4" gutterBottom>{t('Two-Factor Security')}</Typography>
        {this.state.enrollmentRequired && (
          <Alert severity="warning" sx={{mb: 2}}>
            {t('Your role requires two-factor authentication. Please enroll to keep access to sensitive features.')}
          </Alert>
        )}
        {this.state.statusMessage && (
          <Alert severity={this.state.twoFactorEnabled ? 'success' : 'info'} sx={{mb: 2}}>
            {t(this.state.statusMessage)}
          </Alert>
        )}
        <Card variant="outlined">
          <CardContent>
            <Typography variant="h6">{t('Current status')}</Typography>
            <Typography variant="body1" sx={{mt: 1}}>
              {this.state.twoFactorEnabled ? t('Two-factor authentication is enabled on your account.') : t('Two-factor authentication is not enabled.')}
            </Typography>
            {!this.state.twoFactorEnabled && (
              <Typography variant="body2" sx={{mt: 1}}>
                {t('Enroll to protect your account with an authenticator app and single-use recovery codes.')}
              </Typography>
            )}
          </CardContent>
          <CardActions>
            <Button
              variant="contained"
              onClick={this.handleStartEnrollment}
              disabled={this.state.loadingStatus}
            >
              {this.state.twoFactorEnabled ? t('Re-enroll to refresh codes') : t('Start enrollment')}
            </Button>
          </CardActions>
        </Card>

        {this.renderEnrollmentCard()}
        <Divider sx={{my: 3}} />
        {this.renderDisableCard()}
      </Container>
    );
  }
}
