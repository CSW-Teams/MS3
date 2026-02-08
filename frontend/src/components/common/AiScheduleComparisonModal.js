import React from 'react';
import { Box, Button, Card, CardContent, Grid, Modal, Typography } from '@mui/material';
import { t } from 'i18next';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '90%',
  maxWidth: 900,
  bgcolor: 'background.paper',
  borderRadius: 2,
  boxShadow: 24,
  p: 3,
};

const cardStyle = {
  height: '100%',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  textAlign: 'center',
  bgcolor: 'background.default',
};

const metricsContainerStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: 1.5,
  alignItems: 'center',
};

const formatMetric = (value, placeholder) => {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return placeholder;
  }
  if (typeof value === 'number') {
    return value.toFixed(2);
  }
  return value;
};

const metricLabels = [
  { key: 'coverage', label: 'Coverage / Copertura' },
  { key: 'uffaBalance', label: 'UFFA balance / Bilanciamento UFFA' },
  { key: 'sentimentTransitions', label: 'Sentiment transitions / Transizioni sentimento' },
  { key: 'upDelta', label: 'UP delta / Delta UFFA' },
  { key: 'varianceDelta', label: 'Variance delta / Delta varianza' },
];

const resolveCandidateLabel = (metadata) => {
  if (!metadata?.type) {
    return 'Schedule / Schedulazione';
  }
  switch (metadata.type.toLowerCase()) {
    case 'standard':
      return 'Standard / Standard';
    case 'empathetic':
      return 'Empathetic / Empatica';
    case 'efficient':
      return 'Efficient / Efficiente';
    case 'balanced':
      return 'Balanced / Bilanciata';
    default:
      return `${metadata.type} / ${metadata.type}`;
  }
};

const resolveSelectionKey = (candidate) =>
  candidate?.metadata?.candidateId ?? candidate?.metadata?.type ?? '';

const resolveMetrics = (candidate) =>
  candidate?.metrics?.normalized || candidate?.metrics?.raw || {};

function AiScheduleComparisonModal({
  isOpen,
  onClose,
  candidates = [],
  placeholderText,
  onSelectCandidate,
  selectedCandidateKey,
  selectionLocked,
}) {
  const placeholder = placeholderText || t('—');
  const cards = Array.from({ length: 4 }, (_, index) => candidates[index] ?? null);

  return (
    <Modal
      open={isOpen}
      onClose={onClose}
      aria-labelledby="ai-schedule-comparison"
      aria-describedby="ai-schedule-comparison-metrics"
    >
      <Box sx={modalStyle}>
        <Grid container spacing={2}>
          {cards.map((candidate, cardIndex) => {
            const metadata = candidate?.metadata;
            const metrics = resolveMetrics(candidate);
            const scheduleId = metadata?.scheduleId ?? placeholder;
            const selectionKey = resolveSelectionKey(candidate);
            const isSelected = selectionKey && selectionKey === selectedCandidateKey;
            const isSelectable = Boolean(candidate && selectionKey && onSelectCandidate);
            const values = metricLabels.map((metric) => ({
              label: metric.label,
              value: metrics?.[metric.key],
            }));

            return (
              <Grid item xs={12} sm={6} key={`ai-comparison-card-${cardIndex}`}>
                <Card sx={cardStyle} elevation={3}>
                  <CardContent sx={metricsContainerStyle}>
                    <Typography variant="h6" component="div">
                      {resolveCandidateLabel(metadata)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {`Schedule ID / ID schedulazione: ${scheduleId}`}
                    </Typography>
                    {values.map((metric) => (
                      <Typography
                        key={`ai-comparison-metric-${cardIndex}-${metric.label}`}
                        variant="body1"
                        component="div"
                      >
                        {`${metric.label}: ${formatMetric(metric.value, placeholder)}`}
                      </Typography>
                    ))}
                    <Button
                      variant={isSelected ? 'contained' : 'outlined'}
                      disabled={!isSelectable || (selectionLocked && !isSelected)}
                      onClick={() => onSelectCandidate(candidate)}
                    >
                      {isSelected
                        ? 'Selected / Selezionato'
                        : 'Select schedule / Seleziona schedulazione'}
                    </Button>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      </Box>
    </Modal>
  );
}

export default AiScheduleComparisonModal;
