import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import { ServizioAPI } from '../../API/ServizioAPI';


export default function MultipleSelect(props) {
  const [categoria, setCategoria] = React.useState('');
  const [categorie,setCategorie] = React.useState([])
  const handleChangeCategoria = (event) => {
    setCategoria(event.target.value);
    props.onSelectServizio(event.target.value)
  };


  async function getCategoria() {
    let serviceAPI = new ServizioAPI();
    let categorie = await serviceAPI.getService()
    setCategorie(categorie);
  }

  React.useEffect(() => {
    getCategoria();
  }, [])


  return (
    <Stack spacing={3}>
      <Box sx={{minWidth: 120}}>
        <FormControl fullWidth>
          <InputLabel id="demo-simple-select-label">Turnazioni</InputLabel>
          <Select
            value={categoria}
            label='Turnazioni'
            onChange={handleChangeCategoria}
          >
            {categorie.map((elementData) => (
              <MenuItem value={elementData}>{elementData} </MenuItem>

            ))}
          </Select>
        </FormControl>
      </Box>

    </Stack>
  );
}

