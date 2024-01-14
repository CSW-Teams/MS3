import React from "react"
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {ShiftChangeRequestAPI} from "../../API/ShiftChangeRequestAPI";
import {Button} from "@mui/material";


export default class ShiftChangeView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      turnChangeRequestsBySender: [],
      turnChangeRequestsToSender: []
    };

    let requestAPI = new ShiftChangeRequestAPI();
    let turnChangeRequestsBySender = requestAPI.getTurnChangeRequestsByIdUser(localStorage.getItem("id"));
    console.log(turnChangeRequestsBySender);
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

  handleAccept = (requestId) => {
    ShiftChangeRequestAPI.answerRequest(requestId, true);
    console.log(`Request ${requestId} accepted`);
  };

  handleReject = (requestId) => {
    let answerAPI = ShiftChangeRequestAPI.answerRequest(requestId, false);
    console.log(`Request ${requestId} rejected`);
  };

  render() {
    const { turnChangeRequestsBySender } = this.state;

    const currentLocale = navigator.language;
    console.log(currentLocale)

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
          {sortedRequestsToSender.map((request, index) => {
            const startDate = new Date(request.inizioDate);
            const endDate = new Date(request.fineDate);
            return (
            <tr key={request.requestId}>
              <td>{request.turnDescription[currentLocale] || request.turnDescription["en"]}</td>
              <td>{startDate.toLocaleString(navigator.language, options)}</td>
              <td>{endDate.toLocaleString(navigator.language, options)}</td>
              <td>{request.userDetails}</td>
              <td>
                <button className="btn btn-primary"
                        style={{marginRight: '8px'}}
                        onClick={() => this.handleAccept(request.requestId)}>
                  Accetta
                </button>
                <button className="btn btn-secondary"
                        onClick={() => this.handleReject(request.requestId)}>
                  Rifiuta
                </button>
              </td>
            </tr>
          )})}
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
          {sortedRequestsBySender.map((request, index) => {
            const startDate = new Date(request.inizioDate);
            const endDate = new Date(request.fineDate);
            return (
              <tr key={request.requestId}>
                <td>{request.turnDescription[currentLocale] || request.turnDescription["en"]}</td>
                <td>{startDate.toLocaleString(navigator.language, options)}</td>
                <td>{endDate.toLocaleString(navigator.language, options)}</td>
                <td>{request.userDetails}</td>
                <td>{request.status[currentLocale] || request.status["en"]}</td>
              </tr>
            );
          })}
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
