package org.cswteams.ms3.audit.selection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.audit.AuditProperties;
import org.cswteams.ms3.dao.SelectionAuditRecordRepository;
import org.cswteams.ms3.entity.SelectionAuditRecord;
import org.cswteams.ms3.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditRecorder {
    private static final Logger auditLogger = LoggerFactory.getLogger("MS3_AUDIT");
    private static final String CORRELATION_KEY = "requestId";
    private static final String DEFAULT_TENANT = "central_db";

    private final AuditProperties properties;
    private final ObjectMapper objectMapper;
    private final SelectionAuditRecordRepository repository;

    @Autowired
    public AuditRecorder(AuditProperties properties,
                         ObjectMapper objectMapper,
                         SelectionAuditRecordRepository repository) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    public void record(SelectionAuditEvent event) {
        if (event == null) {
            return;
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }
        if (event.getCorrelationId() == null) {
            event.setCorrelationId(MDC.get(CORRELATION_KEY));
        }
        if (event.getTenantId() == null) {
            String tenant = TenantContext.getCurrentTenant();
            event.setTenantId(tenant == null ? DEFAULT_TENANT : tenant);
        }

        auditLogger.info(
                "event=selection_audit selection_name={} outcome_id={} candidate_id={} selected={} score={} tenant_id={} correlation_id={} reasons={}",
                event.getSelectionName(),
                event.getOutcomeId(),
                event.getCandidateId(),
                event.isSelected(),
                event.getScore(),
                event.getTenantId(),
                event.getCorrelationId(),
                event.getReasons()
        );

        if (properties != null && properties.isPersistenceEnabled() && repository != null) {
            repository.save(toRecord(event));
        }
    }

    private SelectionAuditRecord toRecord(SelectionAuditEvent event) {
        String reasonsJson = null;
        if (event.getReasons() != null) {
            try {
                reasonsJson = objectMapper.writeValueAsString(event.getReasons());
            } catch (JsonProcessingException ex) {
                auditLogger.warn("event=selection_audit_serialization_failed selection_name={} reason=invalid_reasons_json",
                        event.getSelectionName(), ex);
            }
        }
        return new SelectionAuditRecord(
                event.getCorrelationId(),
                event.getTenantId(),
                event.getTimestamp(),
                event.getSelectionName(),
                event.getOutcomeId(),
                event.getCandidateId(),
                event.getScore(),
                event.isSelected(),
                reasonsJson
        );
    }
}
