import * as React from 'react';
import Paper from '@mui/material/Paper';
import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import {ViewState,} from '@devexpress/dx-react-scheduler';
import { UtenteAPI } from '../API/UtenteAPI';

import {
  Scheduler,
  Appointments,
  WeekView,
  AppointmentTooltip,
  CurrentTimeIndicator,
  DayView,
  ViewSwitcher,
  TodayButton,
  DateNavigator,
  Toolbar,
  MonthView, Resources,
} from '@devexpress/dx-react-scheduler-material-ui';
import styled from "@emotion/styled";
import Typography from '@mui/material/Typography';
import {
  Button,
  Box,
  Checkbox,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  Grid,
  IconButton,
  TextField,
} from "@mui/material";

const PREFIX = 'Demo';
// #FOLD_BLOCK
const classes = {
  flexibleSpace: `${PREFIX}-flexibleSpace`,
  textField: `${PREFIX}-textField`,
  locationSelector: `${PREFIX}-locationSelector`,
  button: `${PREFIX}-button`,
  selectedButton: `${PREFIX}-selectedButton`,
  longButtonText: `${PREFIX}-longButtonText`,
  shortButtonText: `${PREFIX}-shortButtonText`,
  title: `${PREFIX}-title`,
  textContainer: `${PREFIX}-textContainer`,
  time: `${PREFIX}-time`,
  text: `${PREFIX}-text`,
  container: `${PREFIX}-container`,
  weekendCell: `${PREFIX}-weekendCell`,
  weekEnd: `${PREFIX}-weekEnd`,
};
// #FOLD_BLOCK
const StyledAppointmentsAppointmentContent = styled(Appointments.AppointmentContent)(() => ({
  [`& .${classes.title}`]: {
    fontWeight: 'bold',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  [`& .${classes.textContainer}`]: {
    lineHeight: 1,
    whiteSpace: 'pre-wrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    width: '100%',
  },
  [`& .${classes.time}`]: {
    display: 'inline-block',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  [`& .${classes.text}`]: {
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  [`& .${classes.container}`]: {
    width: '100%',
  },
}));

const AppointmentContent = ({
                              data, formatDate, ...restProps
                            }) => (
  <StyledAppointmentsAppointmentContent {...restProps} formatDate={formatDate} data={data}>
    <div className={classes.container}>
      <div className={classes.title}>
        {data.title}
      </div>
      <div className={classes.test}>
        {data.tipologia}
      </div>
      <div className={classes.textContainer}>
        <div className={classes.time}>
          {formatDate(data.startDate, { hour: 'numeric', minute: 'numeric' })}
        </div>
        <div className={classes.time}>
          {' - '}
        </div>
        <div className={classes.time}>
          {formatDate(data.endDate, { hour: 'numeric', minute: 'numeric' })}
        </div>

      </div>
    </div>
  </StyledAppointmentsAppointmentContent>
);

export default class SingleScheduleView extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data:[],
    };

  }


  async componentDidMount() {
    let apiTurno = new AssegnazioneTurnoAPI();
    let turni = await apiTurno.getTurnByIdUser('1');
    let utenteAPI = new UtenteAPI();
    let utenti = await utenteAPI.getAllUser();

    this.setState(
      {
        data:turni,
        resources:
          [
            {
              fieldName: 'utenti_guardia', title: 'Guardia',allowMultiple: true, instances: utenti,
            }
            ,{
            fieldName:'utenti_reperibili', title: 'Reperibilità',allowMultiple: true, instances: utenti,
          },
          ],
      })
    this.changeMainResource = this.changeMainResource.bind(this);
  }

  changeMainResource(mainResourceName) {
    this.setState({ mainResourceName });
  }

  render() {
    const { data, resources } = this.state;

    return (
      <Paper>
        <Scheduler
          locale={"it-IT"}
          data={data}
          height={660}
          firstDayOfWeek={1}
          >
          <ViewState/>
          <WeekView
            displayName="Settimanale"
            startDayHour={0}
            endDayHour={24}
            cellDuration={120}
          />
          <DayView
            displayName="Giornaliero"
            startDayHour={0}
            endDayHour={24}
            cellDuration={120}
          />
          <MonthView displayName="Mensile" />
          <Appointments appointmentContentComponent={AppointmentContent} />
          <Resources
            data={resources}
          />
          <Toolbar />
          <DateNavigator/>
          <TodayButton  buttonComponent={(props) => {
            return (
              <Button onClick={() => props.setCurrentDate(new Date())}>
                Oggi
              </Button>
            );
          }}/>
          <AppointmentTooltip
            showCloseButton
          />
          <ViewSwitcher />
          <CurrentTimeIndicator
            shadePreviousAppointments={true}
            shadePreviousCells={true}
            updateInterval={60000}
          />
        </Scheduler>
      </Paper>

    );
  }
}
