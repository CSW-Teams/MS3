# Commenting Guideline for Code Added After `5c533ad4`

## Scope and Enforcement
- This guideline applies to comments in code created or changed after commit `5c533ad4`.
- Pull requests that add comments outside the allowed types must be rejected.
- Every new comment must explain intent and business relevance so it is clear to presentation audiences.

## Language Rules (Mandatory)
- English only.
- Short sentences.
- No jargon unless unavoidable.
- If jargon is unavoidable, add a short plain-language clarification in the same comment.

## Allowed Comment Types
Use only the following types and templates.

### 1) File Header Summary (what this file does)
**Template**
```text
// Summary: <main responsibility>. Business relevance: <why it matters to users or operations>.
```

### 2) Method/Function Intent (why it exists)
**Template**
```text
// Intent: <why this method/function exists>. Business relevance: <outcome enabled>.
```

### 3) Non-Obvious Decision Notes (why this logic is needed)
**Template**
```text
// Decision: <non-obvious choice>. Reason: <constraint/trade-off>. Business relevance: <risk avoided/value preserved>.
```

### 4) Edge-Case Comments (input/output constraints)
**Template**
```text
// Edge case: <specific condition>. Input/Output rule: <constraint>. Business relevance: <impact if ignored>.
```

## Anti-Patterns (Forbidden)
- Restating obvious code behavior.
  - Example to reject: `// increment i` next to `i++`.
- Outdated timestamps or author-style comments.
  - Examples to reject: `// Updated on 2023-11-04`, `// John fixed this`.
- Long narrative blocks.
  - Rule: keep comments concise and directly tied to intent, decision, or edge cases.

## Examples by Technology

### Java
```java
// Summary: Validates shift assignment requests before persistence. Business relevance: prevents invalid schedules shown to planners.
public class ShiftAssignmentValidator {

    // Intent: enforce tenant-level authorization before loading doctor data. Business relevance: protects cross-tenant privacy.
    public Doctor loadAuthorizedDoctor(Long doctorId, String tenantId) {
        // Decision: query by tenant and doctor together. Reason: avoids a second filter step that could be skipped. Business relevance: reduces data-leak risk in demos and production.
        return doctorRepository.findByIdAndTenantId(doctorId, tenantId)
            .orElseThrow(UnauthorizedAccessException::new);
    }

    // Edge case: request includes an overnight slot ending at 00:00. Input/Output rule: normalize end time to next day. Business relevance: prevents wrong coverage metrics in executive reports.
    private ShiftWindow normalizeOvernight(ShiftWindow input) { /* ... */ }
}
```

### React / JavaScript
```javascript
// Summary: Renders planning conflict badges. Business relevance: helps coordinators explain schedule risks during reviews.
export function ConflictBadge({ conflict }) {
  // Intent: map backend severity to UI color tokens. Business relevance: keeps risk priority visible to non-technical stakeholders.
  const tone = mapSeverityToTone(conflict.severity);

  // Edge case: missing severity from legacy payload. Input/Output rule: default to "info" tone. Business relevance: avoids blank badges in live presentations.
  const safeTone = tone || "info";

  return <Badge tone={safeTone}>{conflict.label}</Badge>;
}
```

### SQL
```sql
-- Summary: Builds monthly workload totals for dashboard cards. Business relevance: supports staffing decisions.
WITH monthly_workload AS (
  -- Decision: aggregate from validated shifts only. Reason: draft shifts can be removed later. Business relevance: keeps KPI numbers stable for management updates.
  SELECT doctor_id, SUM(hours) AS total_hours
  FROM concrete_shift
  WHERE status = 'VALIDATED'
  GROUP BY doctor_id
)
-- Edge case: doctors with no validated shifts. Input/Output rule: return 0 instead of NULL. Business relevance: avoids misleading empty values in presentation slides.
SELECT d.id, COALESCE(m.total_hours, 0) AS total_hours
FROM doctor d
LEFT JOIN monthly_workload m ON m.doctor_id = d.id;
```

### Tests
```java
// Summary: Verifies tenant isolation in shift search. Business relevance: proves privacy controls before release demos.
class ShiftSearchControllerTest {

    // Intent: ensure a user cannot read another tenant's shifts. Business relevance: prevents compliance issues in customer-facing walkthroughs.
    @Test
    void shouldRejectCrossTenantAccess() {
        // Edge case: valid shift ID but foreign tenant token. Input/Output rule: endpoint returns 403. Business relevance: confirms expected security posture.
        // test body
    }
}
```

## Review Checklist
- Comment matches one allowed type.
- Comment follows the corresponding template.
- English only, short sentences, limited jargon.
- Comment explains intent and business relevance for presentation audiences.
- No forbidden anti-patterns.

## Final Quality Gate
- Use `docs/commenting_quality_gate_post_5c533ad4.md` as the mandatory closure checklist and reporting template for post-`5c533ad4` comment hardening reviews.
