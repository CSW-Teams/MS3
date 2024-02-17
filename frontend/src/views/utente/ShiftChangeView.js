import React from "react";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { ShiftChangeRequestAPI } from "../../API/ShiftChangeRequestAPI";
import { Button } from "@mui/material";
import { useTranslation } from 'react-i18next';

export default function ShiftChangeView() {
  const { t } = useTranslation();

  const [state, setState] = React.useState({
    turnChangeRequestsBySender: [],
    turnChangeRequestsToSender: []
  });

  const requestAPI = new ShiftChangeRequestAPI();

  React.useEffect(() => {
    fetchData();
    const intervalId = setInterval(() => fetchData(), 6000);
    return () => clearInterval(intervalId); // Cleanup on component unmount
  }, []);

  const handle = (requestId, response) => {
    try {
      requestAPI.answerRequest(requestId, response);
    } catch (err) {

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
    }
    console.log(`Request ${requestId} accepted`);
  };

  const fetchData = async () => {
    try {
      const turnChangeRequestsBySender = await requestAPI.getTurnChangeRequestsByIdUser(localStorage.getItem("id"));
      const turnChangeRequestsToSender = await requestAPI.getTurnChangeRequestsToIdUser(localStorage.getItem("id"));
      setState({
        turnChangeRequestsBySender,
        turnChangeRequestsToSender
      });
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  };

  const renderTable = (requests, headerText) => {
    const sortedRequests = requests.sort((a, b) => new Date(a.inizioDate) - new Date(b.inizioDate));

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
      <>
        <h2 className="h2-padding">{headerText}</h2>
        <table className="table" style={{ borderRadius: '8px' }}>
          <thead>
          <tr>
            <th>{t('Shift')}</th>
            <th>{t('Start Date and Time')}</th>
            <th>{t('End Date and Time')}</th>
            <th>{t('User')}</th>
            <th>{t('Actions')}</th>
          </tr>
          </thead>
          <tbody>
          {sortedRequests.map((request, index) => {
            const startDate = new Date(request.inizioDate);
            const endDate = new Date(request.fineDate);
            return (
              <tr key={request.requestId}>
                <td>{request.turnDescription[t('en')]}</td>
                <td>{startDate.toLocaleString(navigator.language, options)}</td>
                <td>{endDate.toLocaleString(navigator.language, options)}</td>
                <td>{request.userDetails}</td>
                <td>
                  <Button variant="contained" color="primary" style={{ marginRight: '8px' }} onClick={() => handle(request.requestId, true)}>
                    {t('Accept')}
                  </Button>
                  <Button variant="contained" color="secondary" onClick={() => handle(request.requestId, false)}>
                    {t('Reject')}
                  </Button>
                </td>
              </tr>
            );
          })}
          </tbody>
        </table>
      </>
    );
  };

  return (
    <div className="Table-page-container" style={{ padding: '20px' }}>
      <style>
        {`
          .h2-padding {
            margin-top: 20px;
            margin-bottom: 20px;
          }
        `}
      </style>

      {renderTable(state.turnChangeRequestsToSender, t('Requests Received'))}
      {renderTable(state.turnChangeRequestsBySender, t('Requests Sent'))}

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
      <div style={{ marginTop: 'auto' }}></div>
    </div>
  );
}
