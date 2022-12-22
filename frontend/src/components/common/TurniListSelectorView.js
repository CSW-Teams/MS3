import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { TurnoAPI } from '../../API/TurnoAPI';

export default function MultipleSelect(props) {
  const [age, setAge] = React.useState('');
  const [turni,setTurni] = React.useState([])


  async function getTurni() {
    let turniAPI = new TurnoAPI;
    let turni = await turniAPI.getTurniByServizio(props.serviceName);
    setTurni(turni);
  }

  React.useEffect(() => {
    getTurni();
  }, [])


  const handleChange = (event) => {
    setAge(event.target.value);
    props.onSelectTurno(event.target.value)
  };


  return (
    <Box sx={{ minWidth: 120 }}>
      <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Turni</InputLabel>
        <Select
          value={age}
          label="Turni"
          onChange={handleChange}
        >
        
        {turni.map((turno) => (
            <MenuItem value={turno}>{turno} </MenuItem>

        ))}
        
        </Select>
      </FormControl>
    </Box>
  );
}