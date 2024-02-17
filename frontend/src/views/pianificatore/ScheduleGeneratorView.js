import React from "react";
import Button from '@mui/material/Button';
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBCol,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import TemporaryDrawerSchedule from "../../components/common/BottomViewAggiungiSchedulazione";
import {ScheduleAPI} from "../../API/ScheduleAPI";
import { t } from "i18next";

/*
* Schermata che permette di generare un nuovo schedule
*/
export class SchedulerGeneratorView extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            dataStart: "",
            dataEnd: "",
            schedulazioni: [{}],
            loading: false,
        }

        this.componentDidMount = this.componentDidMount.bind(this);
    }

    async componentDidMount() {
      let schedulazioni
      try {
        schedulazioni = await(new ScheduleAPI().getSchedulazini());
      } catch (err) {

        toast(t('Connection Error, please try again later'), {
          position: 'top-center',
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
        return
      }

      this.setState({
        schedulazioni: schedulazioni,
      })

    }

    async handleDelete(idSchedule) {

      this.setState({loading: true});

      let scheduleAPI = new ScheduleAPI();
      let responseStatus;
      try {
        responseStatus = await scheduleAPI.deleteSchedule(idSchedule);
      } catch (err) {

        toast(t('Connection Error, please try again later'), {
          position: 'top-center',
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
        return
      }

      if (responseStatus === 200) {
        this.componentDidMount()
        toast.success(t("Schedule successfully deleted"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      } else if (responseStatus === 400) {
        toast.error(t("Error during removal"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      }else if (responseStatus === 417) {
        toast.error(t("Deleting an old Schedule is not allowed"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      }

      this.setState({loading: false});
    }


    async handleRegeneration(idSchedule) {
      let scheduleAPI = new ScheduleAPI();
      let responseStatus;
      try {
        responseStatus = await scheduleAPI.rigeneraSchedule(idSchedule);
      } catch (err) {

        toast(t('Connection Error, please try again later'), {
          position: 'top-center',
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
        return
      }

      if (responseStatus === 202) {
        this.componentDidMount()
        toast.success(t("Schedule successfully recreated"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      } else if (responseStatus === 417) {
        toast.error(t("Old Schedules cannot be regenerated"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      }

      else {
        toast.error(t("Regeneration Error"), {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      }
    }


  render() {
    return (
      <section>
        <TemporaryDrawerSchedule onPostGeneration={this.componentDidMount}></TemporaryDrawerSchedule>
        <MDBContainer className="py-5">
          <MDBCard alignment='center' style={{ maxHeight: '70vh', overflowY: 'auto' }}>
            <MDBCardBody style={{ height: '64vh' }}>
              <MDBCardTitle>{t("Schedule management")}</MDBCardTitle>
              <MDBRow>
                <MDBCol></MDBCol>
              </MDBRow>
              <MDBRow>
                <MDBTable align="middle" bordered small hover>
                  <MDBTableHead color='tempting-azure-gradient' textwhite>
                    <tr>
                      <th scope='col'>{t("Start Date")}</th>
                      <th scope='col'>{t("End Date")}</th>
                      <th scope='col'>{t("Status")}</th>
                      <th scope='col'> </th>
                      <th scope='col'> </th>
                    </tr>
                  </MDBTableHead>
                  <MDBTableBody>
                    {this.state.schedulazioni.map((schedule, key) => {
                      const millisecondsInDay = 86400000; // 24 * 60 * 60 * 1000
                      const initialDayMillis = schedule.initialDate * millisecondsInDay;
                      const finalDayMillis = schedule.finalDate * millisecondsInDay;

                      const options = {
                        timeZone: 'Europe/Berlin',
                        weekday: 'long',
                        day: "numeric",
                        month: 'long',
                        year: 'numeric',
                      };

                      const startDate = new Date(initialDayMillis);
                      const endDate = new Date(finalDayMillis);

                      return (
                        <tr key={key}>
                          <td className="align-middle">{startDate.toLocaleString(navigator.language, options)}</td>
                          <td className="align-middle">{endDate.toLocaleString(navigator.language, options)}</td>
                          <td className="align-middle">{schedule.isIllegal ? "Incompleta" : "Completa"}</td>
                          <td className="align-middle">
                            <IconButton
                              aria-label="delete"
                              onClick={() => this.handleDelete(schedule.id)}>
                              <DeleteIcon />
                            </IconButton>
                          </td>
                          <td className="align-middle">
                            {schedule === this.state.schedulazioni[this.state.schedulazioni.length - 1] &&
                            <Button onClick={() => this.handleRegeneration(schedule.id)}>Rigenera</Button>
                            }
                          </td>
                        </tr>
                      )
                    })}
                  </MDBTableBody>
                </MDBTable>
              </MDBRow>

              {this.state.loading && (
                <div className="loading-overlay">
                  <div className="loading-spinner"></div>
                </div>
              )}

            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
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
      </section>
    )
  }


}
