import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import {CategoriaAPI} from "../../API/CategoriaAPI";
import { t } from "i18next";
import {panic} from "./Panic";


export default function MultipleSelect(props) {
  const [categoria, setCategoria] = React.useState('');
  const [categorie,setCategorie] = React.useState([])

  const handleChangeCategoria = (event) => {
    setCategoria(event.target.value);
    getCategoria(event.target.value);
    props.onSelectCategoria(event.target.value)
  };


  async function getCategoria() {
    let categoriaAPI = new CategoriaAPI();
    let categorie
    try {
      categorie = await categoriaAPI.getStati()
    } catch (err) {

      panic()
      return
    }
    setCategorie(categorie);
  }

  React.useEffect(() => {
    getCategoria();
  }, [])


  return (
    <Stack spacing={3}>
      <Box sx={{minWidth: 120}}>
        <FormControl fullWidth>
          <InputLabel id="demo-simple-select-label">Stato</InputLabel>
          <Select
            value={categoria}
            label={t("Rotations")}
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

