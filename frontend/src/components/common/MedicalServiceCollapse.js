import React, {useState} from "react";
import type {MedicalService} from "../../entity/MedicalService";
import {Box, Collapse, Divider, IconButton, Typography} from "@mui/material";
import {ExpandMore} from "@mui/icons-material";

const MedicalServiceCollapse: React.FC<{
  medicalService: MedicalService,
  children: React.ReactNode,
}> = ({medicalService, children}) => {
  const [open, setOpen] = useState(false);

  const toggleExpand = () => setOpen((prevOpen) => !prevOpen);

  return (
    <Box sx={{
      border: '1px solid #ccc',
      borderRadius: 4,
      padding: 3,
      width: '95%',
      margin: '0 auto',
      position: 'relative',
      marginBottom: 2,
    }}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <Typography variant="h6" gutterBottom sx={{marginBottom: 0}}>
          {medicalService.name}
        </Typography>

        <IconButton
          onClick={toggleExpand}
          sx={{
            transform: open ? 'rotate(180deg)' : 'rotate(0deg)',
            transition: 'transform 0.3s ease',
            backgroundColor: 'primary.main',
            color: 'white',
            '&:hover': {
              backgroundColor: 'primary.dark'
            },
          }}
        >
          <ExpandMore/>
        </IconButton>
      </Box>

      <Collapse in={open}>
        <Divider sx={{ marginY: 2 }} />

        {children}
      </Collapse>

    </Box>
  )
}

export default MedicalServiceCollapse;
