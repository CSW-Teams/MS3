import * as React from 'react';
import Paper from '@mui/material/Paper';
import { AssegnazioneTurnoAPI } from '../API/AssegnazioneTurnoAPI';
import { ViewState } from '@devexpress/dx-react-scheduler';
import {
  Scheduler,
  DayView,
  WeekView,
  Appointments,
  Toolbar,
  ViewSwitcher,
} from '@devexpress/dx-react-scheduler-material-ui';

export default class InsertAssignedSheet extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data:  []
    };
  }

  async componentDidMount() {
    //let apiTurno = new AssegnazioneTurnoAPI();
    //let turni = await apiTurno.getTurnByIdUser('1');
    this.setState({data:[]})
  }

  render() {
    const { data } = this.state;

    return (
      <Paper>

      </Paper>
    );
  }

}
