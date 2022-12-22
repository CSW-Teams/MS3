/* eslint jsx-a11y/anchor-is-valid: 0 */

import React from 'react';
import {ViewState} from '@devexpress/dx-react-scheduler';
import { styled } from '@mui/material/styles';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import {
  Button,
  Paper,
} from "@mui/material";
import {
  Scheduler,
  Resources,
  Appointments,
  AppointmentTooltip,
  DayView,
  MonthView,
  DateNavigator,
  TodayButton,
  Toolbar,
  ViewSwitcher,
  WeekView, CurrentTimeIndicator,
} from '@devexpress/dx-react-scheduler-material-ui';


import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import { UtenteAPI } from '../API/UtenteAPI';
import {blue, red, teal} from "@mui/material/colors";


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

const StyledDiv = styled('div')(({ theme }) => ({
  [`&.${classes.container}`]: {
    display: 'flex',
    marginBottom: theme.spacing(2),
    justifyContent: 'flex-end',
  },
  [`& .${classes.text}`]: {
    ...theme.typography.h6,
    marginRight: theme.spacing(2),
  },
}));


class GlobalScheduleView extends React.Component {

  async componentDidMount() {

    let utenti = [];
    let turni = [];
    let turnoAPI = new AssegnazioneTurnoAPI();
    let utenteAPI = new UtenteAPI();

    utenti = await utenteAPI.getAllUser();
    turni = await turnoAPI.getGlobalTurn();


    this.setState({
        data:turni,
        mainResourceName: 'utenti_guardia',
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
  }


  constructor(props) {

    super(props);
    this.state = {
      data: [],
      mainResourceName: 'utenti_guardia',
      resources: [
        {fieldName: 'utenti_guardia', title: 'Guardia', allowMultiple: true,instances: [{}] , color:blue,},
        {fieldName: 'utenti_reperibili', title: 'Reperibilità',allowMultiple: true, instances: [{}]}, ]
    };
    this.changeMainResource = this.changeMainResource.bind(this);
  }

  changeMainResource(mainResourceName) {
    this.setState({ mainResourceName });
  }

  render() {
    const { data, resources} = this.state;

    return (
      <React.Fragment>

        <Paper>
          <Scheduler
            locale={"it-IT"}
            firstDayOfWeek={1}
            data={data}
            height={660}>
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
            <MonthView displayName="Mensile"/>
            <Toolbar />
            <Appointments appointmentContentComponent={AppointmentContent} />
            <Resources
              data={resources}
            />
            <DateNavigator />
            <TodayButton  buttonComponent={(props) => {
              return (
                <Button onClick={() => props.setCurrentDate(new Date())}>
                  Oggi
                </Button>
              );
            }}/>
            <ViewSwitcher />
            <AppointmentTooltip
              showCloseButton
            />
            <CurrentTimeIndicator
              shadePreviousAppointments={true}
              shadePreviousCells={true}
              updateInterval={60000}
            />
          </Scheduler>
        </Paper>
      </React.Fragment>
    );
  }
}

export default GlobalScheduleView;
