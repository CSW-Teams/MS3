import React from "react"
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {RequestTurnChangeAPI} from "../../API/RequestTurnChangeAPI";


export default class TurnChangeView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      turnChangeRequestsBySender: [],
      turnChangeRequestsToSender: []
    };

    let requestAPI = new RequestTurnChangeAPI();
    let turnChangeRequestsBySender = requestAPI.getTurnChangeRequestsByIdUser(localStorage.getItem("id"));
    let turnChangeRequestsToSender = requestAPI.getTurnChangeRequestsToIdUser(localStorage.getItem("id"));

    turnChangeRequestsBySender.then(data => {
      this.setState({ turnChangeRequestsBySender: data });
    }).catch(error => {
      console.error("Error fetching turn change requests:", error);
    });

    turnChangeRequestsToSender.then(data => {
      this.setState({ turnChangeRequestsToSender: data });
    }).catch(error => {
      console.error("Error fetching turn change requests to sender:", error);
    });
  }

  render() {
    const { turnChangeRequestsBySender } = this.state;
    const { turnChangeRequestsToSender } = this.state;

    return (
      <div className="Table-page-container">
        <h2>Richieste Ricevute</h2>
        <table className="table">
          <thead>
          <tr>
            <th>Turno</th>
            <th>Data e Ora Inizio</th>
            <th>Data e Ora Fine</th>
            <th>Richiedente</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          {turnChangeRequestsToSender.map((request, index) => (
            <tr key={request.requestId}>
              <td>{request.turnDescription}</td>
              <td>{request.inizioDate.toString()}</td>
              <td>{request.fineDate.toString()}</td>
              <td>{request.userDetails}</td>
              <td>
                <button className="btn btn-primary">Accetta</button>
                <button className="btn btn-secondary">Rifiuta</button>
              </td>
            </tr>
          ))}
          </tbody>
        </table>
        <h2>Richieste Inviate</h2>
        <table className="table">
          <thead>
          <tr>
            <th>Turno</th>
            <th>Data e Ora Inizio</th>
            <th>Data e Ora Fine</th>
            <th>Destinatario</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>
          {turnChangeRequestsBySender.map((request, index) => (
            <tr key={request.requestId}>
              <td>{request.turnDescription}</td>
              <td>{request.inizioDate.toString()}</td>
              <td>{request.fineDate.toString()}</td>
              <td>{request.userDetails}</td>
              <td>{request.status}</td>
            </tr>
          ))}
          </tbody>
        </table>
        <ToastContainer
          position="top-center"
          autoClose={5000}
          hideProgressBar={true}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />
      </div>
    )
  }

}
