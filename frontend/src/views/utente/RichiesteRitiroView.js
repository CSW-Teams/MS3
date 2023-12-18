import React, {useState} from "react"
import {RichiestaRimozioneDaTurnoAPI} from "../../API/RichiestaRimozioneDaTurnoAPI";
//import RequestsTable from "../../components/common/TabellaRichiesteRitiro"
import {TurnoAPI} from "../../API/TurnoAPI";
import {AssegnazioneTurnoAPI} from "../../API/AssegnazioneTurnoAPI";
import {UtenteAPI} from "../../API/UtenteAPI";
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


const ModalLinkFile = ({request}) => {
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <>
      <Button onClick={handleOpen}>
        Allega file
      </Button>

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Allega file</DialogTitle>
        <DialogContent>
          <FilesUpload type={"retirement"} idRequest={request.id} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Chiudi
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

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

    console.log("mounting component")

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
      this.setState({isLocal: true});
      let requestsForUser = await apiRetirement.getAllRequestsForUser(localStorage.getItem("id"));
      this.setState({userRequests: requestsForUser});
    } else {
      let allRequests = await apiRetirement.getAllRequests();
      this.setState({requests: allRequests});
    }

  }

  getSubstitute = (request, users) => {
    let u = users.find(user => user.id === request.idSubstitute);
    console.log("Sostituto:", u);
  }



  RequestsTable = ({ requests, isLocal, shifts, users }) => {
    console.log("Richieste:", requests);
    return (
      <Box mt={2} ml={2} mr={2} mb={2}>
        <TableContainer component={Paper}>
          <Table>
            <caption> Richieste di ritiro da turni </caption>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>ID Utente</TableCell>
                <TableCell>Giustificazione</TableCell>
                <TableCell>Stato</TableCell>
                <TableCell>Esito</TableCell>
                {isLocal &&
                  <TableCell>
                    Allegato
                  </TableCell>
                }
                {!isLocal &&
                  <TableCell>
                    Processamento
                  </TableCell>
                }
                {/* <TableCell>Sostituto</TableCell> */}
              </TableRow>
            </TableHead>
            <TableBody>
              {requests.map((request) => (
                <TableRow key={request.id}>
                  <TableCell>{request.id}</TableCell>
                  <TableCell>{request.idUser}</TableCell>
                  <TableCell>{request.justification}</TableCell>
                  <TableCell>{request.examinated ? 'Esaminata' : 'In attesa'}</TableCell>
                  <TableCell>
                    <div
                      style={{
                        width: '20px',
                        height: '20px',
                        borderRadius: '50%',
                        backgroundColor: request.examinated ?
                          request.outcome ?
                            'green'
                            : 'red'
                          : 'lightgray',
                      }}
                    />
                  </TableCell>
                  {isLocal &&
                    <TableCell>
                      {request.file === null ?
                        <ModalLinkFile request={request}/>
                        : "Allegato presente"}
                    </TableCell>
                  }
                  {!isLocal &&
                    <TableCell>
                      {request.examinated ?
                        "Richiesta processata"
                        : <TemporaryDrawerRetirement request={request} shifts={shifts} users={users}/>
                      }
                    </TableCell>
                  }
                  {/*<TableCell>{getSubstitute(request, users)}</TableCell>*/}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    );
  };

  render(view) {
    if (this.state.isLocal) {
      console.log("Richieste di ritiro:", this.state.userRequests);
      return (
        <React.Fragment>
          <Box mt={2} ml={2} mr={2} mb={2}>
            <TableContainer component={Paper}>
              <Table>
                <caption> Richieste di ritiro da turni </caption>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>ID Utente</TableCell>
                    <TableCell>Giustificazione</TableCell>
                    <TableCell>Stato</TableCell>
                    <TableCell>Esito</TableCell>
                    <TableCell>Allegato</TableCell>
                    {/* <TableCell>Sostituto</TableCell> */}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {this.state.userRequests.map((request) => (
                    <TableRow key={request.id}>
                      <TableCell>{request.id}</TableCell>
                      <TableCell>{request.idUser}</TableCell>
                      <TableCell>{request.justification}</TableCell>
                      <TableCell>{request.examinated ? 'Esaminata' : 'In attesa'}</TableCell>
                      <TableCell>
                        <div
                          style={{
                            width: '20px',
                            height: '20px',
                            borderRadius: '50%',
                            backgroundColor: request.examinated ?
                              request.outcome ?
                                'green'
                                : 'red'
                              : 'lightgray',
                          }}
                        />
                      </TableCell>
                      <TableCell>
                        {request.file === null ?
                          <ModalLinkFile request={request}/>
                          : "Allegato presente"}
                      </TableCell>
                      {/*<TableCell>{getSubstitute(request, users)}</TableCell>*/}
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
                <caption> Richieste di ritiro da turni </caption>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>ID Utente</TableCell>
                    <TableCell>Giustificazione</TableCell>
                    <TableCell>Stato</TableCell>
                    <TableCell>Esito</TableCell>
                    <TableCell>Processamento</TableCell>
                    {/* <TableCell>Sostituto</TableCell> */}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {this.state.requests.map((request) => (
                    <TableRow key={request.id}>
                      <TableCell>{request.id}</TableCell>
                      <TableCell>{request.idUser}</TableCell>
                      <TableCell>{request.justification}</TableCell>
                      <TableCell>{request.examinated ? 'Esaminata' : 'In attesa'}</TableCell>
                      <TableCell>
                        <div
                          style={{
                            width: '20px',
                            height: '20px',
                            borderRadius: '50%',
                            backgroundColor: request.examinated ?
                              request.outcome ?
                                'green'
                                : 'red'
                              : 'lightgray',
                          }}
                        />
                      </TableCell>
                      <TableCell>
                        {request.examinated ?
                          "Richiesta processata"
                          : <TemporaryDrawerRetirement request={request} shifts={this.state.shifts} users={this.state.users}/>
                        }
                      </TableCell>
                      {/*<TableCell>{getSubstitute(request, users)}</TableCell>*/}
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
