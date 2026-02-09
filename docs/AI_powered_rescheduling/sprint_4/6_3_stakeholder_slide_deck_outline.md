# Sprint 4 — Task 6.3 Stakeholder Slide Deck Outline (AI-Powered Rescheduling)

## Slide 1 — Problem Statement & Why It Matters
- Current scheduling is generated via baseline REST endpoints and requires planners to re-fetch schedules after generation, with no AI assistance or comparison view today.【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L1-L153】
- Manual regeneration handles only a single output, making it hard to compare trade-offs (coverage vs. fairness vs. satisfaction).【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L154-L214】
- Objective for Sprint 4: enable AI‑powered rescheduling that offers multiple candidates for planner selection (planner-only scope).【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L9-L12】
- Stakeholder value: improved scheduling quality and retention outcomes while preserving operational safety and auditability.【F:docs/AI_powered_rescheduling/sprint_4/story_3.md†L1-L72】

## Slide 2 — Baseline Scheduling Flow Snapshot (Today)
- REST endpoints: `/schedule/generation` and `/schedule/regeneration/id={id}` trigger the pipeline, returning status codes only (no schedule body).【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L7-L63】
- Orchestrator (`SchedulerController`) collects data, builds ConcreteShift range, configures priorities, and runs `ScheduleBuilder.build()`.【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L64-L149】
- Data sources: shifts, constraints, doctors, holidays, scocciatura priorities, and schedule snapshots used for generation/regeneration.【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L71-L113】
- Error handling: invalid schedules map to `406 NOT_ACCEPTABLE`, with a pattern that can support AI partials later.【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L141-L214】
- **Diagram placeholder:** Baseline flow map from Story 1 (sequence of REST → orchestration → build).【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L30-L69】

## Slide 3 — AI Protocol (TOON/JSON) & Data Minimization
- Direct prompting only (no MCP/RLM): system sends TOON context, AI returns strict JSON response.【F:docs/AI_powered_rescheduling/sprint_4/story_2.md†L15-L78】
- TOON input captures schedule window, shifts, doctors, constraints, blocks, and feedbacks in a token‑efficient format.【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L40-L58】
- JSON output must validate schema and semantics; only `SUCCESS` accepted, failures trigger fallback handling.【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L59-L83】
- GDPR minimization: pseudonymized doctor IDs, limited feedback context, and no direct identifiers in payloads.【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L158-L199】
- **Diagram placeholder:** Protocol flow chart from Story 2 (TOON request → AI response → JSON validation).【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L144-L221】

## Slide 4 — Orchestration Pipeline (Standard + 3 AI Variants)
- Orchestration generates baseline schedule first, then calls AI once to return three variants: Empatica, Efficiente, Bilanciata.【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L61-L119】
- Pipeline steps: build TOON context, validate, call AI broker, parse JSON, compute metrics, return comparison payload + status.【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L33-L103】
- Status handling aligned to UI states: `success`, `partial`, `failure` (fallback to baseline on AI errors).【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L123-L151】
- Persistence rule: only the selected schedule is saved after planner confirmation.【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L84-L87】
- **Diagram placeholder:** Orchestration flow diagram (`ai_rescheduling_orchestration_flow.drawio`).【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L9-L33】

## Slide 5 — Metrics & Decision Framework (GQM‑Aligned)
- GQM goals: reduce burnout/turnover (business), improve scheduling satisfaction (software), generate 3 valid AI schedules (operational).【F:docs/AI_powered_rescheduling/sprint_4/story_3.md†L1-L188】
- Decision metrics include coverage, UFFA balance, sentiment transitions, UP delta, and variance delta, normalized to [0,1].【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L98-L118】
- Priority scale: configurable weights across dimensions with deterministic weighted‑sum scoring.【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L120-L133】
- Tie‑break order prioritizes coverage, then fairness and sentiment improvements to keep patient coverage safe.【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L124-L135】

## Slide 6 — UI Comparison Preview & Next Steps
- UI target: 2×2 comparison grid for baseline + three AI candidates, showing decision metrics only and handling missing data gracefully.【F:docs/AI_powered_rescheduling/sprint_4/story_4.md†L49-L89】【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L138-L165】
- Selection flow: planner selects a candidate, confirms in modal, and the system locks the choice post‑selection.【F:docs/AI_powered_rescheduling/sprint_4/story_4.md†L120-L173】
- Success and error feedback reuse existing UI patterns (loading modal + toast notifications).【F:docs/AI_powered_rescheduling/sprint_4/story_4.md†L20-L47】【F:docs/AI_powered_rescheduling/sprint_4/story_4.md†L174-L225】
- **Next steps (Sprint 4 close‑out):** finalize comparison layout + selection confirmation wiring, align with backend comparison payload schema.【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L386-L448】
- **Placeholder:** Insert UI comparison mock or screenshot once Story 4 UI work is finalized.【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L386-L452】
