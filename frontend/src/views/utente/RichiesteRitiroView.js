import React from "react"
import {RichiestaRimozioneDaTurnoAPI} from "../../API/RichiestaRimozioneDaTurnoAPI";
import RequestsTable from "../../components/common/TabellaRichiesteRitiro"
import {TurnoAPI} from "../../API/TurnoAPI";
import {AssegnazioneTurnoAPI} from "../../API/AssegnazioneTurnoAPI";
import {UtenteAPI} from "../../API/UtenteAPI";


export default class RichiesteRitiroView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      users: [],
      shifts: [],
      requests: [],         // list of all retirement requests
      userRequests: [],     // list of all user's retirement requests
      isLocal: false,       // if true, only user's retirement requests should be showed, oth. all users' requests
    };
  }

  async componentDidMount() {

    let apiUser = new UtenteAPI();
    let apiRetirement = new RichiestaRimozioneDaTurnoAPI();
    let apiShifts = new AssegnazioneTurnoAPI();

    const users = await apiUser.getAllUser();
    this.setState({users: users});
    const shifts = await apiShifts.getGlobalTurn();
    this.setState({shifts: shifts})
    const searchParams = new URLSearchParams(this.props.location.search);
    const local = searchParams.get('locale');
    if (local === "true") {
      this.setState({isLocal:true});
      let requestsForUser = await apiRetirement.getAllRequestsForUser(localStorage.getItem("id"));
      this.setState({userRequests:requestsForUser});
    } else {
      let allRequests = await apiRetirement.getAllRequests();
      this.setState({requests: allRequests});
    }

  }

  render(view) {
    if (this.state.isLocal) {
      console.log("Richieste di ritiro:", this.state.userRequests);
      return (
        <React.Fragment>
          <RequestsTable requests={this.state.userRequests} isLocal={true} shifts={this.state.shifts} users={this.state.users}></RequestsTable>
        </React.Fragment>
      )
    } else {
      return (
        <React.Fragment>
          <RequestsTable requests={this.state.requests} isLocal={false} shifts={this.state.shifts} users={this.state.users}></RequestsTable>
        </React.Fragment>
      )
    }
  }
}
