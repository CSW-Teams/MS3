import React from 'react';
import { Box, Card, CardContent, Grid, Modal, Typography } from '@mui/material';
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

const normalizeCardMetrics = (metrics) => {
  if (Array.isArray(metrics)) {
    return metrics;
  }
  if (metrics && typeof metrics === 'object') {
    return Object.values(metrics);
  }
  if (metrics === null || metrics === undefined) {
    return [];
  }
  return [metrics];
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

function AiScheduleComparisonModal({ isOpen, onClose, comparisonMetrics = [], placeholderText }) {
  const placeholder = placeholderText || t('—');
  const cards = Array.from({ length: 4 }, (_, index) =>
    normalizeCardMetrics(comparisonMetrics[index])
  );

  return (
    <Modal
      open={isOpen}
      onClose={onClose}
      aria-labelledby="ai-schedule-comparison"
      aria-describedby="ai-schedule-comparison-metrics"
    >
      <Box sx={modalStyle}>
        <Grid container spacing={2}>
          {cards.map((metrics, cardIndex) => {
            const values = metrics.length > 0 ? metrics : [placeholder];
            return (
              <Grid item xs={12} sm={6} key={`ai-comparison-card-${cardIndex}`}>
                <Card sx={cardStyle} elevation={3}>
                  <CardContent sx={metricsContainerStyle}>
                    {values.map((metric, metricIndex) => (
                      <Typography
                        key={`ai-comparison-metric-${cardIndex}-${metricIndex}`}
                        variant="h6"
                        component="div"
                      >
                        {formatMetric(metric, placeholder)}
                      </Typography>
                    ))}
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
