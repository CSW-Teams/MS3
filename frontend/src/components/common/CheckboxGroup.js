import React from 'react';
import { FormControlLabel, Checkbox, Box, Typography } from '@mui/material';

import {t} from "i18next";

const CheckboxGroup = ({ options = [], selectedOptions = [], onChange, disabled = false }) => {
  if (!Array.isArray(options) || options.length === 0) {
    return <Typography>{'No options available'}</Typography>;
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
      {options.map((option, _) => (
        <FormControlLabel
          key={option}
          control={
            <Checkbox
              checked={selectedOptions.includes(option)}
              onChange={() => onChange(option)}
              disabled={disabled}
            />
          }
          label={t(option)}
        />
      ))}
    </Box>
  );
};

export default CheckboxGroup;
