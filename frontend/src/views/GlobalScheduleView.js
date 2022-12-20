/* eslint jsx-a11y/anchor-is-valid: 0 */

import React from 'react';
import Paper from '@mui/material/Paper';
import {
  ViewState, GroupingState, IntegratedGrouping, IntegratedEditing, EditingState,
} from '@devexpress/dx-react-scheduler';
import {
  Scheduler,
  Resources,
  Appointments,
  AppointmentTooltip,
  GroupingPanel,
  DayView,
  DragDropProvider,
  AppointmentForm,
} from '@devexpress/dx-react-scheduler-material-ui';

import { TurnoAPI } from '../API/TurnoAPI';
import { UtenteAPI } from '../API/UtenteAPI';


class GlobalScheduleView extends React.Component {

  async componentDidMount() {
    let utenti = [];
    let turni = [];
    let turnoAPI = new TurnoAPI();
    let utenteAPI = new UtenteAPI();

    utenti = await utenteAPI.getAllUser();
    turni = await turnoAPI.getGlobalTurn();

    this.setState({data:turni})
    this.setState({resources: [{
        fieldName: 'members',
        title: 'Members',
        instances: utenti,
        allowMultiple: true,
      }],})

  }

  constructor(props) {

    super(props);
    this.state = {
      data: [],
      resources: [{
        fieldName: 'members',
        title: 'Members',
        instances: [{}],
        allowMultiple: true,
      }],

    };

    this.commitChanges = this.commitChanges.bind(this);
  }


  commitChanges({ added, changed, deleted }) {
    this.setState((state) => {
      let { data } = state;
      if (added) {
        const startingAddedId = data.length > 0 ? data[data.length - 1].id + 1 : 0;
        data = [...data, { id: startingAddedId, ...added }];
      }
      if (changed) {
        data = data.map(appointment => (
          changed[appointment.id] ? { ...appointment, ...changed[appointment.id] } : appointment));
      }
      if (deleted !== undefined) {
        data = data.filter(appointment => appointment.id !== deleted);
      }
      return { data };
    });
  }

  render() {
    const { data, resources } = this.state;

    return (
      <Paper>
        <Scheduler
          data={data}
        >
          <ViewState
            defaultCurrentDate={new Date()}
          />
          <EditingState
            onCommitChanges={this.commitChanges}
          />

          <DayView
            startDayHour={0}
            endDayHour={24}
            intervalCount={7}
            cellDuration={120}
          />
          <Appointments />
          <Resources
            data={resources}
            mainResourceName="members"
          />

          <IntegratedEditing />
          <AppointmentTooltip showOpenButton />
          <AppointmentForm />
          <DragDropProvider />
        </Scheduler>
      </Paper>
    );
  }
}

export default GlobalScheduleView;
