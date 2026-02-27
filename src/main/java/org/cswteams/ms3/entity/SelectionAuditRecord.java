package org.cswteams.ms3.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "selection_audit_record")
public class SelectionAuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "timestamp_utc")
    private Instant timestamp;

    @Column(name = "selection_name")
    private String selectionName;

    @Column(name = "outcome_id")
    private String outcomeId;

    @Column(name = "candidate_id")
    private String candidateId;

    @Column(name = "score")
    private Double score;

    @Column(name = "selected")
    private boolean selected;

    @Lob
    @Column(name = "reasons_json")
    private String reasonsJson;

    protected SelectionAuditRecord() {
    }

    public SelectionAuditRecord(String correlationId,
                                String tenantId,
                                Instant timestamp,
                                String selectionName,
                                String outcomeId,
                                String candidateId,
                                Double score,
                                boolean selected,
                                String reasonsJson) {
        this.correlationId = correlationId;
        this.tenantId = tenantId;
        this.timestamp = timestamp;
        this.selectionName = selectionName;
        this.outcomeId = outcomeId;
        this.candidateId = candidateId;
        this.score = score;
        this.selected = selected;
        this.reasonsJson = reasonsJson;
    }

    public Long getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Double getScore() {
        return score;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getReasonsJson() {
        return reasonsJson;
    }
}
