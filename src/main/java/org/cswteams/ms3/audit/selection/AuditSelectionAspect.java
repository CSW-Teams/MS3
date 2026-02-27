package org.cswteams.ms3.audit.selection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class AuditSelectionAspect {
    private final AuditRecorder auditRecorder;

    public AuditSelectionAspect(AuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    @AfterReturning(pointcut = "@annotation(auditSelection)", returning = "result")
    public void afterSelection(JoinPoint joinPoint, AuditSelection auditSelection, Object result) {
        if (!(result instanceof AuditableSelectionResult)) {
            return;
        }
        String selectionName = auditSelection.value();
        if (selectionName == null || selectionName.trim().isEmpty()) {
            selectionName = joinPoint.getSignature().toShortString();
        }
        List<SelectionAuditEvent> events = ((AuditableSelectionResult) result).toAuditEvents(selectionName);
        for (SelectionAuditEvent event : events) {
            auditRecorder.record(event);
        }
    }
}
