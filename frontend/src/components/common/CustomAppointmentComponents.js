import React, {useState, useEffect} from "react";
import {Grid} from "@mui/material";
import {Appointments} from "@devexpress/dx-react-scheduler-material-ui";
import AccessTime from '@mui/icons-material/AccessTime';
import Lens from '@mui/icons-material/Lens';
import {HOUR_MINUTE_OPTIONS, WEEKDAY_INTERVAL, viewBoundText } from '@devexpress/dx-scheduler-core';
import {StyledAppointmentsAppointmentContent, tooltip_classes} from "./style";
import classNames from "clsx";
import PropTypes from "prop-types";
import {classes,StyledDiv} from "./style"
import { SchedulableType } from "../../API/Schedulable";
import Button from "@mui/material/Button";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import {
  RichiestaRimozioneDaTurnoAPI
} from "../../API/RichiestaRimozioneDaTurnoAPI";


// AppointmentContent di SingleScheduleView
export const AppointmentSingleContent = ({
                                    data, formatDate, ...restProps
                                  }) => (
  <StyledAppointmentsAppointmentContent {...restProps} formatDate={formatDate} data={data} >
    <div className={classes.container}>
      <div style={{textAlign: "center", color: "white","fontFamily":"sans-serif", "font-weight":"bold"}}>
        {data.title}
      </div>
      <li
        style={{color: data.turno === "REPERIBILITA'" ? "white" : "green","font-family":"sans-serif", "font-weight":"bold"}}
      > {data.turno} </li>
        <li
        > {data.tipologia} </li>
      <li>
        <div className={classes.time}>
          {formatDate(data.startDate, { hour: 'numeric', minute: 'numeric' })}
        </div>
        <div className={classes.time}>
          {' - '}
        </div>
        <div className={classes.time}>
          {formatDate(data.endDate, { hour: 'numeric', minute: 'numeric' })}
        </div>
      </li>
    </div>
  </StyledAppointmentsAppointmentContent>
);


//Appointment Tooltip generico
/** Questo componente mostra i dettagli di uno schedulabile, i quali appaiono
 * dopo aver cliccato sull'Appointment corrispondente.
 */
