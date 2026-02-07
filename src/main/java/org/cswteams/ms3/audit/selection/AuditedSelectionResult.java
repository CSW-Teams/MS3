package org.cswteams.ms3.audit.selection;

import java.util.ArrayList;
import java.util.List;

public class AuditedSelectionResult implements AuditableSelectionResult {
    private final String selectedCandidateId;
    private final List<SelectionAuditEvent> events;

    public AuditedSelectionResult(String selectedCandidateId, List<SelectionAuditEvent> events) {
        this.selectedCandidateId = selectedCandidateId;
        this.events = events == null ? List.of() : List.copyOf(events);
    }

    public String getSelectedCandidateId() {
        return selectedCandidateId;
    }

    @Override
    public List<SelectionAuditEvent> toAuditEvents(String selectionName) {
        List<SelectionAuditEvent> result = new ArrayList<>();
        for (SelectionAuditEvent event : events) {
            SelectionAuditEvent copy = new SelectionAuditEvent(
                    selectionName,
                    event.getOutcomeId(),
                    event.getCandidateId(),
                    event.getScore(),
                    event.isSelected(),
                    event.getReasons()
            );
            copy.setCorrelationId(event.getCorrelationId());
            copy.setTenantId(event.getTenantId());
            copy.setTimestamp(event.getTimestamp());
            result.add(copy);
        }
        return result;
    }
}
