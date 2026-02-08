import React from 'react';
import { Alert, AlertTitle, Box, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { t } from 'i18next';

/**
 * Componente per visualizzare feedback strutturato sullo stato di generazione dello schedule.
 * Supporta stati di successo, parziale (warning) e errore.
 *
 * @param {object} props - Le props del componente.
 * @param {string|null} props.status - Lo stato della generazione ('success', 'partial', 'error', o null per nascondere).
 * @param {string} props.message - Il messaggio principale da visualizzare.
 * @param {string} [props.details=''] - Dettagli aggiuntivi per il messaggio (es. lista di vincoli violati).
 * @param {function} props.onClose - Callback da invocare quando l'utente chiude il feedback.
 */
function GenerationStatusFeedback({ status, message, details = '', onClose }) {
  const [open, setOpen] = React.useState(true);

  React.useEffect(() => {
    // Apri l'alert quando lo stato cambia e non è nullo
    setOpen(status !== null);
  }, [status]);

  const handleClose = () => {
    setOpen(false);
    if (onClose) {
      onClose(); // Invocare la callback per resettare lo stato nel componente padre
    }
  };

  if (!status) return null; // Non mostrare nulla se lo stato è nullo

  let severity = 'info';
  let title = '';

  switch (status) {
    case 'success':
      severity = 'success';
      title = t('Schedule Generated Successfully!');
      break;
    case 'partial':
      severity = 'warning';
      title = t('Schedule Generated with Warnings!');
      break;
    case 'error':
      severity = 'error';
      title = t('Schedule Generation Failed!');
      break;
    default:
      return null;
  }

  return (
    <Box sx={{ width: '100%', mb: 2 }}>
      <Collapse in={open}>
        <Alert
          severity={severity}
          action={
            <IconButton
              aria-label="close"
              color="inherit"
              size="small"
              onClick={handleClose}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          }
        >
          <AlertTitle>{title}</AlertTitle>
          {message}
          {details && (
            <Box component="div" sx={{ mt: 1, whiteSpace: 'pre-wrap' }}>
              <strong>{t("Details:")}</strong><br />{details}
            </Box>
          )}
        </Alert>
      </Collapse>
    </Box>
  );
}

export default GenerationStatusFeedback;