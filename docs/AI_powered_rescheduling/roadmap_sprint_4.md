# Sprint 4 Roadmap — AI-Powered Rescheduling (2-Week Sprint)

## Sprint Overview & Assumptions
- **Team capacity**: 5 people × ~12 hours = **60 total hours** for the sprint.
- **Sprint cadence**: 2 weeks, with a **mid-sprint alignment meeting** on Monday of week 2 and **sprint review + retro** the following Monday.
- **Scope**: Analysis + planning for AI-powered rescheduling with backend + frontend + AI-agent communication + metrics + documentation + stakeholder materials.
- **Code change policy for this sprint roadmap**: Planning only; no implementation included here.
- **Key existing flow anchors**: Schedule generation/regeneration uses `/api/schedule/generation` and `/api/schedule/regeneration` in the backend and frontend schedule generator view. The current system generates schedules from the planner UI with a loading overlay and a list of schedules, but has **no AI integration or schedule comparison UI** yet.

### PROMPT FOR THE USER
1. What AI agent APIs (vendor + endpoints) are approved for use, and do we have existing credentials or SDKs?
2. Do we have an official **TOON** protocol spec (schemas, examples) that must be followed for `.toon` scheduling and feedback files?
3. What are the **decision metrics** to display and how are they computed/weighted (e.g., fairness, coverage, overtime, constraint violations)?
4. Should the AI rescheduling flow be **planner-only** or also available to configurators/admins?
5. Are there **non-functional constraints** (latency, max inference cost) for AI schedule generation?

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

## Story 2 — AI Agent Communication & TOON Protocol Design
**Goal**: Define the AI agent communication protocol, context delivery strategy, and TOON artifacts used for scheduling and feedback.

**Functional Scope**
- Identify AI agent APIs and integration constraints.
- Define **system knowledge** payloads to pass as **context/knowledge base** (not prompt).
- Define TOON-based communication protocol and `.toon` file contract boundaries.

**Estimated Effort**: **10 hours** (5 microtasks × 2h)

**Dependencies**
- Story 1 (baseline flow understanding).

**Expected Deliverables**
- AI communication specification (context structure, TOON file schemas, error handling).
- Security + privacy considerations for data transfer (GDPR data minimization).

**Sprint Timing**: **Early sprint → before mid-sprint** (enables backend + UI work).

**Microtasks (2h each)**
1. **AI API inventory and constraints capture**
   - Description: Identify target AI agent APIs, response latency, payload limits, and supported formats.
   - Preconditions: Story 1 complete.
   - Parallel affinities: None.
   - Output artifact: AI API assumptions list + open questions.
2. **Define system knowledge base payload**
   - Description: Specify required system info, constraints, decision metrics, and historic schedule context to pass as a knowledge base.
   - Preconditions: Story 1, Microtask 1.
   - Parallel affinities: Backend metrics design (Story 3).
   - Output artifact: Knowledge base schema draft.
3. **TOON scheduling file specification**
   - Description: Define `.toon` schedule input format for AI and feedback format for evaluation loop.
   - Preconditions: User-provided TOON spec (or prompt for user if missing).
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: TOON scheduling/feedback schema section in documentation.
4. **Define communication protocol & instructions**
   - Description: Specify request/response flow, retries, timeouts, and agent execution instructions (prompt limited to `.toon` files only).
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Protocol flow chart and error taxonomy.
5. **GDPR/data minimization review**
   - Description: Ensure only necessary doctor data and scheduling constraints are transmitted; define redaction rules.
   - Preconditions: Microtasks 2–4.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Data minimization checklist.

---

## Story 3 — Metrics Definition & Schedule Comparison Model
**Goal**: Define decision metrics for comparing 4 schedules (1 standard + 3 AI), and the backend data model required to compute and deliver those metrics.

**Functional Scope**
- Metric definitions (fairness, coverage, constraint violations, overtime, preference compliance, etc.).
- Model for summarizing schedule comparison data.
- API contract for fetching comparison metrics and schedule metadata.

**Estimated Effort**: **12 hours** (6 microtasks × 2h)

**Dependencies**
- Story 1 (constraints + data model).
- Story 2 (knowledge base + AI protocol impacts).

**Expected Deliverables**
- Decision metrics catalog with formulas and output schema.
- API contract draft for comparison and selection.

**Sprint Timing**: **Early sprint → before mid-sprint** (so UI can target final metrics).

**Microtasks (2h each)**
1. **Define decision metrics list**
   - Description: Identify the minimal, planner-facing metrics to compare schedules.
   - Preconditions: Story 1.
   - Parallel affinities: None.
   - Output artifact: Metrics list + definitions.
2. **Map data sources for each metric**
   - Description: Map required data (constraints, preferences, shift assignments) to each metric.
   - Preconditions: Microtask 1.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Data-to-metric matrix.
3. **Define aggregation + normalization rules**
   - Description: Specify formulas, scaling, and normalization to enable side-by-side comparison.
   - Preconditions: Microtasks 1–2.
   - Parallel affinities: None.
   - Output artifact: Metric calculation spec.
4. **Design comparison payload schema**
   - Description: Draft a backend response schema for 4 schedules and their metrics.
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: JSON schema outline.
5. **Define selection logging + audit data**
   - Description: Specify what to store when a planner selects a schedule (who, why, metrics snapshot).
   - Preconditions: Microtask 4.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Selection audit requirements.
6. **Metrics validation checklist**
   - Description: Define sanity checks and failure conditions for metrics computation.
   - Preconditions: Microtasks 1–4.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Metrics QA checklist.

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
- AI schedule ingestion (TOON file parsing/unmarshalling).
- Metrics computation and storage.
- Selection endpoint behavior.

**Estimated Effort**: **10 hours** (5 microtasks × 2h)

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
2. **TOON ingestion + validation plan**
   - Description: Define how `.toon` schedules and feedback files are parsed and validated.
   - Preconditions: Story 2.
   - Parallel affinities: None.
   - Output artifact: Parsing/validation checklist.
3. **Comparison + selection API contract**
   - Description: Define endpoints for fetching comparison metrics and committing planner selection.
   - Preconditions: Story 3, Microtask 4.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: Endpoint spec draft.
4. **Error + retry strategy**
   - Description: Define failure modes for AI calls, metrics computation, and schedule parsing.
   - Preconditions: Story 2 protocol.
   - Parallel affinities: UI error design (Story 4).
   - Output artifact: Failure matrix.
5. **Security + audit considerations**
   - Description: Define audit logging for schedule selection and AI decisions.
   - Preconditions: Story 3, Microtask 5.
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
- **Week 2 (Before mid-sprint meeting)**: Finalize Stories 2–5 design outputs so that UI wireframes and backend orchestration plan can be demoed.
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
- Story 3: 12h
- Story 4: 10h
- Story 5: 10h
- Story 6: 8h
- **Total**: **56 hours** (≤ 60-hour sprint capacity)
