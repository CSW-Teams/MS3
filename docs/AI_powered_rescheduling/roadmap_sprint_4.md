# Sprint 4 Roadmap — AI-Powered Rescheduling (2-Week Sprint)

## Sprint Overview & Assumptions
- **Team capacity**: 5 people × ~12 hours = **60 total hours** for the sprint.
- **Sprint cadence**: 2 weeks, with a **mid-sprint alignment meeting** on Monday of week 2 and **sprint review + retro** the following Monday.
- **Scope**: Analysis + planning for AI-powered rescheduling with backend + frontend + AI-agent communication + metrics + documentation + stakeholder materials.
- **Code change policy for this sprint roadmap**: Planning only; no implementation included here.
- **Key existing flow anchors**: Schedule generation/regeneration uses `/api/schedule/generation` and `/api/schedule/regeneration` in the backend and frontend schedule generator view. The current system generates schedules from the planner UI with a loading overlay and a list of schedules, but has **no AI integration or schedule comparison UI** yet.
- **AI models selected**: Gemini **Gemma** and Grok **Llama-70B**.
- **Planner-only scope**: AI rescheduling and final schedule selection are **planner-only** during the rescheduling phase, then the chosen schedule replaces the algorithm-generated schedule in full.

### PROMPT FOR THE USER
1. If direct model **endpoints/SDKs** are not available now, confirm that they will be provided during implementation tasks in a later sprint.

---

## Story 1 — Current Scheduling Flow Analysis & Baseline Mapping
**Goal**: Document the current scheduling generation/regeneration flow, the scheduling data model, and constraint pipeline to ground AI-powered rescheduling requirements.

**Functional Scope**
- Backend schedule generation/regeneration endpoints and controller orchestration.
- Schedule-building constraints and priority queues (UFFA/scocciatura).
- Frontend planner UI for schedule generation and schedule list.

**Estimated Effort**: **6 hours** (3 microtasks × 2h)

**Dependencies**: None (starting point).

**Expected Deliverables**
- Baseline flow map (sequence of REST calls and data dependencies).
- List of current UI surfaces involved in scheduling.

**Sprint Timing**: **Early sprint** (needed for all downstream stories).

**Microtasks (2h each)**
1. **Map backend schedule generation/regeneration flow**
   - Description: Identify entrypoints, controller orchestration, and data sources used to build schedules.
   - Preconditions: None.
   - Parallel affinities: None.
   - Output artifact: Backend flow diagram / notes in roadmap appendix.
2. **Catalog constraint and priority pipeline**
   - Description: Summarize how constraints and UFFA/scocciatura priorities influence schedule generation.
   - Preconditions: Microtask 1.
   - Parallel affinities: None.
   - Output artifact: Constraint/priority summary and risks list.
3. **Map current planner UI scheduling surfaces**
   - Description: Inventory schedule generation UI, loading states, schedule list presentation, and regeneration actions.
   - Preconditions: None.
   - Parallel affinities: None.
   - Output artifact: UI touchpoints list for planner flow.

---

## Story 2 — AI Agent Communication & TOON/JSON Protocol Design (Direct Prompting)
**Goal**: Define the AI agent communication protocol, context delivery strategy, and TOON artifacts used for scheduling and feedback.

**Functional Scope**
- Identify AI agent APIs and integration constraints.
- Define **system knowledge** payloads to pass as **context/knowledge base** (not prompt).
- Define dual-channel communication: **`.toon` from system to agent** and **`.json` from agent to system**.
- Define TOON-based scheduling file and JSON response contract boundaries.
- Plan for **direct prompting with context** (no MCP/RLM in this version).

**Estimated Effort**: **10 hours** (5 microtasks × 2h)

**Dependencies**
- Story 1 (baseline flow understanding).

**Expected Deliverables**
- AI communication specification (context structure, TOON request + JSON response schemas, error handling).
- Security + privacy considerations for data transfer (GDPR data minimization).

