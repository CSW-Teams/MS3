import React from 'react';
import { Box, Button, Modal, Typography } from '@mui/material';
import { t } from 'i18next';

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
  const resolvedCandidateLabel = candidateLabel || "Schedule";
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
          {t("Confirm schedule selection")}
        </Typography>
        <Typography id="ai-selection-confirmation-description" variant="body1">
          {t("You are about to finalize this schedule. This choice will lock the selection for this comparison and cannot be changed.")}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {t("Candidate")}: {t(resolvedCandidateLabel)}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {t("Schedule ID")}: {resolvedScheduleId}
        </Typography>
        <Box sx={actionsStyle}>
          <Button onClick={onCancel} variant="outlined" disabled={isSubmitting}>
            {t("Cancel")}
          </Button>
          <Button onClick={onConfirm} variant="contained" disabled={isSubmitting}>
            {t("Confirm selection")}
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}

export default AiScheduleSelectionConfirmationModal;
