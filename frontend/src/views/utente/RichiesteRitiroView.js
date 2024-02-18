import React, {useState} from "react"
import {RichiestaRimozioneDaTurnoAPI} from "../../API/RichiestaRimozioneDaTurnoAPI";
//import RequestsTable from "../../components/common/TabellaRichiesteRitiro"
import {TurnoAPI} from "../../API/TurnoAPI";
import {AssegnazioneTurnoAPI} from "../../API/AssegnazioneTurnoAPI";
import {UserAPI} from "../../API/UserAPI";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle
} from "@mui/material";
import FilesUpload from "../../components/common/FilesUpload";
import Box from "@mui/material/Box";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import TemporaryDrawerRetirement
  from "../../components/common/BottomViewGestisciRitiro";
import {DoctorAPI} from "../../API/DoctorAPI";
import { t } from "i18next";
import {toast} from "react-toastify";
import {panic} from "../../components/common/Panic";


const ModalLinkFile = ({request, updateRequest}) => {
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <>
      <Button onClick={handleOpen}>
        {t("Add file")}
      </Button>

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{t("Add file")}</DialogTitle>
        <DialogContent>
          <FilesUpload type={"retirement"} idRequest={request.idRequest} request={request} updateRequest={updateRequest} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            {t("Close")}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

const getSostituto = (users, request) => {
  if (request.idSubstitute === null)
    return null;
  let u = users.find(user => user.id === request.idSubstitute);
  let seniority = t(u.seniority)
  return u.name + " " + u.lastname + " - " + seniority;
}

export default class RichiesteRitiroView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      doctors: [],
      shifts: [],
      requests: [],         // list of all retirement requests
      userRequests: [],     // list of all user's retirement requests
      isLocal: false,       // if true, only user's retirement requests should be showed, oth. all users' requests
    };
  }

  async componentDidMount() {
    let apiDoctors = new DoctorAPI();
    let apiRetirement = new RichiestaRimozioneDaTurnoAPI();
    let apiShifts = new AssegnazioneTurnoAPI();

    let doctors
    try {
      doctors = await apiDoctors.getAllDoctorsInfo();
    } catch (err) {

      panic()
      return
    }
    let shifts
    try {
      shifts = await apiShifts.getGlobalShift();
    } catch (err) {

      panic()
      return
    }
    this.setState({doctors: doctors});
    this.setState({shifts: shifts})
    const searchParams = new URLSearchParams(this.props.location.search);
    try {

      const local = searchParams.get('locale');
      if (local === "true") {
        this.setState({isLocal: true});
        let requestsForUser = await apiRetirement.getAllRequestsForUser(localStorage.getItem("id"));
        this.setState({userRequests: requestsForUser});
      } else {
        let allRequests = await apiRetirement.getAllRequests();
        this.setState({requests: allRequests});
      }
    } catch (err) {

      panic()
      return
    }
  }

  updateRequest = (updatedRequest) => {
    console.log("Updating request. New allegato:", updatedRequest.allegato);
    console.log(this.state.requests);
    const newRequests = this.state.requests.filter(request => request.idRichiestaRimozioneDaTurno !== updatedRequest.idRichiestaRimozioneDaTurno);
    newRequests.push(updatedRequest);
    this.setState({requests: newRequests});
  };

  getDoctor = (request) => {
    const doctor = this.state.doctors.find(user => user.id === request.idRequestingUser);
    const seniority = t(doctor.seniority);
    return doctor.name + " " + doctor.lastname + " - " + seniority;
  }

  render(view) {
    if (this.state.isLocal) {
      return (
        <React.Fragment>
          <Box mt={2} ml={2} mr={2} mb={2}>
            <TableContainer component={Paper}>
              <Table>
                <caption> Richieste di ritiro da turni </caption>
                <TableHead>
                  <TableRow>
                    <TableCell>{t("ID")}</TableCell>
                    <TableCell>{t(  "Requester")}</TableCell>
                    <TableCell>{t(  "Justification")}</TableCell>
                    <TableCell>{t(  "Status")}</TableCell>
                    <TableCell>{t(  "Outcome")}</TableCell>
                    <TableCell>{t(  "Attachment")}</TableCell>
                    <TableCell>{t(  "Substitute")}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {this.state.userRequests.map((request) => (
                    <TableRow key={request.idRequest}>
                      <TableCell>{request.idRequest}</TableCell>
                      <TableCell>{this.getDoctor(request)}</TableCell>
                      <TableCell>{request.justification}</TableCell>
                      <TableCell>{request.examined ?   t("Examined"): t("Waiting")}</TableCell>
                      <TableCell>
                        <div
                          style={{
                            width: '20px',
                            height: '20px',
                            borderRadius: '50%',
                            backgroundColor: request.examined ?
                              request.outcome ?
                                'green'
                                : 'red'
                              : 'lightgray',
                          }}
                        />
                      </TableCell>
                      <TableCell>
                        {request.file === null ?
                          <ModalLinkFile request={request} updateRequest={this.updateRequest}/>
                          : t("File Attached")}
                      </TableCell>
                      <TableCell>{getSostituto(this.state.doctors, request)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        </React.Fragment>
      )
    } else {
      return (
        <React.Fragment>
          <Box mt={2} ml={2} mr={2} mb={2}>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t("ID")}</TableCell>
                    <TableCell>{t(  "Requester")}</TableCell>
                    <TableCell>{t(  "Justification")}</TableCell>
                    <TableCell>{t(  "Status")}</TableCell>
                    <TableCell>{t(  "Outcome")}</TableCell>
                    <TableCell>{t(  "Processing")}</TableCell>
                    <TableCell>{t(  "Substitute")}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {this.state.requests.map((request) => (
                    <TableRow key={request.idRequest}>
                      <TableCell>{request.idRequest}</TableCell>
                      <TableCell>{this.getDoctor(request)}</TableCell>
                      <TableCell>{request.justification}</TableCell>
                      <TableCell>{request.examined ? t("Examined"): t("Waiting")}</TableCell>
                      <TableCell>
                        <div
                          style={{
                            width: '20px',
                            height: '20px',
                            borderRadius: '50%',
                            backgroundColor: request.examined ?
                              request.outcome ?
                                'green'
                                : 'red'
                              : 'lightgray',
                          }}
                        />
                      </TableCell>
                      <TableCell>
                        {request.examined ?
                          t("Processed Request")
                          : <TemporaryDrawerRetirement request={request} shifts={this.state.shifts} users={this.state.doctors} updateRequest={this.updateRequest}/>
                        }
                      </TableCell>
                      <TableCell>{
                        getSostituto(this.state.doctors, request)
                      }</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        </React.Fragment>
      )
    }
  }
}