export const Content = ({
                          className,
                          children,
                          appointmentData,
                          appointmentResources,
                          formatDate,
                          recurringIconComponent: RecurringIcon,
                          view,
                          onRetirement,
                          actor,
                          checkRequests,
                          ...restProps
                        }) => {
  const weekDays = viewBoundText(
    appointmentData.startDate, appointmentData.endDate, WEEKDAY_INTERVAL,
    appointmentData.startDate, 1, formatDate,
  );

  const [isConfirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [justification, setJustification] = useState('');
  const [hasPendingRequest, setHasPendingRequest] = useState(false);
  const [retiredUser, setRetiredUser] = useState('');

  useEffect(() => {
    const checkPendingRequest = async () => {
      if(actor === "PLANNER") {
        try {
          const result = await checkRequests(appointmentData.id);
          if (result === -1) {
            setHasPendingRequest(false);
          } else {
            setHasPendingRequest(true);
            setRetiredUser(result);
          }
        } catch (error) {
          console.error('Error checking requests:', error);
        };
      }
    }
    checkPendingRequest();
  }, [appointmentData.id, checkRequests,actor]);


  const handleConfirmRetirement = () => {
    closeConfirmationDialog();
    onRetirement(justification, appointmentData.id);
  }

  const openConfirmationDialog = () => {
    setConfirmationDialogOpen(true);
  };

  const closeConfirmationDialog = () => {
    setConfirmationDialogOpen(false);
  };

  const handleRetireButtonClick = () => {
    openConfirmationDialog();
  };

  const retireFromShiftButton = view!=="global" && (
    <Button
      style={{ marginTop: '20px' }}
      onClick={handleRetireButtonClick}
    >
      Ritirati dal turno
    </Button>
  );


  // contents of tooltip may vary depending on the type of the corresponding schedulable
  switch(appointmentData.schedulableType){
    case SchedulableType.Holiday:
      return (

        <StyledDiv
        resources={appointmentResources}
        className={classNames(tooltip_classes.content, className)}
        {...restProps}
       >
         <h1>{appointmentData.title}! 🥳</h1>
          <li>Festività {appointmentData.category}</li>
          <li>Assegnare turni in questo giorno può generare malcontento. Ricorda di essere equo!</li>
      </StyledDiv>

      )

      break;
    case SchedulableType.AssignedShift:

      /**
       * Lo schedulabile è un turno assegnato.
       * Per tutti i turni verranno mostrati tra i dettagli gli allocati al turno.
       * Se il turno è una GUARDIA, verranno mostrati anche i reperibili.
       */
      return (
        <StyledDiv
          resources={appointmentResources}
          className={classNames(tooltip_classes.content, className)}
          {...restProps}
        >
          <Grid container alignItems="flex-start" className={tooltip_classes.titleContainer}>
            <Grid item xs={2}>
              <div className={tooltip_classes.relativeContainer}>
                <Lens className={tooltip_classes.lens} />
                {!!appointmentData.rRule && (
                  <RecurringIcon className={tooltip_classes.recurringIcon} />
                )}
              </div>
            </Grid>
            <Grid item xs={10}>
              <div>
                <div className={classNames(tooltip_classes.title, tooltip_classes.dateAndTitle)}>
                  {appointmentData.title}
                </div>
                <div className={classNames(tooltip_classes.text, tooltip_classes.dateAndTitle)}>
                  {weekDays}
                </div>
              </div>
            </Grid>
          </Grid>
          <Grid container alignItems="center" className={tooltip_classes.contentContainer}>
            <Grid item xs={2} className={tooltip_classes.textCenter}>
              <AccessTime className={tooltip_classes.icon} />
            </Grid>
            <Grid item xs={10}>
              <div className={tooltip_classes.text}>
                {`${formatDate(appointmentData.startDate, HOUR_MINUTE_OPTIONS)}
                  - ${formatDate(appointmentData.endDate, HOUR_MINUTE_OPTIONS)}`}
              </div>
            </Grid>
          </Grid>
          <Grid>
            <Grid container alignItems="center" >
              <div className={tooltip_classes.text}> Allocati: </div>
            </Grid>
            { appointmentData.utenti_guardia.map(resourceItem => (
              <Grid container alignItems="center" className={tooltip_classes.resourceContainer} key={`${resourceItem.id}`}>
                <Grid item xs={2} className={tooltip_classes.textCenter}>
                  <div className={tooltip_classes.relativeContainer}>
                    <Lens
                      className={classNames(tooltip_classes.lens, tooltip_classes.lensMini)}
                    />
                  </div>
                </Grid>
                <Grid item xs={10}>
                  <div className={tooltip_classes.text}>
                    {resourceItem.name} {resourceItem.lastname} - {resourceItem.seniority}
                  </div>
                </Grid>
              </Grid>
            ))}
            { appointmentData.reperibilitaAttiva === true ? (
              <div>
                <Grid item xs={2}>
                  <div className={tooltip_classes.text}> Reperibili: </div>
                </Grid>
                { appointmentData.utenti_reperibili.map(resourceItem => (
                  <Grid container alignItems="center" className={tooltip_classes.resourceContainer} key={`${resourceItem.id}`}>
                    <Grid item xs={2} className={tooltip_classes.textCenter}>
                      <div className={tooltip_classes.relativeContainer}>
                        <Lens
                          className={classNames(tooltip_classes.lens, tooltip_classes.lensMini)}
                        />
                      </div>
                    </Grid>
                    <Grid item xs={10}>
                      <div className={tooltip_classes.text}>
                        {resourceItem.name} {resourceItem.lastname} - {resourceItem.seniority}
                      </div>
                    </Grid>
                  </Grid>
                ))}
              </div>
          ) : (
              <div></div>
            ) }
          </Grid>
          {retireFromShiftButton}
          {view === 'global' && actor === 'PIANIFICATORE' && hasPendingRequest === true && (
            <div style={{ color: 'red', marginTop: '10px' }}>
              <p>Attenzione: {retiredUser} ha richiesto di ritirarsi da questo turno.</p>
            </div>
          )}


          {/* Dialogo di conferma */}
          <Dialog
            open={isConfirmationDialogOpen}
            onClose={closeConfirmationDialog}
            maxWidth="md"
            fullWidth
          >
            <DialogTitle>Conferma Ritiro</DialogTitle>
            <DialogContent>
              <TextField
                id={"motivazione"}
                margin={"normal"}
                label="Inserisci motivazione"
                fullWidth
                value={justification}
                onChange={(e) => setJustification(e.target.value)}
                autoFocus
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={closeConfirmationDialog} color="primary">
                Annulla
              </Button>
              <Button onClick={handleConfirmRetirement} color="primary">
                Conferma
              </Button>
            </DialogActions>
          </Dialog>

          {children}
        </StyledDiv>
      );
          break;
    default:
      return (
        // empty tooltip

        null
        )

  }
};

Content.propTypes = {
  appointmentData: PropTypes.object,
  appointmentResources: PropTypes.array,
  children: PropTypes.node,
  className: PropTypes.string,
  formatDate: PropTypes.func.isRequired,
  recurringIconComponent: PropTypes.oneOfType([PropTypes.func, PropTypes.object]).isRequired,
};

Content.defaultProps = {
  appointmentData: undefined,
  appointmentResources: [],
  className: undefined,
  children: undefined,
};


/** Questo appointment descrive il rettangolo colorato contenente il sommario
 * delle informazioni di uno schedulabile.
 */
export class AppointmentContent extends React.Component{

  constructor(data, formatDate, ...restProps) {
    super(data, formatDate, ...restProps);
    this.state = {
      utenti_allocati: [],
      utenti_reperibili: [], // corrispondono ai reperibili nelle mansioni di guardia
      utenti_rimossi: [],
      formatDate: formatDate,
      ...data,
      restProps: {...restProps},
      attore: data.attore,
      requests: [],
    }

  }

  async componentDidMount() {

    /**
     * Se questo schedulabile è un turno, recuperiamo i dettagli degli utenti
     * usando gli ids salvati nell'oggetto turno.
     */
    if (this.state.data.schedulableType === SchedulableType.AssignedShift) {
      this.setState({
        utenti_allocati: this.state.data.utenti_guardia,
        utenti_reperibili: this.state.data.utenti_reperibili,
        utenti_rimossi: this.state.data.utenti_rimossi,
      })
    }

    let api = new RichiestaRimozioneDaTurnoAPI();
    let array = await api.getAllPendingRequests();
    this.setState({ requests: array })
  }


  render() {
  // mostriamo informazioni diverse a seconda del tipo di schedulabile
  if (this.state.data.schedulableType === SchedulableType.AssignedShift) {

    /* Se esistono richieste di ritiro pendenti per il turno, il rettangolo è di colore rosso, altrimenti è blu
    *  Questa differenza è visibile solo ai pianificatori.
    * */
    let appointmentStyle = {backgroundColor: '#4db6ac'};

    if (this.state.attore === "PLANNER") {
      let pendingRequestExists = this.state.requests.some(request => request.idShift === this.state.data.id);

      if (pendingRequestExists) {
        appointmentStyle = {backgroundColor: 'red'}
      }
    }


    /**
     * Mostriamo i cognomi dei partecipanti al turno, includendo i reperibili
     * se il turno è una GUARDIA
     */
    return (
      <StyledAppointmentsAppointmentContent {...this.state.restProps} formatDate={this.state.formatDate} data={this.state.data} style={appointmentStyle}>
        <div className={classes.container}>
          <div style={{ textAlign: "center", color: "black", "fontFamily": "sans-serif", "font-weight": "bold" }}>
            {this.state.data.title}
          </div>
          <div>

            {this.state.utenti_allocati.length > 0 &&
            <div style = {{color: 'black'}}>
              Allocati:
            <ul>
              {this.state.utenti_allocati.map((user) => <li> {user.lastname} </li>) }
            </ul>
            </div>}

            {((this.state.data.reperibilitaAttiva === true) && (this.state.utenti_reperibili.length > 0)) &&
              <div style = {{color: 'black'}}>
                Reperibili:
                <ul>
                  {this.state.utenti_reperibili.map((user) => <li> {user.lastname} </li>)}
                </ul>
              </div>
            }

            {this.state.utenti_rimossi.length > 0 &&
            <div>
              <div>Rimossi:</div>
            <ul>
              {this.state.utenti_rimossi.map((user) => <li> <s>{user.lastname}</s></li>) }
            </ul>
            </div>
            }

          </div>
        </div>
      </StyledAppointmentsAppointmentContent>
    );
  }
  else {
    /**
     * Mostriamo solo il nome della festività
     */
    return (

      <Appointments.Appointment
        {...this.state.restProps}
        formatDate={this.state.formatDate}
        data={this.state.data}
        style={{
          backgroundColor: 'red',
        }}
      >
        <div className={classes.container}>
          <div style={{ textAlign: "center", color: "white", "fontFamily": "sans-serif", "fontWeight": "bold" }}>
            {this.state.data.title}
          </div>
        </div>
      </Appointments.Appointment>
    );
  }
  }
}
