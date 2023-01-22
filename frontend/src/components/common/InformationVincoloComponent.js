import * as React from 'react';
import PropTypes from 'prop-types';
import { styled } from '@mui/material/styles';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import Typography from '@mui/material/Typography';
import HelpOutlineSharpIcon from '@mui/icons-material/HelpOutlineSharp';
import { VincoloAPI } from '../../API/VincoliAPI';

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiDialogContent-root': {
    padding: theme.spacing(2),
  },
  '& .MuiDialogActions-root': {
    padding: theme.spacing(1),
  },
}));


function BootstrapDialogTitle(props) {
  const { children, onClose, ...other } = props;

  return (
    <DialogTitle sx={{ m: 0, p: 2 }} {...other}>
      {children}
      {onClose ? (
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </DialogTitle>
  );
}

BootstrapDialogTitle.propTypes = {
  children: PropTypes.node,
  onClose: PropTypes.func.isRequired,
};

export default function InformationDialogs() {
  const [open, setOpen] = React.useState(false);
  const [vincoli,setVincoli] = React.useState([{}])

  React.useEffect(() => {
      getVincoli();
  }, []);

  async function getVincoli() {
      let vincoloApi = new VincoloAPI();
      let vincoli = await vincoloApi.getVincoli()
      setVincoli(vincoli);
  }

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
      

      <IconButton color="primary" style={{'margin-top':'-28%','margin-left':'85%'}} onClick={handleClickOpen}  >
         <HelpOutlineSharpIcon />
      </IconButton>

      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
      >
        <BootstrapDialogTitle id="customized-dialog-title" onClose={handleClose}>
          Quali sono i vincoli non stringenti ?
        </BootstrapDialogTitle>
        <DialogContent dividers>
          <Typography gutterBottom>
            I vincoli si dividono in due categorie : stringenti e non stringenti. I vincolo stringenti sono quelli 
            che per legge lo schedulo non può infrangere. I vincoli non stringeti sono invece vincoli che 
            all'occorrenza il pianificatore può decidere di violare.
          </Typography>
          <Typography gutterBottom>
            <h4>Vincoli non violabili</h4>
            <ul>
            {Array.from(vincoli).map((vincolo, i) => (
            !vincolo.violabile? <li>{ vincolo.descrizione }</li>:null
            ))}  
            </ul>
                    </Typography>
          <Typography gutterBottom>
          <h4>Vincoli violabili</h4>
          <ul>
          {Array.from(vincoli).map((vincolo, i) => (
            vincolo.violabile? <li>{ vincolo.descrizione }</li>:null
            ))}
          
          </ul>
          </Typography>
        </DialogContent>
      </BootstrapDialog>
    </div>
  );
}