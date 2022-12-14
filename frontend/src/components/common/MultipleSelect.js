import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import { TurnoAPI } from '../../API/TurnoAPI';
import { ServizioAPI } from '../../API/ServizioAPI';



export default function MultipleSelect(props) {
  const [servizio, setServizio] = React.useState('');
  const [turno, setTurno] = React.useState('');
  const [turni,setTurni] = React.useState([])
  const [servizi,setServizi] = React.useState([])

  const handleChangeServizio = (event) => {
    setServizio(event.target.value);
    getTurni(event.target.value)
    props.onSelectServizio(event.target.value)
  };

  const handleChangeTurno =(event) => {
    setTurno(event.target.value);
    props.onSelectTurno(event.target.value)
  };

  async function getTurni(servizio) {
    let turniAPI = new TurnoAPI();
    let turni = await turniAPI.getTurniByServizio(servizio);
    setTurni(turni);
  }

  async function getServizi() {
    let serviceAPI = new ServizioAPI();
    let servizi = await serviceAPI.getService()
    setServizi(servizi);
  }
  
  React.useEffect(() => {
      getServizi();
  }, [])


  return (
    <Stack spacing={3} >
        <Box sx={{ minWidth: 120 }}>
          <FormControl fullWidth>
            <InputLabel id="demo-simple-select-label">Servizi</InputLabel>
            <Select
              value={servizio}
              label= 'Servizi'
              onChange={handleChangeServizio}
            >
            
            {servizi.map((elementData) => (
                <MenuItem value={elementData}>{elementData} </MenuItem>

            ))}
            
            </Select>
          </FormControl>
        </Box>
        <Box sx={{ minWidth: 120 }}>
          <FormControl fullWidth>
            <InputLabel id="demo-simple-select-label">Turni</InputLabel>
            <Select
              value={turno}
              label= 'Turni'
              onChange={handleChangeTurno}
            >
            
            {turni.map((elementData) => (
                <MenuItem value={elementData}>{elementData} </MenuItem>

            ))}
            
            </Select>
          </FormControl>
        </Box>

    </Stack>
    
  );
}