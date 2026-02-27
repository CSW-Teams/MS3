# AI-Powered Scheduler Analysis (MS3 ↔ ABS)

## 1. Context and Goals
- **User story**: When planners generate a deterministic schedule, doctors can leave structured (picklists, ratings) and free-text feedback. Before publishing a new schedule, MS3 should invoke an external Agent Broker Service (ABS) that returns up to 3 alternative rearrangements of the deterministic output that better satisfy prior feedback while respecting constraints and uffaPoints. Planners review proposals, pick one, or fall back to MS3’s baseline.
- **Non-goals**: Implementing ABS itself; redesigning the deterministic scheduler; detailed UI mockups or persistence schemas; adding new scheduling algorithms; production hardening of unrelated modules.
- **Success criteria / KPIs**:
  - Hard constraints are never violated in accepted proposals; soft/tenant violations are explicit and auditable.
  - Planners receive ≤3 proposals with clear metrics (uffaPoints delta, broken constraints, unsatisfied feedbacks) and explanations.
  - Scheduler workload does not increase (proposal review fits existing regeneration/publish flow).
  - Feedback lifecycle is governed (expiry, conflicts, removals) and auditable.
  - Tenant isolation, GDPR minimization (pseudonymization), and schema-versioned contracts for MS3↔ABS.
  - Deterministic reproducibility: proposals can be justified and re-validated with MS3’s constraint engine.

## 2. Current MS3 Scheduling: As-Is (Repo-Based)
- **Actors/permissions**:
  - Planner (PLANNER): generate/regenerate/delete schedules, manage concrete shifts.【F:src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java†L29-L136】 
  - Doctor (DOCTOR): view schedules, view illegal schedules, request removal from shifts; cannot generate schedules.【F:src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java†L88-L118】【F:src/main/java/org/cswteams/ms3/rest/RichiestaRimozioneDaTurnoRestEndpoint.java†L21-L75】
  - Configurator: read schedules/illegal schedules.
- **Scheduling generation pipeline**:
  - Frontend planner view triggers POST `/api/schedule/generation` with start/end dates; toasts differentiate 202/206/406/400 outcomes.【F:frontend/src/views/pianificatore/ScheduleGeneratorView.js†L38-L155】
  - REST endpoint calls `SchedulerController.createSchedule`, which builds concrete shifts for each day/shift template, instantiates `ScheduleBuilder`, and persists schedule and priority snapshots.【F:src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java†L87-L163】
  - Regeneration restores priority snapshots, deletes the old schedule (if future), and rebuilds it.【F:src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java†L165-L199】
- **Current constraint model**:
  - Constraints are entities with `violable` (soft vs hard) and description; enforced via `verifyConstraint` on `ContextConstraint` during schedule build and manual shift edits.【F:src/main/java/org/cswteams/ms3/entity/constraint/Constraint.java†L15-L46】【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L433-L484】
  - Hard violations (non-violable or forced=false) mark schedule illegal; soft violations may be allowed when `forced` is true for manual edits.【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L433-L520】
- **Current uffaPoints logic**:
  - Uffa priorities (`DoctorUffaPriority`) drive queue ordering; `ControllerScocciatura` updates priorities based on “scocciature” annoyances, normalizes bounds, and orders doctors per queue before assignment.【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L12-L96】
  - Schedule generation updates snapshots and priorities per assignment and persists them with the schedule.【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L180-L240】【F:src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java†L149-L158】
- **Data lifecycle (draft/published/versioning)**:
  - Schedules are persisted with start/end, concrete shifts, violated constraints, and an empty `causeIllegal` setter (effectively a flag). No explicit draft/publish phases; regeneration deletes and recreates.
- **Existing feedback mechanisms**:
  - Doctor “request removal from shift” workflow exists but no structured/textual feedback on schedules; preferences per doctor capture desired time slots but are not tied to AI proposals.【F:src/main/java/org/cswteams/ms3/rest/RichiestaRimozioneDaTurnoRestEndpoint.java†L21-L75】【F:src/main/java/org/cswteams/ms3/control/preferences/PreferenceController.java†L19-L107】
