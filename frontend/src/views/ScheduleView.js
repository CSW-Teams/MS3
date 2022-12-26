/* eslint jsx-a11y/anchor-is-valid: 0 */

import React from 'react';
import {ViewState} from '@devexpress/dx-react-scheduler';
import { styled } from '@mui/material/styles';
import { ButtonGroup } from '@material-ui/core';
import { ServizioAPI } from '../API/ServizioAPI';
import Stack from '@mui/material/Stack';

import {
  Button, Grid,
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

import {blue} from "@mui/material/colors";
import {Room} from "@mui/icons-material";
import { getAppointmentColor, getResourceColor } from '../utils/utils';
import PropTypes from 'prop-types';
import classNames from 'clsx';
import AccessTime from '@mui/icons-material/AccessTime';
import Lens from '@mui/icons-material/Lens';
import { HOUR_MINUTE_OPTIONS, WEEKDAY_INTERVAL, viewBoundText } from '@devexpress/dx-scheduler-core';
import { ServiceFilterSelectorButton } from '../components/common/ServiceFilterSelectorButton';
import { UtenteAPI } from '../API/UtenteAPI';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';


const PREFIX_TOOLTIP = 'Content';

export const tooltip_classes = {
  content: `${PREFIX_TOOLTIP}-content`,
  text: `${PREFIX_TOOLTIP}-text`,
  title: `${PREFIX_TOOLTIP}-title`,
  icon: `${PREFIX_TOOLTIP}-icon`,
  lens: `${PREFIX_TOOLTIP}-lens`,
  lensMini: `${PREFIX_TOOLTIP}-lensMini`,
  textCenter: `${PREFIX_TOOLTIP}-textCenter`,
  dateAndTitle: `${PREFIX_TOOLTIP}-dateAndTitle`,
  titleContainer: `${PREFIX_TOOLTIP}-titleContainer`,
  contentContainer: `${PREFIX_TOOLTIP}-contentContainer`,
  resourceContainer: `${PREFIX_TOOLTIP}-resourceContainer`,
  recurringIcon: `${PREFIX_TOOLTIP}-recurringIcon`,
  relativeContainer: `${PREFIX_TOOLTIP}-relativeContainer`,
};

const StyledDiv = styled('div')(({
                                   theme: { spacing, palette, typography }, resources,
                                 }) => ({
  [`&.${tooltip_classes.content}`]: {
    padding: spacing(1.5, 1),
    paddingTop: spacing(1),
    backgroundColor: palette.background.paper,
    boxSizing: 'border-box',
    ...typography.body2,
  },
  [`& .${tooltip_classes.text}`]: {
    display: 'inline-block',
  },
  [`& .${tooltip_classes.title}`]: {
    ...typography.h6,
    color: palette.text.secondary,
    fontWeight: typography.fontWeightBold,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  [`& .${tooltip_classes.icon}`]: {
    verticalAlign: 'middle',
    color: palette.action.active,
  },
  [`& .${tooltip_classes.lens}`]: {
    color: getAppointmentColor(300, getResourceColor(resources), palette.primary),
    width: spacing(4.5),
    height: spacing(4.5),
    verticalAlign: 'super',
    position: 'absolute',
    left: '50%',
    transform: 'translate(-50%,0)',
  },
  [`& .${tooltip_classes.lensMini}`]: {
    width: spacing(2.5),
    height: spacing(2.5),
  },
  [`& .${tooltip_classes.textCenter}`]: {
    textAlign: 'center',
    height: spacing(2.5),
  },
  [`& .${tooltip_classes.dateAndTitle}`]: {
    lineHeight: 1.4,
  },
  [`& .${tooltip_classes.titleContainer}`]: {
    paddingBottom: spacing(2),
  },
  [`& .${tooltip_classes.contentContainer}`]: {
    paddingBottom: spacing(1.5),
  },
  [`& .${tooltip_classes.resourceContainer}`]: {
    paddingBottom: spacing(0.25),
  },
  [`& .${tooltip_classes.recurringIcon}`]: {
    position: 'absolute',
    paddingTop: spacing(0.875),
    left: '50%',
    transform: 'translate(-50%,0)',
    color: palette.background.paper,
    width: spacing(2.625),
    height: spacing(2.625),
  },
  [`& .${tooltip_classes.relativeContainer}`]: {
    position: 'relative',
    width: '100%',
    height: '100%',
  },
}));

export const Content = ({
                          className,
                          children,
                          appointmentData,
                          appointmentResources,
                          formatDate,
                          recurringIconComponent: RecurringIcon,
                          ...restProps
                        }) => {
  const weekDays = viewBoundText(
    appointmentData.startDate, appointmentData.endDate, WEEKDAY_INTERVAL,
    appointmentData.startDate, 1, formatDate,
  );
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
        <div className={tooltip_classes.text}> Di Guardia : </div>
      </Grid>
      { appointmentResources.slice(0, appointmentData.utenti_guardia.length).map(resourceItem => (
        <Grid container alignItems="center" className={tooltip_classes.resourceContainer} key={`${resourceItem.fieldName}_${resourceItem.id}`}>
          <Grid item xs={2} className={tooltip_classes.textCenter}>
            <div className={tooltip_classes.relativeContainer}>
              <Lens
                className={classNames(tooltip_classes.lens, tooltip_classes.lensMini)}
                style={{ color: getAppointmentColor(300, resourceItem.color) }}
              />
            </div>
          </Grid>
          <Grid item xs={10}>
            <div className={tooltip_classes.text}>
              {resourceItem.text}
            </div>
          </Grid>
        </Grid>
      ))}
      <Grid item xs={2}>
        <div className={tooltip_classes.text}> In Reperibilità: </div>
      </Grid>
      { appointmentResources.slice(appointmentData.utenti_guardia.length, appointmentData.utenti_guardia.length + appointmentData.utenti_reperibili.length).map(resourceItem => (
        <Grid container alignItems="center" className={tooltip_classes.resourceContainer} key={`${resourceItem.fieldName}_${resourceItem.id}`}>
          <Grid item xs={2} className={tooltip_classes.textCenter}>
            <div className={tooltip_classes.relativeContainer}>
              <Lens
                className={classNames(tooltip_classes.lens, tooltip_classes.lensMini)}
                style={{ color: getAppointmentColor(300, resourceItem.color) }}
              />
            </div>
          </Grid>
          <Grid item xs={10}>
            <div className={tooltip_classes.text}>
              {resourceItem.text}
            </div>
          </Grid>
        </Grid>
      ))}
      </Grid>
      {children}
    </StyledDiv>
  );
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

const verticalTopHorizontalCenterOptions = {
  vertical: "top",
  horizontal: "center"
};

const StyledGrid = styled(Grid)(() => ({
  [`&.${classes.textCenter}`]: {
    textAlign: 'center',
  },
}));

const StyledRoom = styled(Room)(({ theme: { palette } }) => ({
  [`&.${classes.icon}`]: {
    color: palette.action.active,
  },
}));


/**
 * This view defines a generic shift schedule view.
 * Children are expected to query shifts from their preferred backend API
 * before the render() method is fired, i.e. overriding the componentDidMount() method
 * 
 */
class ScheduleView extends React.Component{
     
    constructor(props) {
        super(props);
        this.state = {
            data: [],   // list of shifts to display in schedule (not filtered yet)
            mainResourceName: 'utenti_guardia',
            resources: [ // TODO: queste risorse sarebbero i dettagli da scrivere nelle carte dei turni assegnati? Ulteriori dettagli andrebbero aggiunti qui?
              {fieldName: 'utenti_guardia', title: 'Guardia', allowMultiple: true,instances: [{}] , color:blue,},
              {fieldName: 'utenti_reperibili', title: 'Reperibilità',allowMultiple: true, instances: [{}]},
            ],
            /**
             * Filter criteria are the attributes used by filters to choose if a shift can be displayed. 
             */
            filterCriteria: {
              
              /** what services we want to display? (default: all) */
              services: new Set(),
              users: [],
            
              // add more filter criteria here ...
            },
            /** all services registered in the system */
            allServices: new Set(), 
            allUser : [],
          };
          /** 
           * All filtering functions.
           * Each filter function must take a shift as input
           * and return true if the shift is feasible according to the filter conditions confronting
           * them to the filterCriteria specified in state.
           * We can adopt the following name convention to make the code more readable: 
           * @function filterBy\<MyCriterion\>(shift) -> boolean
           */ 
          this.filters = [
            
            function filterByServices(shift){
              let services = this.state.filterCriteria.services;
              return services.size === 0 || services.has(shift.servizio);
            }.bind(this),

            function filterByUsers(shift){
              let users = this.state.filterCriteria.users;
              
              for (let i = 0; i < shift.utenti_guardia.length; i++) 
                for (let j = 0; j < users.length; j++) 
                  if(users[j].id === shift.utenti_guardia[i])
                    return true;
                      
              return users.length === 0
            }.bind(this),

            // add more filters here ...
          ];
          this.changeMainResource = this.changeMainResource.bind(this);
          this.updateFilterCriteria = this.updateFilterCriteria.bind(this);      
    }

    changeMainResource(mainResourceName) {
      this.setState({ mainResourceName });
    }

    /**
     * This function is passed as a callback to the filter selectors components, which
     * can use it to change the filter criteria in ScheduleView state.
     * It merely consists of a decorator adding a call to forceUpdate() to the updateLogic function
     * defined by the filter selector.
     * the updateLogic() function must take the filterCriteria object as argument and change its properties values.
     * An example: function updateLogic(filterCriteria) { filterCriteria.myAttribute = myValue; }
     */
    updateFilterCriteria(updateLogic){
      updateLogic(this.state.filterCriteria);
      this.forceUpdate();
    }
 
    async componentDidMount(turni, utenti){
      let allServices = await new ServizioAPI().getService();
      let allUser = await new UtenteAPI().getAllUserOnlyNameSurname();
     
      this.setState(
        {
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
            allServices: new Set(allServices),
            allUser : allUser
        })
      }

    

    render(){

        let { data, resources} = this.state;

        /** Filtering of shifts is performed by ANDing results of all filter functions applied on each shift */
        data = data.filter((shift) => {
          return this.filters.reduce(
            (isFeasible, currentFilter) => isFeasible && currentFilter(shift, this.state.filterCriteria),
            true
          );
        });

        return (
          <React.Fragment>
            <Paper>
              <Stack spacing={1} style={{
                    display: 'flex',
                    'padding-top': '10px',
                    justifyContent: 'center',
                    'align-items': 'center'
                  }}>

                <Autocomplete
                  onChange={(event, value) => {
                    this.updateFilterCriteria(()=>this.state.filterCriteria.users= value)
                    }}
                  multiple
                  options={this.state.allUser}
                  sx={{ width: 300 }}
                  renderInput={(params) => <TextField {...params} label="Medici" />}
                />
                {/** Service Filter selectors */}
                <div style={{display : 'flex','justify-content': 'space-between','column-gap': '20px'}}>
                  {Array.from(this.state.allServices).map(
                    (service, i) => (
                      <ServiceFilterSelectorButton key={i} criterion={service} updateFilterCriteriaCallback={this.updateFilterCriteria}/>
                    ))}
                </div>
            
              </Stack>

              <Scheduler
                locale={"it-IT"}
                firstDayOfWeek={1}
                data={data}
                height={660}
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
                  contentComponent={Content}
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

export default ScheduleView;