import React, {useState} from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Box from "@mui/material/Box";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle
} from "@mui/material";
import FilesUpload from "../common/FilesUpload";
import TemporaryDrawerRetirement from "./BottomViewGestisciRitiro";

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




const RequestsTable = ({ requests, isLocal, shifts, users }) => {
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
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    </Box>
  );
};

export default RequestsTable;
