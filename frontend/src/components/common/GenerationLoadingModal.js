import React from 'react';
import { Modal, Box, CircularProgress, Typography } from '@mui/material';
import { t } from 'i18next';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  gap: 2,
};

function GenerationLoadingModal({ isOpen }) {
  return (
    <Modal
      open={isOpen}
      aria-labelledby="loading-modal-title"
      aria-describedby="loading-modal-description"
      disableEscapeKeyDown // Prevents closing with Escape key
    >
      <Box sx={style}>
        <CircularProgress />
        <Typography id="loading-modal-title" variant="h6" component="h2">
          {t("Generating Schedule...")}
        </Typography>
        <Typography id="loading-modal-description" sx={{ mt: 2 }}>
          {t("The AI is working to create your schedule. This might take a moment.")}
        </Typography>
      </Box>
    </Modal>
  );
}

export default GenerationLoadingModal;