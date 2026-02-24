# Dual-layer coverage parity monitoring

This document defines the log fields and example queries to monitor parity regressions release-over-release for AI scheduling dual-layer coverage checks.

## New structured metadata fields

Both orchestration and converter validation now emit structured metadata with:

- `dual_layer_coverage_valid` (boolean)
- `required_slots_per_shift_role_layer` (`<shiftId>|<assignmentStatus>|<role>` -> required count)
- `assigned_slots_per_shift_role_layer` (`<shiftId>|<assignmentStatus>|<role>` -> assigned count)
- `missing_slots_summary` (compact human-readable mismatch rows)
- `mismatch_examples` (first N mismatch samples for triage)

### Log events to monitor

- `ai_candidate_dual_layer_coverage_validation`
  - Per variant/candidate check in orchestration.
- `ai_converter_dual_layer_coverage_validation`
  - Converter-side validation snapshot before throwing schema mismatch.

## Suggested dashboards

Create a dashboard with the following tiles for each release version (`service.version`) and candidate (`variant_id`):

1. **Dual-layer validity rate**
   - `% valid = count(dual_layer_coverage_valid=true) / total validations`.
2. **Missing slot volume trend**
   - Sum of `missing_slots_count` by day and by variant.
3. **Top regressing layers**
   - Parse keys in `required_slots_per_shift_role_layer` and `assigned_slots_per_shift_role_layer` and rank by `(required-assigned)`.
4. **Mismatch triage feed**
   - Table of most recent `mismatch_examples` grouped by `candidate_id` and `service.version`.

## Example queries

> Adapt syntax for your observability stack (Datadog/ELK/Grafana Loki/Splunk).

### 1) Validity rate by release and variant

```sql
SELECT
  service_version,
  variant_id,
  100.0 * SUM(CASE WHEN dual_layer_coverage_valid THEN 1 ELSE 0 END) / COUNT(*) AS validity_rate
FROM logs
WHERE event = 'ai_candidate_dual_layer_coverage_validation'
GROUP BY service_version, variant_id
ORDER BY service_version DESC, variant_id;
```

### 2) Missing-slot drift release-over-release

```sql
SELECT
  service_version,
  variant_id,
  SUM(missing_slots_count) AS missing_slots
FROM logs
WHERE event IN (
  'ai_candidate_dual_layer_coverage_validation',
  'ai_converter_dual_layer_coverage_validation'
)
GROUP BY service_version, variant_id
ORDER BY service_version DESC, missing_slots DESC;
```

### 3) First mismatch examples for support triage

```sql
SELECT
  timestamp,
  service_version,
  variant_id,
  candidate_id,
  mismatch_examples,
  missing_slots_summary
FROM logs
WHERE event = 'ai_candidate_dual_layer_coverage_validation'
  AND dual_layer_coverage_valid = false
ORDER BY timestamp DESC
LIMIT 100;
```

## Release-gate recommendation

Before promoting a release:

1. Compare `validity_rate` against the previous two releases.
2. Ensure no new dominant `(shift,status,role)` mismatch key appears.
3. Spot-check `mismatch_examples` for repeated root causes in the same role/layer.
4. Block rollout if validity rate regresses above agreed SLO threshold.
