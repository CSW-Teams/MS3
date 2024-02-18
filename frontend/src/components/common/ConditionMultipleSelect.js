import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import { t } from "i18next";


export default function ConditionMultipleSelect(props) {
  const [condition, setCondition] = React.useState([]);
  const [conditions,setConditions] = React.useState([])

  const handleChangeCondition = (event) => {
    setCondition(event.target.value);
    getCondition(event.target.value);
    props.onSelectCondition(event.target.value)
  };


  async function getCondition() {
    var c = [];
    var found = false;

    for(var i =0;i<props.currentConditionsList.length;i++){
      found = false;
      for(var j = 0;j<props.conditionsList.length;j++){
        if(props.currentConditionsList[i].label === props.conditionsList[j].label){
          found = true;
        }
      }
      if(found === false){
        c.push([props.currentConditionsList[i].label,props.currentConditionsList[i].permanent]);
      }
    }

    setConditions(c);
  }

  React.useEffect(() => {
    getCondition();
  }, []);


  return (
    <Stack spacing={3}>
      <Box sx={{minWidth: 120}}>
        <FormControl fullWidth>
          <InputLabel id="demo-simple-select-label">{t("Conditions")}</InputLabel>
          <Select
            value={condition}
            label={t("Conditions")}
            onChange={handleChangeCondition}
          >
            {conditions.map((data) => (

                <MenuItem value={data[0]}>{data[0]} </MenuItem>

            ))}
          </Select>
        </FormControl>
      </Box>

    </Stack>
  );
}

