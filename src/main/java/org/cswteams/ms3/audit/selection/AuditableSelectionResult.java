package org.cswteams.ms3.audit.selection;

import java.util.List;

public interface AuditableSelectionResult {
    List<SelectionAuditEvent> toAuditEvents(String selectionName);
}
