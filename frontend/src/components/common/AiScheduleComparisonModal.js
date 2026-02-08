import React from 'react';
import { Box, Button, Card, CardContent, Grid, Modal, Typography } from '@mui/material';
import { t } from 'i18next';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '80%',
  maxWidth: 720,
  maxHeight: '80vh',
  bgcolor: 'background.paper',
  borderRadius: 2,
  boxShadow: 24,
  p: 2,
  overflowY: 'auto',
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
  { key: 'coverage', labelKey: 'Coverage' },
  { key: 'uffaBalance', labelKey: 'UFFA balance' },
  { key: 'sentimentTransitions', labelKey: 'Sentiment transitions' },
  { key: 'upDelta', labelKey: 'UP delta' },
  { key: 'varianceDelta', labelKey: 'Variance delta' },
];

const resolveCandidateLabel = (metadata) => {
  if (!metadata?.type) {
    return t('Schedule');
  }
  switch (metadata.type.toLowerCase()) {
    case 'standard':
      return t('Standard');
    case 'empathetic':
      return t('Empathetic');
    case 'efficient':
      return t('Efficient');
    case 'balanced':
      return t('Balanced');
    default:
      return metadata.type;
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
  const placeholder = placeholderText || t('N/A');
  const cards = Array.from({ length: 4 }, (_, index) => candidates[index] ?? null);

  return (
    <Modal
      open={isOpen}
      onClose={onClose}
      aria-labelledby="ai-schedule-comparison"
      aria-describedby="ai-schedule-comparison-metrics"
    >
      <Box sx={modalStyle}>
        {candidates.length === 0 ? (
          <Typography variant="h6" component="div" align="center" sx={{ mt: 2 }}>
            {t('No AI-generated schedules available for comparison.')}
          </Typography>
        ) : (
          <Grid container spacing={2}>
            {cards.map((candidate, cardIndex) => {
              const metadata = candidate?.metadata;
              const metrics = resolveMetrics(candidate);
              const scheduleId = metadata?.scheduleId ?? placeholder;
              const selectionKey = resolveSelectionKey(candidate);
              const isSelected = selectionKey && selectionKey === selectedCandidateKey;
              const isSelectable = Boolean(candidate && selectionKey && onSelectCandidate);
              const values = metricLabels.map((metric) => ({
                label: t(metric.labelKey),
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
                        {`${t('Schedule ID')}: ${scheduleId}`}
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
                        {isSelected ? t('Selected') : t('Select schedule')}
                      </Button>
                    </CardContent>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        )}
      </Box>
    </Modal>
  );
}

export default AiScheduleComparisonModal;