**Sprint Timing**: **Early sprint → before mid-sprint** (enables backend + UI work).

**Microtasks (2h each)**
1. **AI API inventory and constraints capture**
   - Description: Identify Gemini Gemma and Grok Llama-70B endpoints (if available), payload limits, auth, and supported formats; document TBD items if endpoints are not yet available.
   - Preconditions: Story 1 complete.
   - Parallel affinities: None.
   - Output artifact: AI API assumptions list + open questions.
2. **Define system knowledge base payload**
   - Description: Specify required system info, constraints, decision metrics, and historic schedule context to pass as a knowledge base.
   - Preconditions: Story 1, Microtask 1.
   - Parallel affinities: Metrics design (Story 3).
   - Output artifact: Knowledge base schema draft.
3. **TOON request + JSON response specification**
   - Description: Define `.toon` schedule input format sent from system to the agent and `.json` response format for AI-produced schedules and metadata.
   - Preconditions: Story 1, Microtask 2.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: TOON request schema section + JSON response schema section.
4. **Define communication protocol & instructions**
   - Description: Specify request/response flow, retries, timeouts, and agent execution instructions (direct prompting with `.toon` input; response strictly `.json`).
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Protocol flow chart and error taxonomy.
5. **GDPR/data minimization review**
   - Description: Ensure only necessary doctor data and scheduling constraints are transmitted; define redaction rules.
   - Preconditions: Microtasks 2–4.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Data minimization checklist.

---

## Story 3 — Metrics & Decision Framework (GQM+Strategy Integration)
**Goal**: Integrate the provided GQM+Strategy analysis into the decision metrics, define the multidimensional priority scale, and specify the decision algorithm for comparing 4 schedules (1 standard + 3 AI).

**Functional Scope**
- Integrate GQM+Strategy metrics and measurement plan into scheduler comparison.
- Define a **multidimensional priority scale** and decision algorithm to select among the 4 schedules.
- Model for summarizing schedule comparison data and selection rationale.

**Estimated Effort**: **14 hours** (7 microtasks × 2h)

**Dependencies**
- Story 1 (constraints + data model).
- Story 2 (knowledge base + AI protocol impacts).

**Expected Deliverables**
- Decision metrics catalog aligned with GQM+Strategy levels (business/software/operational).
- Priority scale and decision algorithm specification.
- API contract draft for comparison and selection.

**Sprint Timing**: **Early sprint → before mid-sprint** (so UI can target final metrics).

**Microtasks (2h each)**
1. **Integrate GQM+Strategy metrics into planner metrics list**
   - Description: Translate Business/Software/Operational goals into planner-visible comparison metrics.
   - Preconditions: Story 1.
   - Parallel affinities: None.
   - Output artifact: GQM-aligned metrics list + definitions.
2. **Map data sources for each metric**
   - Description: Map required data (constraints, preferences, shift assignments, feedback) to each GQM metric.
   - Preconditions: Microtask 1.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Data-to-metric matrix.
3. **Define aggregation + normalization rules**
   - Description: Specify formulas, scaling, and normalization for each metric.
   - Preconditions: Microtasks 1–2.
   - Parallel affinities: None.
   - Output artifact: Metric calculation spec.
4. **Define multidimensional priority scale**
   - Description: Establish weighting/priority rules across metrics (e.g., trade-offs between fairness, coverage, burnout).
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: None.
   - Output artifact: Priority scale definition.
5. **Decision workshop: weights and trade-offs**
   - Description: Facilitate stakeholder decision-making to set metric weights/priority ranges and tie-break rules.
   - Preconditions: Microtasks 1–4.
   - Parallel affinities: None.
   - Output artifact: Signed-off weighting matrix + trade-off rules.
6. **Define decision algorithm**
   - Description: Specify the algorithm that chooses the preferred schedule based on the agreed priority scale.
   - Preconditions: Microtask 5.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Decision algorithm spec.
