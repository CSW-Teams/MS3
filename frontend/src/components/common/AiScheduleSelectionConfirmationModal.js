import React from 'react';
import { Box, Button, Modal, Typography } from '@mui/material';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '90%',
  maxWidth: 520,
  bgcolor: 'background.paper',
  borderRadius: 2,
  boxShadow: 24,
  p: 3,
  display: 'flex',
  flexDirection: 'column',
  gap: 2,
};

const actionsStyle = {
  display: 'flex',
  justifyContent: 'flex-end',
  gap: 1.5,
  flexWrap: 'wrap',
};

function AiScheduleSelectionConfirmationModal({
  isOpen,
  onConfirm,
  onCancel,
  candidateLabel,
  scheduleId,
  isSubmitting,
}) {
  const resolvedCandidateLabel = candidateLabel || 'Schedule / Schedulazione';
  const resolvedScheduleId = scheduleId ?? '—';

  return (
    <Modal
      open={isOpen}
      onClose={onCancel}
      aria-labelledby="ai-selection-confirmation-title"
      aria-describedby="ai-selection-confirmation-description"
    >
      <Box sx={modalStyle}>
        <Typography id="ai-selection-confirmation-title" variant="h6" component="h2">
          Confirm schedule selection / Conferma selezione schedulazione
        </Typography>
        <Typography id="ai-selection-confirmation-description" variant="body1">
          You are about to finalize this schedule. This choice will lock the selection for this
          comparison and cannot be changed. / Stai per finalizzare questa schedulazione. Questa
          scelta bloccherà la selezione per questo confronto e non potrà essere modificata.
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {`Candidate / Candidato: ${resolvedCandidateLabel}`}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {`Schedule ID / ID schedulazione: ${resolvedScheduleId}`}
        </Typography>
        <Box sx={actionsStyle}>
          <Button onClick={onCancel} variant="outlined" disabled={isSubmitting}>
            Cancel / Annulla
          </Button>
          <Button onClick={onConfirm} variant="contained" disabled={isSubmitting}>
            Confirm selection / Conferma selezione
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}

export default AiScheduleSelectionConfirmationModal;
