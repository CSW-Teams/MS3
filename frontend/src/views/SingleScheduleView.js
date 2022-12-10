import * as React from 'react';
import Paper from '@mui/material/Paper';
import { TurnoAPI } from '../API/TurnoAPI';
import { ViewState } from '@devexpress/dx-react-scheduler';
import {
  Scheduler,
  DayView,
  WeekView,
  Appointments,
  Toolbar,
  ViewSwitcher,
} from '@devexpress/dx-react-scheduler-material-ui';


export default class SingleScheduleView extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data:  []
    };


  }

 
  async componentDidMount() {
    let apiTurno = new TurnoAPI();
    let turni = await apiTurno.getTurnByIdUser('1');
    this.setState({data:turni})
  }

  render() {
    const { data } = this.state;
    
    return (
      <Paper>
        <Scheduler
          data={data}
          height={660}
        >
          <ViewState
            defaultCurrentDate= {new Date()}
            defaultCurrentViewName="Week"
          />

          <DayView
            startDayHour={9}
            endDayHour={18}
          />
          <WeekView
            startDayHour={10}
            endDayHour={19}
          />

          <Toolbar />
          <ViewSwitcher />
          <Appointments />
        </Scheduler>
      </Paper>
    );
  }
}