- **Multi-tenancy/security**:
  - `TenantContext` thread-local is set from JWT but scheduling controllers/DAOs do not filter by tenant; effectively single-tenant behavior now.【F:src/main/java/org/cswteams/ms3/tenant/TenantContext.java†L1-L15】【F:src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java†L26-L98】

## 3. The New Feature: Target User Flow
1. **Doctor feedback capture**
   - Doctors submit structured feedback (ratings, selections) and free text tied to specific schedules or periods.
2. **Scheduler review**
   - Planner views aggregated feedback, filters unacceptable/conflicting items, can disable or edit before requesting proposals.
3. **ABS request**
   - MS3 sends deterministic baseline schedule snapshot + vetted feedback + constraint/uffa model references to ABS.
4. **ABS proposals**
   - ABS returns 1–3 rearranged schedules with metrics (uffaPoints delta vs baseline, broken constraints, unsatisfied feedbacks) and explanations.
5. **Scheduler decision**
   - Planner compares proposals vs baseline, can accept one, force-accept with justification, or fall back to baseline.
6. **Auditability**
   - All requests/responses, validations, and chosen/forced actions are logged with correlation IDs.

### GDPR-compliant pseudonymization guardrails (new critical point)
- ABS must never receive direct identifiers (e.g., name, surname, badge number, codice fiscale, email, phone, addresses) for doctors or other entities.
- ABS operates only on pseudonymous identifiers generated by MS3 (e.g., `absDoctorId`, `absLocationId`, `absShiftId`) per request/tenant; sensitive attributes are excluded or generalized.
- MS3 is the sole resolver that maps pseudonymous identifiers back to real entities; ABS responses must contain only pseudonymous references so MS3 can re-link internally.

## 4. Proposed Integration Architecture Options
| Option | Latency | Complexity | Reliability | UX impact | Auditability | Multi-tenant risk |
| --- | --- | --- | --- | --- | --- | --- |
| Synchronous REST (POST ABS, wait) | Low (request/response) | Medium (timeouts/retries) | Sensitive to ABS latency/outages | Fast feedback but blocks UI | Correlate via request IDs; inline explanations | Risk if tenant context leaks in payload; easier to bound scope |
| Async job + poll/callback | Medium (queue + status) | Higher (job store, callbacks) | Better retry/isolation; supports longer ABS runs | Planner waits on status; needs refresh/poll UI | Job records improve audit trail | Payloads stored server-side; ensure tenant-specific queues |
| Event-driven (message broker) | Variable | Highest (broker ops, consumers) | Decouples failures; resilient | Requires notification UI; eventual consistency | Strong traceability if events are signed/versioned | Broker must be tenant-scoped; risk of cross-topic leakage |

## 5. Contract / Protocol Design (JSON-Based)
- **Request payload (to ABS)**
  - `contractVersion`, `requestId` (uuid/idempotency), `tenantId`
  - `baselineSchedule`: dates, shifts, assignments, violatedConstraints (soft-only), doctor availability, priority snapshot references — all using pseudonymous identifiers (`absDoctorId`, `absShiftId`, `absLocationId`) and non-identifying attributes (skills/tags, not names/contact info)
  - `constraints`: list with ids, type (hard/soft/tenant), descriptions
  - `uffaModel`: version/hash of scoring function and per-doctor priority states
  - `feedback`: structured array (type, target shift/doctor/period, priority) + free-text items with optional NLP preprocessing hints; references only pseudonymous IDs
  - `overridePolicy`: what soft/tenant constraints ABS may relax and acceptable thresholds
  - `lifecycle`: generation timestamp, expiry window, concurrency token (schedule version)
  - `privacy`: pseudonymization scheme identifier, retention expectations, and redaction hints (no direct identifiers)
- **Response payload (from ABS)**
  - `contractVersion`, `requestId`, `correlationId`
  - `proposals`: max 3 items each with `proposalId`, `deltaUffaPoints`, `brokenConstraints` (ids + severities), `unsatisfiedFeedback` list, `changeset` (diff vs baseline), `explanations` (human-readable + machine-readable codes), `dataProvenance` (models/weights used) — all references use pseudonymous IDs so MS3 can re-link internally
  - `errors|warnings`: validation issues, unsupported feedback, partial results reason
- **Versioning strategy**
  - Semver per contract; ABS must tolerate older versions; MS3 keeps backward-compatible fields and adds `extensions` map for future flags.