7. **Design comparison payload + audit/validation checklist**
   - Description: Draft the backend response schema for 4 schedules and define selection audit + metrics QA checks.
   - Preconditions: Microtasks 1–6.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: JSON schema outline + selection audit/metrics QA checklist.

---

## Story 4 — Planner UI: Schedule Comparison & Selection
**Goal**: Design the planner UI experience for comparing 4 schedules with decision metrics and selecting a preferred schedule.

**Functional Scope**
- Loading modal for schedule generation.
- Display of **only decision metrics** for each schedule.
- Schedule selection buttons + confirmation pop-up.
- Reuse existing success message patterns.

**Estimated Effort**: **10 hours** (5 microtasks × 2h)

**Dependencies**
- Story 3 (metrics schema).
- Story 1 (current UI surfaces).

**Expected Deliverables**
- UI wireframes or flow diagram.
- UI component breakdown (data bindings, states, error cases).

**Sprint Timing**: **Before mid-sprint** (so UI concept can be demoed).

**Microtasks (2h each)**
1. **Define UI states & flows**
   - Description: Outline states for loading, success, failure, and partial schedules.
   - Preconditions: Story 1.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: UI state diagram.
2. **Design comparison layout**
   - Description: Layout four schedule cards showing decision metrics only.
   - Preconditions: Story 3, Microtask 4.
   - Parallel affinities: None.
   - Output artifact: Wireframe or annotated layout.
3. **Design selection + confirmation modal**
   - Description: Define confirmation copy and selection behaviors.
   - Preconditions: Microtask 2.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Modal copy + interaction spec.
4. **Define success message reuse**
   - Description: Specify how existing success notification patterns are reused.
   - Preconditions: Story 1 UI inventory.
   - Parallel affinities: None.
   - Output artifact: Success state spec.
5. **UI error + fallback design**
   - Description: Define error handling for missing metrics or AI failures.
   - Preconditions: Story 2 protocol.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Error UX checklist.

---

## Story 5 — Backend Orchestration Plan for AI Rescheduling
**Goal**: Define backend orchestration steps for generating 1 standard + 3 AI schedules, computing metrics, and exposing comparison + selection endpoints.

**Functional Scope**
- Generation pipeline orchestration and sequencing.
- AI schedule ingestion using **TOON input** and **JSON output** parsing/unmarshalling.
- Metrics computation and storage.
- Selection endpoint behavior.

**Estimated Effort**: **12 hours** (6 microtasks × 2h)

**Dependencies**
- Story 2 (AI protocol + TOON).
- Story 3 (metrics spec).

**Expected Deliverables**
- Backend orchestration plan with endpoint list and sequence diagram.
- Error handling + fallback strategy.

**Sprint Timing**: **Before mid-sprint** (to enable demoable architecture).

**Microtasks (2h each)**
1. **Define orchestration sequence**
   - Description: Sequence steps to generate standard + AI schedules and compute metrics.
   - Preconditions: Stories 2–3.
   - Parallel affinities: UI state design (Story 4).
   - Output artifact: Sequence diagram.
2. **TOON request ingestion + validation plan**
   - Description: Define how `.toon` requests are generated, parsed, and validated before AI calls.
   - Preconditions: Story 2.
   - Parallel affinities: None.
   - Output artifact: TOON parsing/validation checklist.
3. **JSON response ingestion + validation plan**
   - Description: Define how `.json` AI schedules are parsed, validated, and converted to internal models.
   - Preconditions: Story 2.
   - Parallel affinities: None.
   - Output artifact: JSON parsing/validation checklist.
4. **Comparison + selection API contract**
   - Description: Define endpoints for fetching comparison metrics and committing planner selection.
   - Preconditions: Story 3, Microtask 7.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: Endpoint spec draft.
