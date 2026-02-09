# Sprint 4 Task 6.2 — Sprint Summary + Decisions Log

## 6.2.a Sprint Timeline Summary (Stories 1–5)

### Week 1 (early sprint)
- **Story 1 (baseline flow + UI mapping)**: Completed the backend scheduling flow analysis, constraints/priority pipeline analysis, and planner UI surface inventory to establish the current scheduling baseline for AI rescheduling. These outputs anchor the orchestration and UI changes in later stories, including REST endpoints, DAO inputs, and planner UI behavior. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L12-L140】【F:docs/AI_powered_rescheduling/sprint_4/story_1.md†L1-L223】
- **Story 2 (AI protocol design)**: Completed AI API inventory and assumptions for free-tier providers and established the direct prompting approach with TOON input + JSON output, plus provider-agnostic protocol expectations. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L144-L251】【F:docs/AI_powered_rescheduling/sprint_4/story_2.md†L1-L270】
- **Story 3 (metrics + decision framework)**: Completed the metrics catalog and normalization foundation aligned to GQM+Strategy, enabling downstream comparison and decision logic. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L255-L382】【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L86-L135】

### Week 2 (before mid‑sprint alignment)
- **Story 4 (planner UI comparison/selection)**: Implemented core planner UI states, success message reuse, and error handling. The comparison layout and selection modal are still pending. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L386-L452】【F:docs/AI_powered_rescheduling/sprint_4/checkbox_roadmap_sprint_4.md†L39-L45】
- **Story 5 (backend orchestration plan)**: Implemented the orchestration sequence and AI IO pipeline (TOON generation + JSON ingestion), comparison/selection endpoints, error handling, and audit logging. This establishes the end‑to‑end orchestration for standard + AI schedule variants and comparison payloads. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L458-L535】【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L61-L352】

### End of sprint (packaging)
- **Story 6 (documentation)**: Technical documentation draft is already available; this sprint summary + decisions log completes Task 6.2. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L295-L346】【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L1-L205】

## 6.2.b Decisions Log

### AI model and provider decisions
- **Chosen AI models**: Gemini **Gemma** and Grok **Llama‑70B** are the selected targets for the sprint roadmap. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L9-L12】
- **Provider access assumptions**: AI integrations are designed around free‑tier constraints (rate limits, quotas), requiring retries and robust error handling. This is a guiding assumption in the protocol design. 【F:docs/AI_powered_rescheduling/sprint_4/story_2.md†L45-L236】

### Protocol and orchestration choices
- **Direct prompting with context**: The protocol uses direct prompting (no MCP/RLM, but system + user prompts) with **TOON as input** and **strict JSON as output**, making the integration provider‑agnostic and parseable by the backend. 【F:docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md†L144-L251】【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L32-L83】
- **Single AI call, three variants**: Orchestration produces a baseline schedule and a single AI call returning three labeled variants (Empatica, Efficiente, Bilanciata) to compare within a single request/response cycle. 【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L61-L87】
- **Selection persistence rule**: Only the explicitly selected schedule is persisted; baseline and AI variants are ephemeral until selection. 【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L84-L87】【F:docs/AI_powered_rescheduling/sprint_4/story_5.md†L290-L307】

### Metrics and decision priorities
- **Decision framework**: Metrics are normalized to [0,1] and aggregated via a weighted priority scale; the decision algorithm uses a deterministic weighted sum with a tie‑break ordering that prioritizes coverage first. 【F:docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md†L98-L135】
