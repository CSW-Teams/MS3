# Sprint 4 Retro Inputs — Story 6, Task 6.4

## What went well
- Baseline scheduling flow, constraints pipeline, and planner UI touchpoints were mapped early, giving a stable foundation for AI rescheduling work across Stories 2–5.
- Metrics and decision framework were specified and aligned to GQM+Strategy, enabling deterministic schedule comparison and selection.
- Documentation artifacts (technical doc draft, sprint summary, stakeholder outline) consolidated Story 1–5 outputs for end-of-sprint packaging.

## What didn’t go well
- Provider assumptions (e.g., Grok free-tier access) remained uncertain, which may affect integration timelines.
- UI regeneration flow currently does not trigger the AI agent call when using “rigenera,” creating a functional gap between UI intent and orchestration.

## Risks & mitigations for next sprint
- **AI reliability/availability risk** (timeouts, partial responses, provider limits) could degrade planner experience. **Mitigation**: keep baseline schedule as fallback, enforce strict JSON validation, and implement observable retry/backoff with structured errors.
- **UI readiness risk** (comparison grid + selection modal incomplete) could block adoption. **Mitigation**: prioritize completion of Story 4 UI items and align UI states with backend status codes early in sprint.

## Improvement actions
- Expand testing strategy with regression checks for AI orchestration, JSON schema validation, and UI state transitions.
- Evaluate the best prompting strategy for the next iteration, leveraging both system prompts and user prompts to improve AI consistency.
- Fix the “rigenera” flow so it triggers the AI agent call and aligns with the orchestrated rescheduling pipeline.
