package org.cswteams.ms3.audit.selection;

import org.cswteams.ms3.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditRecorder {
    private static final Logger auditLogger = LoggerFactory.getLogger("MS3_AUDIT");
    private static final String CORRELATION_KEY = "requestId";
    private static final String DEFAULT_TENANT = "central_db";

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
    }
}