- **Idempotency & correlation**
  - `requestId` acts as idempotency key; `correlationId` for tracing MS3↔ABS; include `scheduleVersion` to detect races.
- **Error taxonomy**
  - `INVALID_PAYLOAD`, `UNSUPPORTED_CONSTRAINT`, `TIMEOUT`, `PARTIAL_PROPOSALS`, `NO_FEASIBLE_PROPOSAL`, `AUTH_FAILURE`.

### Draft JSON schema sketch
```json
{
  "contractVersion": "1.0",
  "requestId": "uuid",
  "tenantId": "string",
  "scheduleVersion": "etag/number",
  "privacy": {"pseudonymization": "v1", "retentionPolicy": "no-ABS-persistence"},
  "baselineSchedule": {
    "startDate": "YYYY-MM-DD",
    "endDate": "YYYY-MM-DD",
    "concreteShifts": [
      {"id": "uuid", "date": "YYYY-MM-DD", "shiftId": "absShiftId", "assignments": [{"doctorId": "absDoctorId", "taskId": "id", "status": "ON_DUTY|ON_CALL"}]}
    ],
    "violatedConstraints": [{"constraintId": "id", "type": "SOFT|TENANT", "description": "..."}],
    "doctorPriorities": [{"doctorId": "absDoctorId", "priority": {"general": 0, "night": 0, "long": 0}}]
  },
  "constraints": [{"id": "id", "type": "HARD|SOFT|TENANT", "description": "...", "parameters": {}}],
  "uffaModel": {"version": "string", "upperBound": 0, "lowerBound": 0},
  "feedback": [{"id": "uuid", "kind": "structured|text", "target": {"date": "...", "doctorId": "absDoctorId"}, "value": "...", "priority": "LOW|MEDIUM|HIGH", "expiresAt": "..."}],
  "overridePolicy": {"allowSoft": true, "allowTenant": false, "maxBroken": 2},
  "lifecycle": {"generatedAt": "timestamp", "expiresAt": "timestamp"}
}
```
```json
{
  "contractVersion": "1.0",
  "requestId": "uuid",
  "correlationId": "uuid",
  "proposals": [
    {
      "proposalId": "uuid",
      "deltaUffaPoints": -3,
      "brokenConstraints": [{"constraintId": "id", "type": "SOFT|TENANT", "severity": "minor|major"}],
      "unsatisfiedFeedback": ["feedbackId"],
      "changeset": [{"concreteShiftId": "id", "action": "swap|move|replace", "details": {"fromDoctorId": "absDoctorId", "toDoctorId": "absDoctorId"}}],
      "explanations": {"summary": "...", "codes": ["SOFT_CONSTRAINT_RELAXED"], "trace": "url|blob"},
      "metrics": {"fairness": {}, "coverage": {}},
      "validation": {"schema": "1.0", "issues": []}
    }
  ],
  "warnings": ["string"],
  "errors": []
}
```

#### Pseudonymous ID strategy
- **Per-tenant scoping**: generate `absDoctorId`/other IDs that are unique within a tenant and unusable across tenants; incorporate tenant-specific salts to prevent linkability.
- **Stability window options**:
  - *Stable per tenant/time window*: aids ABS pattern learning but increases linkability risk; requires strict retention and DPA controls.
  - *Per-request ephemeral*: minimizes linkage/snooping risk; reduces continuity for ABS optimization; requires deterministic mapping within a single request/response cycle.
- **Collision resistance/non-guessability**: use cryptographically secure random IDs or HMAC-based derivations (non-sequential), refreshed per chosen stability window.

## 6. Feedback Interpretation and Acceptability Rules
- **Responsibilities**
  - MS3 vets feedback for legality, duplicates, conflicts, expiry; ABS focuses on optimization within approved feedback and constraint bounds.
- **Examples**
  - Unacceptable: requests that violate hard constraints (e.g., pregnant doctor on night shift), cross-tenant requests, discriminatory text.
  - Partially acceptable: “fewer night shifts” without numeric bound → MS3 normalizes to threshold or marks as advisory.
- **Conflict resolution**
  - Detect doctor vs doctor conflicts (both want same holiday) and prioritize via tenant policy/uffa weights; mark unsatisfied feedback explicitly in proposals.
