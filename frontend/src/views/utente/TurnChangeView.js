import React from "react"
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {RequestTurnChangeAPI} from "../../API/RequestTurnChangeAPI";


export default class TurnChangeView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      turnChangeRequests: []
    };

    let requestAPI = new RequestTurnChangeAPI();
    let turnChangeRequests = requestAPI.getTurnChangeRequestsByIdUser(localStorage.getItem("id"));

    turnChangeRequests.then(data => {
      this.setState({ turnChangeRequests: data }); // Update state with the fetched data
    }).catch(error => {
      console.error("Error fetching turn change requests:", error);
    });
  }

  render() {
    const { turnChangeRequests } = this.state;
    // Sample data (replace with your actual data)
    const objects = [
      { id: 1, name: 'Object 1', description: 'Description for Object 1' },
      { id: 2, name: 'Object 2', description: 'Description for Object 2' },
      // Add more objects as needed
    ];

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
          {/* Map through objects and render table rows */}
          {objects.map((obj, index) => (
            <tr key={objects.id}>
              <td>{obj.id}</td>
              <td>{obj.name}</td>
              <td>{obj.description}</td>
              <td>{obj.name}</td>
              <td>
                {/* Buttons for each row */}
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
          </tr>
          </thead>
          <tbody>
          {turnChangeRequests.map((request, index) => (
            <tr key={request.requestId}>
              <td>{request.turnDescription}</td>
              <td>{request.inizioDate}</td>
              <td>{request.fineDate}</td>
              <td>{request.userDetails}</td>
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
