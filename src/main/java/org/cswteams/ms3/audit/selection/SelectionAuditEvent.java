package org.cswteams.ms3.audit.selection;

import java.time.Instant;
import java.util.Map;

public class SelectionAuditEvent {
    private String correlationId;
    private String tenantId;
    private Instant timestamp;
    private String selectionName;
    private String outcomeId;
    private String candidateId;
    private Double score;
    private boolean selected;
    private Map<String, Object> reasons;

    public SelectionAuditEvent(String selectionName,
                               String outcomeId,
                               String candidateId,
                               Double score,
                               boolean selected,
                               Map<String, Object> reasons) {
        this.selectionName = selectionName;
        this.outcomeId = outcomeId;
        this.candidateId = candidateId;
        this.score = score;
        this.selected = selected;
        this.reasons = reasons;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Map<String, Object> getReasons() {
        return reasons;
    }

    public void setReasons(Map<String, Object> reasons) {
        this.reasons = reasons;
    }
}
