package org.cswteams.ms3.audit.selection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionAuditRecordRepository extends JpaRepository<SelectionAuditRecord, Long> {
}
