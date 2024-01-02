import React from "react"
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {ShiftChangeRequestAPI} from "../../API/ShiftChangeRequestAPI";


export default class ShiftChangeView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      turnChangeRequestsBySender: [],
      turnChangeRequestsToSender: []
    };

    let requestAPI = new ShiftChangeRequestAPI();
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

    const sortedRequestsBySender = turnChangeRequestsBySender.sort((a, b) => {
      return new Date(a.inizioDate) - new Date(b.inizioDate);
    });

    const { turnChangeRequestsToSender } = this.state;

    const sortedRequestsToSender = turnChangeRequestsToSender.sort((a, b) => {
      return new Date(a.inizioDate) - new Date(b.inizioDate);
    });

    const options = {
      timeZone: 'Europe/Berlin',
      weekday: 'long',
      day: "numeric",
      month: 'long',
      year: 'numeric',
      hour12: false,
      hour: 'numeric',
      minute: 'numeric',
    };

    return (
      <div className="Table-page-container" style={{padding: '20px'}}>
        <style>
          {`
            .h2-padding {
              margin-top: 20px;
              margin-bottom: 20px;
            }
          `}
        </style>
        <h2 className="h2-padding">Richieste Ricevute</h2>
        <table className="table" style={{borderRadius: '8px'}}>
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
          {sortedRequestsToSender.map((request, index) => (
            <tr key={request.requestId}>
              <td>{request.turnDescription}</td>
              <td>{request.inizioDate.toLocaleString('it-IT', options)}</td>
              <td>{request.fineDate.toLocaleString('it-IT', options)}</td>
              <td>{request.userDetails}</td>
              <td>
                <button className="btn btn-primary"
                        style={{marginRight: '8px'}}>Accetta
                </button>
                <button className="btn btn-secondary">Rifiuta</button>
              </td>
            </tr>
          ))}
          </tbody>
        </table>
        <h2 className="h2-padding">Richieste Inviate</h2>
        <table className="table" style={{borderRadius: '8px'}}>
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
          {sortedRequestsBySender.map((request, index) => (
            <tr key={request.requestId}>
              <td>{request.turnDescription}</td>
              <td>{request.inizioDate.toLocaleString('it-IT', options)}</td>
              <td>{request.fineDate.toLocaleString('it-IT', options)}</td>
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
        <div style={{marginTop: 'auto'}}></div>
      </div>
    )
  }

}
