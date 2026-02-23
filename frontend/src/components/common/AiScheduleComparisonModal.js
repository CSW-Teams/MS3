import React from 'react';
import { Alert, Box, Button, Card, CardContent, Grid, Modal, Typography } from '@mui/material';
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
  candidate?.metrics?.raw || candidate?.metrics?.normalized || {};

const resolveSentimentTransitionCounts = (candidate) =>
  candidate?.metrics?.raw?.sentimentTransitionCounts ||
  candidate?.metrics?.normalized?.sentimentTransitionCounts ||
  {};

const downloadJson = (data, filename) => {
  const blob = new Blob([JSON.stringify(data, null, 2)], {
    type: 'application/json;charset=utf-8',
  });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
};

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
  const downloadableCandidates = cards.filter(Boolean);
  const downloadDisabled = downloadableCandidates.length === 0;

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
          <React.Fragment>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
              <Button
                variant="outlined"
                disabled={downloadDisabled}
                onClick={() =>
                  downloadJson(downloadableCandidates, 'ai-schedules.json')
                }
              >
                {t('Download')}
              </Button>
            </Box>
            <Grid container spacing={2}>
              {cards.map((candidate, cardIndex) => {
                const metadata = candidate?.metadata;
                const metrics = resolveMetrics(candidate);
                const sentimentTransitionCounts = resolveSentimentTransitionCounts(candidate);
                const scheduleId = metadata?.scheduleId ?? placeholder;
                const selectionKey = resolveSelectionKey(candidate);
                const isSelected = selectionKey && selectionKey === selectedCandidateKey;
                const isSelectable = Boolean(candidate && selectionKey && onSelectCandidate);
                const warningMessage = metadata?.validationCode === 'PARTIAL_SUCCESS'
                  ? (metadata?.validationMessage || t('Generated with warnings.'))
                  : null;
                const values = metricLabels.map((metric) => ({
                  label: t(metric.labelKey),
                  value: metrics?.[metric.key],
                }));
                const sentimentTransitions = [
                    { label: 'N → 0', key: 'negativeToNeutral' },
                    { label: 'N → P', key: 'negativeToPositive' },
                    { label: '0 → P', key: 'neutralToPositive' },
                    { label: '0 → N', key: 'neutralToNegative' },
                    { label: 'P → N', key: 'positiveToNegative' },
                    { label: 'P → 0', key: 'positiveToNeutral' },
                ];

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
                        {warningMessage && (
                          <Alert severity="warning" sx={{ width: '100%' }}>
                            {warningMessage}
                          </Alert>
                        )}
                        {values.map((metric) => (
                          <Typography
                            key={`ai-comparison-metric-${cardIndex}-${metric.label}`}
                            variant="body1"
                            component="div"
                          >
                            {`${metric.label}: ${formatMetric(metric.value, placeholder)}`}
                          </Typography>
                        ))}
                                                <Box sx={{ mt: 2, width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                                    <Typography variant="subtitle1" component="div" gutterBottom>
                                                        {t("Sentiment Transition")}
                                                    </Typography>
                                                    {sentimentTransitions.map((transition) => (
                                                        <Box key={transition.key} sx={{ display: 'flex', gap: 1 }}>
                                                            <Typography variant="body2">{transition.label}:</Typography>
                                                            <Typography variant="body2">
                                                                {formatMetric(sentimentTransitionCounts?.[transition.key], placeholder)}
                                                            </Typography>
                                                        </Box>
                                                    ))}
                                                </Box>
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
          </React.Fragment>
        )}
      </Box>
    </Modal>
  );
}

export default AiScheduleComparisonModal;