5. **Error + retry strategy**
   - Description: Define failure modes for AI calls, metrics computation, and schedule parsing.
   - Preconditions: Story 2 protocol.
   - Parallel affinities: UI error design (Story 4).
   - Output artifact: Failure matrix.
6. **Security + audit considerations**
   - Description: Define audit logging for schedule selection and AI decisions.
   - Preconditions: Story 3, Microtask 7.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Audit log plan.

---

## Story 6 — Documentation & Stakeholder Materials
**Goal**: Produce documentation, sprint summary materials, and stakeholder presentation assets.

**Functional Scope**
- Technical documentation for new flows, AI integration, and metrics logic.
- Sprint documentation (what was done, decisions made).
- Slides for stakeholder presentation.
- Retro input artifacts.

**Estimated Effort**: **8 hours** (4 microtasks × 2h)

**Dependencies**
- Stories 1–5 (for accurate content).

**Expected Deliverables**
- Technical docs for AI rescheduling flows.
- Sprint summary + retrospective input.
- Stakeholder slide deck.

**Sprint Timing**: **After mid-sprint → end of sprint** (final packaging + review).

**Microtasks (2h each)**
1. **Technical documentation draft**
   - Description: Document new flow diagrams, AI protocol, and metrics logic.
   - Preconditions: Stories 2–5.
   - Parallel affinities: None.
   - Output artifact: Technical doc draft.
2. **Sprint summary + decisions log**
   - Description: Write a sprint summary and major decision record.
   - Preconditions: Story 1–5.
   - Parallel affinities: None.
   - Output artifact: Sprint summary doc.
3. **Stakeholder slide deck**
   - Description: Create slides covering problem, approach, and progress.
   - Preconditions: Story 4 design and Story 5 orchestration plan.
   - Parallel affinities: None.
   - Output artifact: Slide deck outline or draft.
4. **Retrospective inputs**
   - Description: Capture what went well, what didn’t, and risks for next sprint.
   - Preconditions: Story 6 Microtasks 1–3.
   - Parallel affinities: None.
   - Output artifact: Retro notes.

---

## Cross-Story Dependency Highlights
- **Story 1 → Stories 2–5**: Baseline scheduling flow informs AI integration, metrics design, UI, and orchestration.
- **Story 2 → Stories 3–5**: AI protocol and TOON specification drive metrics inputs and backend orchestration.
- **Story 3 → Story 4**: UI depends on finalized decision metrics and comparison payload schema.
- **Story 5 → Story 4**: Error/timeout behavior influences UI loading and failure states.
- **Stories 1–5 → Story 6**: Documentation and stakeholder materials depend on completed analysis/design outputs.

---

## Timeline View (Narrative)
- **Week 1 (Early sprint)**: Complete Story 1. Start Story 2 (AI protocol) and Story 3 (metrics). Begin Story 4 UI concepting in parallel once metrics schema is drafted.
- **Week 2 (Before mid-sprint meeting)**: Finalize Stories 2–5 design outputs (including TOON/JSON protocol and decision algorithm) so that UI wireframes and backend orchestration plan can be demoed.
- **After mid-sprint alignment (Week 2)**: Focus on Story 6 documentation + stakeholder materials using finalized artifacts.

---

## Expected Outcomes
### Mid-sprint (Alignment Meeting)
- Baseline flow map (Story 1).
- AI communication + TOON protocol draft (Story 2).
- Decision metrics list + comparison schema (Story 3).
- UI wireframes + selection flow (Story 4).
- Backend orchestration plan (Story 5).

### End of Sprint (Review + Retro)
- Full technical documentation, sprint summary, and stakeholder slide deck (Story 6).
- Consolidated roadmap with dependency-consistent story and microtask plan.

---

## Effort Summary
- Story 1: 6h
- Story 2: 10h
- Story 3: 14h
- Story 4: 10h
- Story 5: 12h
- Story 6: 8h
- **Total**: **60 hours**
