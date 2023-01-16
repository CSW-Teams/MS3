import React from "react";
import {Grid} from "@mui/material";
import {getAppointmentColor} from "../../utils/utils";
import AccessTime from '@mui/icons-material/AccessTime';
import Lens from '@mui/icons-material/Lens';
import {HOUR_MINUTE_OPTIONS, WEEKDAY_INTERVAL, viewBoundText } from '@devexpress/dx-scheduler-core';
import {StyledAppointmentsAppointmentContent, tooltip_classes} from "./style";
import classNames from "clsx";
import PropTypes from "prop-types";
import {classes,StyledDiv} from "./style"
import { SchedulableType } from "../../API/Schedulable";


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


// AppointmentContent di GlobalScheduleView
export const AppointmentContent = ({
                              data, formatDate, ...restProps
                            }) => (
  <StyledAppointmentsAppointmentContent {...restProps} formatDate={formatDate} data={data}>
    <div className={classes.container}>
      <div style={{textAlign: "center", color: "white","fontFamily":"sans-serif", "font-weight":"bold"}}>
        {data.title}
      </div>
      <li className={classes.text}>
        {data.tipologia}
      </li>
      <li className={classes.textContainer}>
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