- **Decay/expiry**
  - Attach `expiresAt` or decay weight to feedback; auto-expire after N cycles or when schedule window passes.

## 7. Validation, Safety, and Governance
- MS3 remains source of truth: all ABS proposals are re-validated through `ScheduleBuilder.verifyAllConstraints` (hard constraints inviolate) before display and again before acceptance.【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L433-L520】
- Pre-display gate: reject proposals violating hard constraints or exceeding allowed soft/tenant breaches; attach reject reasons.
- Audit: log request/response bodies (redacted), validation results, and planner actions with correlation IDs.
- GDPR minimization: share only necessary non-identifying attributes; exclude direct identifiers; apply pseudonymization with tenant-scoped, non-guessable IDs; avoid free-text unless required and consider template-based structured capture.
- Retention & processor governance: MS3 stores feedback text per tenant policy with minimization/expiry; ABS (as a data processor) must not persist inputs/outputs beyond processing, must document processing purposes in the DPA, and should expose deletion/retention attestations in observability.
- Multi-tenancy: include `tenantId` in contract; ABS must be forbidden from cross-tenant learning; isolate storage and model fine-tuning per tenant; pseudonymous IDs must not be linkable across tenants.

## 8. UX/Decision Support for Scheduler
- Present baseline + up to 3 proposals side-by-side with metrics (delta uffaPoints, broken constraint count/types, unsatisfied feedback list) and textual explanations.
- Provide diff view of changesets per concrete shift/doctor assignment.
- Allow “accept”, “accept with override” (captures justification), or “reject all → keep baseline”.
- Enable editing/voiding feedback items before sending to ABS; show why items were deemed unacceptable.

## 9. Observability and Operations
- Logging: structured logs for ABS requests/responses with `tenantId`, `requestId`, `correlationId`, validation outcomes.
- Metrics: ABS latency, success/partial/failure counts, proposal count distribution, validation rejection rates, constraint violation types, feedback coverage rate.
- Tracing: distributed tracing spans from UI action → MS3 → ABS → MS3 validation for auditability.
- Fallbacks: if ABS unavailable or times out, fall back to deterministic schedule; surface toast/notification but do not block publishing.
- Performance/latency budgets: synchronous path only if ABS SLA < few seconds; otherwise async job with polling.
- Cost: limit proposals to ≤3 and throttle per tenant; optional rate limits to protect ABS.

## 10. Open Decisions and Recommendations
- **Open decisions**
  - Which feedback types are supported in MVP (e.g., shift-level vs period-level)?
  - Thresholds for acceptable soft/tenant constraint violations per tenant.
  - Whether ABS can persist learned models per tenant or operate statelessly with MS3-provided data only.
  - Storage/retention for textual feedback and ABS traces; whether pseudonymous IDs are stable per window or per request.
  - UI placement for feedback moderation and proposal comparison.
- **Must-not-break invariants**
  - Hard constraints are never violated in accepted schedules; soft/tenant relaxations are explicit and bounded.
  - Max 3 proposals per ABS response; if none valid, baseline remains.
  - No direct identifiers ever leave the MS3 boundary; ABS receives only pseudonymous IDs and non-identifying attributes.
  - ABS outputs must remain re-linkable to MS3 entities solely via pseudonymous IDs resolved internally by MS3.
  - No cross-tenant data leakage or linkability in payloads, logs, pseudonymous IDs, or ABS learning.
  - UffaPoints/priority deltas use MS3’s authoritative logic for validation and display consistency.
  - Every accepted proposal is re-validated by MS3 before persistence/display.
- **Recommended MVP slice**
  - Synchronous ABS call with short timeout; single proposal returned; structured feedback only; UI for planner to approve/disable feedback items pre-request; re-validation gate using existing `ScheduleBuilder` and constraint metadata; logging with correlation IDs.
- **Phased rollout**
  1. **Phase 1**: Add feedback data model + moderation UI; define contract v1 with versioning/idempotency; synchronous ABS integration behind feature flag; validation gate.
  2. **Phase 2**: Introduce async job/polling for long-running proposals; richer metrics/explanations; text feedback NLP preprocessing.
  3. **Phase 3**: Event/broker-based scaling, tenant-specific model tuning, advanced conflict resolution and fairness analytics.
